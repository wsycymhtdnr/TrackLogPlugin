package com.xiaoan.asm

import com.xiaoan.beans.TrackEventBean
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
    // class的@TraceEvent注解filters参数大小
    private var filterSize = classAttributes.filters.size
    // filters数组的标志符
    private var filtersId :Int = 0

    // 方法上每有一个注解就会调用一次该方法
    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        return if (descriptor == TRACK_EVENT) {
            hasTraceEvent = true
            TrackLogMethodAnnotationVisitor(descriptor,Opcodes.ASM7, super.visitAnnotation(descriptor, visible))
        } else {
            super.visitAnnotation(descriptor, visible)
        }
    }

    override fun visitLocalVariableAnnotation(
        typeRef: Int, typePath: TypePath?, start: Array<out Label>?,
        end: Array<out Label>?, index: IntArray?, descriptor: String?, visible: Boolean,
    ): AnnotationVisitor {
        println("$name---$descriptor---$index")
        return TrackLogMethodLocalVarAnnotationVisitor(descriptor, Opcodes.ASM7,
            super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, descriptor, visible))
    }


    override fun visitLocalVariable(
        name: String?, descriptor: String?, signature: String?,
        start: Label?, end: Label?, index: Int,
    ) {
        if (hasTraceEvent) {
            println("$name---$descriptor---$index---$start---$end")
        }

        super.visitLocalVariable(name, descriptor, signature, start, end, index)
    }

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
        mv.visitLdcInsn(classAttributes.name)
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
            println("" + index + "----" + classAttributes.filters[index])
            mv.visitInsn(DUP)
            mv.visitIntInsn(SIPUSH, index)
            mv.visitIntInsn(SIPUSH, classAttributes.filters[index])
            mv.visitInsn(IASTORE)
        }
    }
}