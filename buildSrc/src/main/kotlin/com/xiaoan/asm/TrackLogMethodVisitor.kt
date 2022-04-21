package com.xiaoan.asm

import com.sun.xml.fastinfoset.util.StringArray
import com.xiaoan.beans.TrackEventBean
import com.xiaoan.const.AnnotationConstants
import com.xiaoan.const.AnnotationConstants.FIXED_ATTRIBUTE
import com.xiaoan.const.AnnotationConstants.FIXED_ATTRIBUTES
import com.xiaoan.const.AnnotationConstants.IS_SHARED
import com.xiaoan.const.AnnotationConstants.KEY
import com.xiaoan.const.AnnotationConstants.PARAMETER_ATTRIBUTE
import com.xiaoan.const.AnnotationConstants.RETURN_ATTRIBUTE
import com.xiaoan.const.AnnotationConstants.TRACK_EVENT
import com.xiaoan.const.AnnotationConstants.VALUE
import com.xiaoan.utils.ReturnAttributeUtils.printInt
import com.xiaoan.utils.ReturnAttributeUtils.printObject
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter

/**
 * @Author liyunfei
 * @Description 访问方法的信息，并且插入埋点代码， AOP Advice概念
 * @Date 2022/4/11 15:52
 */
class TrackLogMethodVisitor(classAttributes: TrackEventBean, methodVisitor: MethodVisitor?, access: Int,
    methodName: String?, descriptor: String?,
) : AdviceAdapter(Opcodes.ASM7, methodVisitor, access, methodName, descriptor) {
    // 是否含有@TraceEvent注解
    private var hasTraceEvent = false
    // class的@TraceEvent注解filters参数大小,解析完方法注解后加上方法注解filters
    private var filterSize = classAttributes.filters.size
    // class的@TraceEvent注解name,解析完方法注解后加上方法注解name
    private var trackEventName = classAttributes.name
    // class的@TraceEvent注解filters参数,解析完方法注解后加上方法注解filters
    private var filters = classAttributes.filters
    // trackLog对象在局部变量中的位置
    private var trackLogIndex :Int = 0
    // attributes对象在局部变量中的位置
    private var attributesIndex :Int = 0
    // sharedAttributes对象在局部变量中的位置
    private var sharedAttributesIndex :Int = 0
    // 私有的参数
    private var attributes: MutableMap<String, Any> = mutableMapOf()
    // 公共的参数
    private var sharedAttributes: MutableMap<String, Any> = mutableMapOf()
    // @ParameterAttribute注解入参在局部变量表上的位置
    private var parameterIndexes = intArrayOf()
    // @ParameterAttribute注解入参name
    private var parameterNames = StringArray()
    // 是否有@ReturnAnnotation注解
    private var hasReturnAnnotation = false
    // @ReturnAnnotation注解key
    private var returnKey = "return_key"

    private var isReturnShared = false

    // 用于构建埋点属性的参数
    private var key = "default_key"
    private var value = "default_value"
    private var isShared = false

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        return if (descriptor == TRACK_EVENT || descriptor == FIXED_ATTRIBUTE
            || descriptor == FIXED_ATTRIBUTES || descriptor == RETURN_ATTRIBUTE) {
            if(descriptor == TRACK_EVENT) hasTraceEvent = true
            if (descriptor == RETURN_ATTRIBUTE) hasReturnAnnotation = true
            object: AnnotationVisitor(Opcodes.ASM7, super.visitAnnotation(descriptor, visible)) {
                override fun visitEnd() {
                    super.visitEnd()
                    if (key!="default_key" && value!="default_value")
                    if (isShared) {
                        sharedAttributes[key] = value
                    } else {
                        attributes[key] = value
                    }
                }

                // 通过visitArray()来获取@FixedAttribute注解
                override fun visitArray(name: String?): AnnotationVisitor {
                    return object: AnnotationVisitor(Opcodes.ASM7, super.visitArray(name)){
                        override fun visitAnnotation(
                            name: String?,
                            descriptor: String?
                        ): AnnotationVisitor {
                            return object : AnnotationVisitor(Opcodes.ASM7, super.visitAnnotation(name, descriptor)){
                                override fun visit(name: String?, value: Any?) {
                                    super.visit(name, value)
                                    if (descriptor == FIXED_ATTRIBUTE) {
                                        handleFixedAttribute(name, value)
                                    }
                                }

                                override fun visitEnd() {
                                    super.visitEnd()
                                    if (key!="default_key" && value!="default_value")
                                        if (isShared) {
                                            sharedAttributes[key] = value
                                        } else {
                                            attributes[key] = value
                                        }
                                }
                            }
                        }
                    }
                }

                override fun visit(name: String?, value: Any?) {
                    super.visit(name, value)
                    when (descriptor) {
                        TRACK_EVENT -> handleTrackEvent(name, value)
                        // TODO 暂时不支持
                        // FIXED_ATTRIBUTE -> handleFixedAttribute(name, value)
                        // LOCAL_VARIABLE_ATTRIBUTE -> {}
                        RETURN_ATTRIBUTE -> handleReturnAttribute(name, value)
                    }
                }
            }
        } else {
            super.visitAnnotation(descriptor, visible)
        }
    }

    private fun handleReturnAttribute(name: String?, value: Any?) {
        when (name) {
            KEY -> {
                if (attributes.keys.contains(value.toString()) || sharedAttributes.keys.contains(value.toString())) {
                    throw IllegalArgumentException("已经存在相同的key:---${value.toString()},请检查相关埋点注解")
                }
                returnKey = value.toString()
            }
            IS_SHARED -> isReturnShared = value as Boolean
        }
    }

    // 访问方法入参的注解
    override fun visitParameterAnnotation(parameter: Int, descriptor: String?, visible: Boolean): AnnotationVisitor {
        return if (descriptor != PARAMETER_ATTRIBUTE) {
            super.visitParameterAnnotation(parameter, descriptor, visible)
        } else {

            object: AnnotationVisitor(Opcodes.ASM7, super.visitParameterAnnotation(parameter, descriptor, visible)){
                override fun visit(name: String?, value: Any?) {
                    super.visit(name, value)
                    handleParameterAttribute(name, value)
                }

                override fun visitEnd() {
                    super.visitEnd()
                    if (key!="default_key" && value!="default_value") {
                        // 非静态方法第一个参数是this
                        parameterIndexes = parameterIndexes.plus(parameter + 1)
                        parameterNames.add(key)
                        if (isShared) {
                            sharedAttributes[key] = value
                        } else {
                            attributes[key] = value
                        }
                    }
                }
            }
        }
    }

    override fun visitInsn(opcode: Int) {
        // 首先，处理自己的代码逻辑
        if (opcode >= IRETURN && opcode <= RETURN || opcode == ATHROW) {
            //printMessage("Method Exit:")
            if (opcode == IRETURN) {
                super.visitInsn(DUP)
                printInt(mv)
            } else if (opcode == FRETURN) {
                super.visitInsn(DUP)
                //printFloat()
            } else if (opcode == LRETURN) {
                super.visitInsn(DUP2)
                //printLong()
            } else if (opcode == DRETURN) {
                super.visitInsn(DUP2)
                //printDouble()
            } else if (opcode == ARETURN) {
                super.visitInsn(DUP)
                printObject(this, returnKey, if(isReturnShared) attributesIndex else sharedAttributesIndex)
            } else if (opcode == RETURN) {
                //printMessage("    return void")
            } else {
                //printMessage("    abnormal return")
            }
        }

        // 其次，调用父类的方法实现
        super.visitInsn(opcode)
    }

    // 处理@ParameterAttribute注解，kotlin1.6之前不支持可重复注解
    private fun handleParameterAttribute(name: String?, value: Any?) {
        when (name) {
            KEY -> {
                if (attributes.keys.contains(value.toString()) || sharedAttributes.keys.contains(value.toString())) {
                    throw IllegalArgumentException("已经存在相同的key:---${value.toString()},请检查相关埋点注解")
                }
                key = value.toString()
            }
            IS_SHARED -> isShared = value as Boolean
        }
        this.value = "default_parameter_value"
    }

    // 处理@FixedAttribute注解，kotlin1.6之前不支持可重复注解
    private fun handleFixedAttribute(name: String?, value: Any?) {
        when (name) {
            KEY -> {
                if (attributes.keys.contains(value.toString()) || sharedAttributes.keys.contains(value.toString())) {
                    throw IllegalArgumentException("已经存在相同的key:---${value.toString()},请检查相关埋点注解")
                }
                key = value.toString()
            }
            VALUE -> this.value = value.toString()
            IS_SHARED -> isShared = value as Boolean
        }
    }

    // 处理@TrackEvent注解
    private fun handleTrackEvent(name: String?, value: Any?) {
        when (name) {
            AnnotationConstants.NAME -> trackEventName = trackEventName.plus("_").plus(value.toString())
            AnnotationConstants.FILTERS -> {
                val methodFilters = value as IntArray
                filters = filters.plus(methodFilters).distinct().toIntArray()
                filterSize = filters.size
            }
        }
    }

