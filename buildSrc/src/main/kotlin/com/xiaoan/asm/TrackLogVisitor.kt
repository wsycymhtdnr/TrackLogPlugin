package com.xiaoan.asm

import org.objectweb.asm.*
import org.objectweb.asm.commons.AdviceAdapter

//访问类,主要是为了注入一些统计信息到方法上的
class TrackLogVisitor(classWriter: ClassWriter) : ClassVisitor(Opcodes.ASM7, classWriter) {
    companion object {
        const val annotationDes = "Lcom/hank/hannotation/HannoLog;"
    }
    private lateinit var className: String

    override fun visit(version: Int, access: Int, name: String?, signature: String?,superName: String?, interfaces: Array<out String>?) {
        super.visit(version, access, name, signature, superName, interfaces)
        name?.let {
            className = name
        }
    }

    override fun visitMethod(access: Int, name: String?, descriptor: String?, signature: String?,
                             exceptions: Array<out String>?): MethodVisitor {
        val methodVisitor = cv.visitMethod(access, name, descriptor, signature, exceptions)
        return TrackLogMethodAdapter(methodVisitor, access, name, descriptor)
    }
}



