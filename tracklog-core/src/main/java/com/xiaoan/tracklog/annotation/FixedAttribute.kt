package com.xiaoan.tracklog.annotation

/**
 * @Author liyunfei
 * @Description 固定值的注解
 * @Date 2022/4/9 16:17
 */
@Repeatable
@Target(AnnotationTarget.FUNCTION,AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class FixedAttribute(val key: String, val value: String)
