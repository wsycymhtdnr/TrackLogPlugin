package com.xiaoan.asm

import com.xiaoan.const.AnnotationConstants.FIXED_ATTRIBUTE
import com.xiaoan.const.AnnotationConstants.LOCAL_VARIABLE_ATTRIBUTE
import com.xiaoan.const.AnnotationConstants.PARAMETER_ATTRIBUTE
import com.xiaoan.const.AnnotationConstants.RETURN_ATTRIBUTE
import com.xiaoan.const.AnnotationConstants.TRACK_EVENT
import org.objectweb.asm.AnnotationVisitor

/**
 * @Author: liyunfei
 * @Description: 访问埋点入口方法的注解
 * @Date: 2022-04-16 16:57
 */
class TrackLogMethodAnnotationVisitor(private val descriptor: String?,
    api: Int, annotationVisitor: AnnotationVisitor?) : AnnotationVisitor(api, annotationVisitor) {

    override fun visit(name: String?, value: Any?) {
        when (descriptor) {
            TRACK_EVENT -> {
                println("TrackLogMethodAnnotationVisitor")
            }
            FIXED_ATTRIBUTE -> {}
            LOCAL_VARIABLE_ATTRIBUTE -> {}
            PARAMETER_ATTRIBUTE -> {}
            RETURN_ATTRIBUTE -> {}
        }
        super.visit(name, value)
    }
}