package com.xiaoan.asm

import org.objectweb.asm.AnnotationVisitor

/**
 * @Author: liyunfei
 * @Description:
 * @Date: 2022-04-17 01:38
 */
class TrackLogMethodLocalVarAnnotationVisitor(private val descriptor: String?,
    api: Int, annotationVisitor: AnnotationVisitor?) : AnnotationVisitor(api, annotationVisitor) {
    override fun visit(name: String?, value: Any?) {
        println("TrackLogMethodLocalVarAnnotationVisitor")
        super.visit(name, value)

    }
}