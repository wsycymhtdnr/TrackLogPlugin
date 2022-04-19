package com.xiaoan.tracklog.annotation

/**
 * @Author liyunfei
 * @Description kotlin1.6才支持重复注解
 * @Date 2022/4/19 11:37
 */
@Target(AnnotationTarget.FUNCTION,AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class FixedAttributes(val value: Array<FixedAttribute>)
