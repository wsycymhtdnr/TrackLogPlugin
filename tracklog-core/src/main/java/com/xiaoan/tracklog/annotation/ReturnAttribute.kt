package com.xiaoan.tracklog.annotation

/**
 * @Author liyunfei
 * @Description 方法返回参数注解
 * @Date 2022/4/9 16:17
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ReturnAttribute(val key: String, val shared: Boolean = false)
