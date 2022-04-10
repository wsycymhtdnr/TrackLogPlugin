package com.xiaoan.tracklog.runtime

import com.xiaoan.tracklog.annotation.TrackEvent

/**
 * @Author 用于ASM触发日志事件和添加公共的参数
 * @Description 监听接口
 * @Date 2022/4/10 23:12
 */
object TrackLogManager {
    private lateinit var trackLogListener: TrackLogListener

    fun subscribe(listener: TrackLogListener) {
        trackLogListener = listener
    }

    fun sendEvent(trackEvent: TrackEvent, attributes: MutableMap<String, Any>) {
        trackLogListener.onEventTriggered(trackEvent, attributes)
    }

    fun addSharedAttribute(key: String, value: Any) {
        trackLogListener.onSharedAttributeAdded(key, value)
    }
}