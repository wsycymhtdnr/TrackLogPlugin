package com.xiaoan.asm

import com.xiaoan.beans.TrackEventBean
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
    // 类上面的@TraceEvent注解
    private var traceEventOnClass : TrackEventBean = TrackEventBean()

    // 是否含有@TraceEvent注解
    private var hasTraceEvent = false

    override fun visit(version: Int, access: Int, name: String?, signature: String?, superName: String?, interfaces: Array<out String>?) {
        super.visit(version, access, name, signature, superName, interfaces)
        name?.let {
            className = name
        }
    }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        if (descriptor == AnnotationConstants.TRACK_EVENT) {
            hasTraceEvent = true
        }
        return if (hasTraceEvent) {
            object: AnnotationVisitor(Opcodes.ASM7, super.visitAnnotation(descriptor, visible)) {
                override fun visit(name: String?, value: Any?) {
                    super.visit(name, value)
                    if (descriptor == AnnotationConstants.TRACK_EVENT) {
                        when (name) {
                            "name" -> traceEventOnClass.name = value as String
                            "filters" -> traceEventOnClass.filters = value as IntArray
                        }
                    }
                }
            }
        } else {
            super.visitAnnotation(descriptor, visible)
        }

    }

    override fun visitMethod(
        access: Int, name: String?, descriptor: String?, signature: String?,
        exceptions: Array<out String>?): MethodVisitor {

        val methodVisitor = cv.visitMethod(access, name, descriptor, signature, exceptions)
        // 有注解才对该类进行hook
        return if (configExt.isHookClassWithTrackEventAnnotation && hasTraceEvent) {
            TrackLogMethodVisitor(traceEventOnClass, methodVisitor, access, name, descriptor)
        } else {
            object : AdviceAdapter(Opcodes.ASM7, methodVisitor, access, name, descriptor) {}
        }
    }

}



