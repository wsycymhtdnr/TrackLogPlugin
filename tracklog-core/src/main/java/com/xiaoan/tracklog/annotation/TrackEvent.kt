package com.xiaoan.tracklog.annotation

/**
 * @Author liyunfei
 * @Description 触发埋点事件
 * @Date 2022/4/9 16:17
 * @param filters 可以用于过滤埋点事件, 比如说把挪车相关的所有的埋点事件设置同一个filters值
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class TrackEvent(val value: String, val filters: IntArray)
