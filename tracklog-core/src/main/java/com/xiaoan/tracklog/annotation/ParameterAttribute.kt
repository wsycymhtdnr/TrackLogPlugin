package com.xiaoan.tracklog.annotation

/**
 * @Author liyunfei
 * @Description 方法参数注解
 * @Date 2022/4/9 16:17
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class ParameterAttribute(val key: String)
