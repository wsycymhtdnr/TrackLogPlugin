package com.xiaoan.asm

import com.xiaoan.const.AnnotationConstants.TRACK_EVENT
import org.objectweb.asm.*
import org.objectweb.asm.commons.AdviceAdapter

/**
 * @Author liyunfei
 * @Description 访问方法的信息，并且插入埋点代码， AOP Advice概念
 * @Date 2022/4/11 15:52
 */
class TrackLogMethodVisitor(
    classAttributes: MutableList<Map<String, String>>, methodVisitor: MethodVisitor?, access: Int,
    name: String?, descriptor: String?,
) : AdviceAdapter(Opcodes.ASM7, methodVisitor, access, name, descriptor) {
    // 是否含有@TraceEvent注解
    private var hasTraceEvent = false

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
        val conditionLabel = Label()
        val returnLabel = Label()

        // 第1段

        // 第1段
        mv.visitCode()
        mv.visitInsn(ICONST_0)
        mv.visitVarInsn(ISTORE, 1)

        // 第2段

        // 第2段
        mv.visitLabel(conditionLabel)
        mv.visitVarInsn(ILOAD, 1)
        mv.visitIntInsn(BIPUSH, 10)
        mv.visitJumpInsn(IF_ICMPGE, returnLabel)
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        mv.visitVarInsn(ILOAD, 1)
        mv.visitMethodInsn(INVOKEVIRTUAL,
            "java/io/PrintStream",
            "println",
            "(I)V",
            false)
        mv.visitIincInsn(1, 1)
        mv.visitJumpInsn(GOTO, conditionLabel)

        // 第3段

        // 第3段
        mv.visitLabel(returnLabel)
        mv.visitInsn(RETURN)
        mv.visitMaxs(0, 0)
        // val localVariable = newLocal(Type.)
    }
}