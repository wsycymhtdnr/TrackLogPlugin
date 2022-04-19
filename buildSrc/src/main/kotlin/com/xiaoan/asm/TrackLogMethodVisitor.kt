package com.xiaoan.asm

import com.xiaoan.beans.TrackEventBean
import com.xiaoan.const.AnnotationConstants
import com.xiaoan.const.AnnotationConstants.KEY
import com.xiaoan.const.AnnotationConstants.VALUE
import com.xiaoan.const.AnnotationConstants.IS_SHARED
import com.xiaoan.const.AnnotationConstants.FIXED_ATTRIBUTE
import com.xiaoan.const.AnnotationConstants.TRACK_EVENT
import com.xiaoan.const.AnnotationConstants.LOCAL_VARIABLE_ATTRIBUTE
import com.xiaoan.const.AnnotationConstants.FIXED_ATTRIBUTES
import com.xiaoan.const.AnnotationConstants.PARAMETER_ATTRIBUTE
import com.xiaoan.const.AnnotationConstants.RETURN_ATTRIBUTE
import org.objectweb.asm.*
import org.objectweb.asm.commons.AdviceAdapter
import java.lang.IllegalArgumentException

/**
 * @Author liyunfei
 * @Description 访问方法的信息，并且插入埋点代码， AOP Advice概念
 * @Date 2022/4/11 15:52
 */
class TrackLogMethodVisitor(
    private val classAttributes: TrackEventBean, methodVisitor: MethodVisitor?, access: Int,
    name: String?, descriptor: String?,
) : AdviceAdapter(Opcodes.ASM7, methodVisitor, access, name, descriptor) {
    // 是否含有@TraceEvent注解
    private var hasTraceEvent = false
    // class的@TraceEvent注解filters参数大小,解析完方法注解后加上方法注解filters
    private var filterSize = classAttributes.filters.size
    // class的@TraceEvent注解name,解析完方法注解后加上方法注解name
    private var trackEventName = classAttributes.name
    // class的@TraceEvent注解filters参数,解析完方法注解后加上方法注解filters
    private var filters = classAttributes.filters
    // filters数组的标志符
    private var filtersId :Int = 0
    // 私有的参数
    private var attributes: MutableMap<String, Any> = mutableMapOf()
    // 公共的参数
    private var shardAttributes: MutableMap<String, Any> = mutableMapOf()

    // 用于构建埋点属性的参数
    private var key = "default_key"
    private var value = "default_value"
    private var isShared = false

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        return if (descriptor == TRACK_EVENT || descriptor == FIXED_ATTRIBUTE ||descriptor == FIXED_ATTRIBUTES) {
            if(descriptor == TRACK_EVENT) hasTraceEvent = true
            object: AnnotationVisitor(Opcodes.ASM7, super.visitAnnotation(descriptor, visible)) {
                override fun visitEnd() {
                    super.visitEnd()
                    if (key!="default_key" && value!="default_value")
                    if (isShared) {
                        shardAttributes[key] = value
                    } else {
                        attributes[key] = value
                    }
                }

                override fun visitArray(name: String?): AnnotationVisitor {
                    return object: AnnotationVisitor(Opcodes.ASM7, super.visitArray(name)){
                        override fun visitAnnotation(
                            name: String?,
                            descriptor: String?
                        ): AnnotationVisitor {
                            return object : AnnotationVisitor(Opcodes.ASM7, super.visitAnnotation(name, descriptor)){
                                override fun visit(name: String?, value: Any?) {
                                    super.visit(name, value)
                                    println(name + "-----" + value)
                                }
                            }
                        }
                    }
                }

                override fun visit(name: String?, value: Any?) {
                    println("3" + descriptor)
                    super.visit(name, value)
                    when (descriptor) {
                        TRACK_EVENT -> handleTrackEvent(name, value)
                        FIXED_ATTRIBUTE -> handleFixedAttribute(name, value)
                        //FIXED_ATTRIBUTES -> handleFixedAttributes(name, value)
                        LOCAL_VARIABLE_ATTRIBUTE -> {}
                        PARAMETER_ATTRIBUTE -> {}
                        RETURN_ATTRIBUTE -> {}
                    }
                }
            }
        } else {
            super.visitAnnotation(descriptor, visible)
        }
    }

    // 处理@FixedAttributes注解,kotlin1.6之前不支持可重复注解
    private fun handleFixedAttributes(name: String?, value: Any?) {
        println("$name ---$value")
//        (value as Array<*>).forEach {
//            it as FixedAttribute
//            if (it.isShared) {
//                shardAttributes[it.key] = it.value
//            } else {
//                attributes[it.key] = it.value
//            }
//        }
    }

    // 处理@TFixedAttribute注解
    private fun handleFixedAttribute(name: String?, value: Any?) {
        when (name) {
            KEY -> {
                if (attributes.keys.contains(value.toString()) || shardAttributes.keys.contains(value.toString())) {
                    throw IllegalArgumentException("已经存在相同的key:---$value.toString()")
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
            print("attribute---$it")
        }
        shardAttributes.forEach {
            print("shardAttributes---$it")
        }
        generateTrackEventBeanInsn()

    }

    // 生成rackEventBean的相关指令
    private fun generateTrackEventBeanInsn() {
        // 创建int数组并且储存到局部变量表中
        filtersId = newLocal(Type.INT_TYPE)
        mv.visitIntInsn(SIPUSH, filterSize)
        mv.visitIntInsn(NEWARRAY, T_INT)
        initFilters()
        mv.visitVarInsn(ASTORE, filtersId)

        mv.visitTypeInsn(NEW, "com/xiaoan/tracklog/beans/TrackEventBean")
        mv.visitInsn(DUP)
        mv.visitLdcInsn(trackEventName)
        mv.visitVarInsn(ALOAD, filtersId)
        mv.visitMethodInsn(INVOKESPECIAL,
            "com/xiaoan/tracklog/beans/TrackEventBean",
            "<init>",
            "(Ljava/lang/String;[I)V",
            false)
        //TODO object类型局部变量处理
        val trackEventBeanId = filtersId + 1
        mv.visitVarInsn(ASTORE, trackEventBeanId)

        mv.visitInsn(ICONST_0)
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        mv.visitVarInsn(ALOAD, trackEventBeanId)
        mv.visitMethodInsn(INVOKEVIRTUAL,
            "java/io/PrintStream",
            "println",
            "(Ljava/lang/Object;)V",
            false)
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