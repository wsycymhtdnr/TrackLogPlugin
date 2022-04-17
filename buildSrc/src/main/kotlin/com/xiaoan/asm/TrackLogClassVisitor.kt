package com.xiaoan.asm

import com.xiaoan.extension.TrackLogConfigExt
import com.xiaoan.const.AnnotationConstants
import org.objectweb.asm.*
import org.objectweb.asm.commons.AdviceAdapter

/**
 * @Author liyunfei
 * @Description 访问类的注解信息, 会将类注解的参数传入到该类的所有带@TrackEvent注解的方法中
 * @Date 2022/4/11 15:52
 */
class TrackLogClassVisitor(classWriter: ClassWriter, private val configExt: TrackLogConfigExt) :
    ClassVisitor(Opcodes.ASM7, classWriter) {
    private lateinit var className: String

    // 是否含有@TraceEvent注解
    private var hasTraceEvent = false

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        name?.let {
            className = name
        }
    }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        if (descriptor == AnnotationConstants.TRACK_EVENT) {
            hasTraceEvent = true
        }
        return super.visitAnnotation(descriptor, visible)
    }

    override fun visitMethod(
        access: Int, name: String?, descriptor: String?, signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val methodVisitor = cv.visitMethod(access, name, descriptor, signature, exceptions)
        // 有注解才对该类进行hook
        if (configExt.isHookClassWithTrackEventAnnotation && hasTraceEvent) {
            return TrackLogMethodVisitor(methodVisitor, access, name, descriptor)
        } else {
            return object : AdviceAdapter(Opcodes.ASM7, methodVisitor, access, name, descriptor) {}
        }
    }

}



