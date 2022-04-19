package com.xiaoan.tracklog.annotation

import jdk.jfr.Description

/**
 * @Author liyunfei
 * @Description 局部变量注解
 * @Date 2022/4/9 16:17
 */
@Target(AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@Description("kotlin编译器会丢弃掉局部变量注解，暂不支持")
annotation class LocalVariableAttribute(val key: String)
