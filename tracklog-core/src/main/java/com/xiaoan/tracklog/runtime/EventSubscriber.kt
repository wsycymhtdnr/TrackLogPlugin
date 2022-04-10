package com.xiaoan.tracklog.runtime

/**
 * @Author liyunfei
 * @Description 监听接口
 * @Date 2022/4/10 23:12
 */
interface EventSubscriber {
    fun onEventTracked(event: Event?)
}