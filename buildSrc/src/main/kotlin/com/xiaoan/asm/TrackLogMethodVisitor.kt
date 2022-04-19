package com.xiaoan.asm

import com.xiaoan.beans.TrackEventBean
import com.xiaoan.const.AnnotationConstants
import com.xiaoan.const.AnnotationConstants.TRACK_EVENT
import org.objectweb.asm.*
import org.objectweb.asm.commons.AdviceAdapter

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

    // 方法上每有一个注解就会调用一次该方法
    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        return if (descriptor == TRACK_EVENT) {
            hasTraceEvent = true
            object: AnnotationVisitor(Opcodes.ASM7, super.visitAnnotation(descriptor, visible)){
                override fun visit(name: String?, value: Any?) {
                    super.visit(name, value)
                    when (descriptor) {
                        TRACK_EVENT -> {
                            //println("TrackLogMethodAnnotationVisitor")
                            handleTrackEvent(name, value)
                        }
                        AnnotationConstants.FIXED_ATTRIBUTE -> {}
                        AnnotationConstants.LOCAL_VARIABLE_ATTRIBUTE -> {}
                        AnnotationConstants.PARAMETER_ATTRIBUTE -> {}
                        AnnotationConstants.RETURN_ATTRIBUTE -> {}
                    }
                    super.visit(name, value)
                }
            }
        } else {
            super.visitAnnotation(descriptor, visible)
        }
    }

    private fun handleTrackEvent(name: String?, value: Any?) {
        when (name) {
            "name" -> trackEventName.plus("_").plus(value.toString())
            "filters" -> {
                val methodFilters = value as IntArray
                filters = filters.plus(methodFilters)
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
        mv.visitMethodInsn(INVOKESPECIAL, "com/xiaoan/tracklog/beans/TrackEventBean", "<init>", "(Ljava/lang/String;[I)V", false)
        //TODO object类型局部变量处理
        val trackEventBeanId = filtersId + 1
        mv.visitVarInsn(ASTORE, trackEventBeanId)

        mv.visitInsn(ICONST_0)
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        mv.visitVarInsn(ALOAD, trackEventBeanId)
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/Object;)V", false)
        mv.visitInsn(RETURN)

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