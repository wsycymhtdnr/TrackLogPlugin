package com.xiaoan.asm

import jdk.jfr.Description
import org.objectweb.asm.AnnotationVisitor

/**
 * @Author: liyunfei
 * @Description:
 * @Date: 2022-04-17 01:38
 */
@Description("kotlin编译器会丢弃掉局部变量注解，暂不支持")
class TrackLogMethodLocalVarAnnotationVisitor(private val descriptor: String?,
    api: Int, annotationVisitor: AnnotationVisitor?) : AnnotationVisitor(api, annotationVisitor) {
    override fun visit(name: String?, value: Any?) {
        println("TrackLogMethodLocalVarAnnotationVisitor")
        super.visit(name, value)

    }
}