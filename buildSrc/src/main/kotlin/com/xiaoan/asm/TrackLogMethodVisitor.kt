package com.xiaoan.asm

import com.xiaoan.const.AnnotationConstants.TRACK_EVENT
import org.objectweb.asm.*
import org.objectweb.asm.commons.AdviceAdapter

/**
 * @Author liyunfei
 * @Description 访问方法的信息，并且插入埋点代码， AOP Advice概念
 * @Date 2022/4/11 15:52
 */
class TrackLogMethodVisitor(methodVisitor: MethodVisitor?, access: Int, name: String?, descriptor: String?) :
    AdviceAdapter(Opcodes.ASM7, methodVisitor, access, name, descriptor) {
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

    override fun visitLocalVariableAnnotation(typeRef: Int, typePath: TypePath?, start: Array<out Label>?,
                end: Array<out Label>?, index: IntArray?, descriptor: String?, visible: Boolean): AnnotationVisitor {
        println("$name---$descriptor---$index")
        return TrackLogMethodLocalVarAnnotationVisitor(descriptor, Opcodes.ASM7,
            super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, descriptor, visible))
    }


    override fun visitLocalVariable(name: String?, descriptor: String?, signature: String?,
                                    start: Label?, end: Label?, index: Int) {
        if (hasTraceEvent) {
            println("$name---$descriptor---$index---$start---$end")
        }

        super.visitLocalVariable(name, descriptor, signature, start, end, index)
    }
}