//TODO kotlin编译器会丢弃掉局部变量注解，暂不支持
//region
//    override fun visitLocalVariableAnnotation(
//        typeRef: Int, typePath: TypePath?, start: Array<out Label>?,
//        end: Array<out Label>?, index: IntArray?, descriptor: String?, visible: Boolean,
//    ): AnnotationVisitor {
//        println("$name---$descriptor---$index")
//        return TrackLogMethodLocalVarAnnotationVisitor(descriptor, Opcodes.ASM7,
//            super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, descriptor, visible))
//    }
//
//    override fun visitLocalVariable(
//        name: String?, descriptor: String?, signature: String?,
//        start: Label?, end: Label?, index: Int,
//    ) {
//        if (hasTraceEvent) {
//            println("$name---$descriptor---$index---$start---$end")
//        }
//
//        super.visitLocalVariable(name, descriptor, signature, start, end, index)
//    }
//endregion

    override fun onMethodEnter() {
        super.onMethodEnter()
        if (!hasTraceEvent) {
            return
        }

        attributes.forEach {
            println("attribute---$it")
        }
        sharedAttributes.forEach {
            println("shardAttributes---$it")
        }
        println(parameterIndexes.size)
        println(parameterNames.size)
        generateTrackEventBeanInsn()
        generateShardAttributesInsn()
        generateAttributesInsn()
        assignParameterAttribute()
    }

    override fun onMethodExit(opcode: Int) {
        if (!hasTraceEvent) {
            return
        }
        super.onMethodExit(opcode)
        generateAddSharedAttribute()
        generateSendEventInsn()
    }

    // 添加公共参数
    private fun generateAddSharedAttribute() {
        mv.visitFieldInsn(GETSTATIC, "com/xiaoan/tracklog/runtime/TrackLogManager", "INSTANCE", "Lcom/xiaoan/tracklog/runtime/TrackLogManager;")
        mv.visitVarInsn(ALOAD, sharedAttributesIndex)
        mv.visitMethodInsn(INVOKEVIRTUAL, "com/xiaoan/tracklog/runtime/TrackLogManager", "addSharedAttribute", "(Ljava/util/Map;)V", false)
    }

    // 给@ParameterAttribute注解的参数赋值， visitParameterAnnotation()方法只给了一个默认值
    private fun assignParameterAttribute() {
        parameterIndexes.forEachIndexed { index, value ->
            val key = parameterNames[index]
            traverseAttributesMapAndModify(attributes, key, value, attributesIndex)
            traverseAttributesMapAndModify(sharedAttributes, key, value, sharedAttributesIndex)
        }
    }

    // 生成公共的attributes变量相关的指令
    private fun generateShardAttributesInsn() {
        sharedAttributesIndex = newLocal(Type.INT_TYPE)
        mv.visitTypeInsn(NEW, "java/util/LinkedHashMap")
        mv.visitInsn(DUP)
        mv.visitMethodInsn(INVOKESPECIAL, "java/util/LinkedHashMap", "<init>", "()V", false)
        mv.visitTypeInsn(CHECKCAST, "java/util/Map")
        mv.visitVarInsn(ASTORE, sharedAttributesIndex)
        mv.visitVarInsn(ALOAD, sharedAttributesIndex)
        traverseAttributesMap(sharedAttributes, sharedAttributesIndex)
        mv.visitInsn(POP)
    }

    // 生成私有的attributes相关的指令
    private fun generateAttributesInsn() {
        //attributes Map在局部变量表的index
        attributesIndex = newLocal(Type.INT_TYPE)
        mv.visitTypeInsn(NEW, "java/util/LinkedHashMap")
        mv.visitInsn(DUP)
        mv.visitMethodInsn(INVOKESPECIAL, "java/util/LinkedHashMap", "<init>", "()V", false)
        mv.visitTypeInsn(CHECKCAST, "java/util/Map")
        mv.visitVarInsn(ASTORE, attributesIndex)
        traverseAttributesMap(attributes, attributesIndex)
        mv.visitInsn(POP)
    }

    private fun traverseAttributesMap(map :MutableMap<String, Any>, index: Int) {
        map.forEach {
            mv.visitVarInsn(ALOAD, index)
            mv.visitLdcInsn(it.key)
            mv.visitVarInsn(ASTORE, index+1)
            mv.visitLdcInsn(it.value)
            mv.visitVarInsn(ASTORE, index+2)
            mv.visitVarInsn(ALOAD, index+1)
            mv.visitVarInsn(ALOAD, index+2)
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true)
        }
    }

    /**
     * Traverse attributes map and modify
     *
     * @param map 参数集合 attribute sharedAttribute 私有参数和共享参数
     * @param modifyKey 被修改参数的key
     * @param modifyValueIndex 被修改参数的value对应在本地变量表的位置
     * @param index 参数集合在本地变量表的位置 (attribute sharedAttribute)
     */
    private fun traverseAttributesMapAndModify(map: MutableMap<String, Any>, modifyKey: String, modifyValueIndex: Int, index: Int) {
        map.forEach {
            if (modifyKey == it.key) {
                mv.visitVarInsn(ALOAD, index)
                mv.visitLdcInsn(it.key)
                mv.visitVarInsn(ASTORE, index+1)
                mv.visitVarInsn(ALOAD, index+1)
                mv.visitVarInsn(ALOAD, modifyValueIndex)
                mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true)
            }
          }
        }

    // 生成发送埋点事件的指令
    private fun generateSendEventInsn() {
        mv.visitFieldInsn(GETSTATIC, "com/xiaoan/tracklog/runtime/TrackLogManager", "INSTANCE", "Lcom/xiaoan/tracklog/runtime/TrackLogManager;")
        mv.visitVarInsn(ALOAD, trackLogIndex)
        mv.visitVarInsn(ALOAD, attributesIndex)
        mv.visitMethodInsn(INVOKEVIRTUAL, "com/xiaoan/tracklog/runtime/TrackLogManager", "sendEvent", "(Lcom/xiaoan/tracklog/beans/TrackEventBean;Ljava/util/Map;)V", false)
    }

    // 生成rackEventBean的相关指令
    private fun generateTrackEventBeanInsn() {
        // 创建int数组并且储存到局部变量表中
        val filtersIndex = newLocal(Type.INT_TYPE)
        mv.visitIntInsn(SIPUSH, filterSize)
        mv.visitIntInsn(NEWARRAY, T_INT)
        initFilters()
        mv.visitVarInsn(ASTORE, filtersIndex)

        mv.visitTypeInsn(NEW, "com/xiaoan/tracklog/beans/TrackEventBean")
        mv.visitInsn(DUP)
        mv.visitLdcInsn(trackEventName)
        mv.visitVarInsn(ALOAD, filtersIndex)
        mv.visitMethodInsn(INVOKESPECIAL, "com/xiaoan/tracklog/beans/TrackEventBean", "<init>", "(Ljava/lang/String;[I)V", false)
        trackLogIndex = newLocal(Type.INT_TYPE)
        mv.visitVarInsn(ASTORE, trackLogIndex)
    }

    // 初始化filters数组
    private fun initFilters() {
        if (filterSize == 0) return
        for(index in 0 until filterSize) {
            mv.visitInsn(DUP)
            mv.visitIntInsn(SIPUSH, index)
            mv.visitIntInsn(SIPUSH, filters[index])
            mv.visitInsn(IASTORE)
        }
    }
}