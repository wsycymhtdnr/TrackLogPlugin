package com.xiaoan.tracklog.annotation

/**
 * @Author liyunfei
 * @Description 局部变量注解
 * @Date 2022/4/9 16:17
 */
@Target(AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
annotation class LocalVariableAttribute(val key: String)
