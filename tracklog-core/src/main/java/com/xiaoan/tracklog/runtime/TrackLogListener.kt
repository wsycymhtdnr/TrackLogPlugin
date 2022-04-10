package com.xiaoan.tracklog.runtime

import com.xiaoan.tracklog.annotation.TrackEvent

interface TrackLogListener {
    /**
     * 触发日志
     *
     * @param trackEvent
     * @param attributes 参数
     */
    fun onEventTriggered(trackEvent : TrackEvent, attributes: MutableMap<String, Any>)

    /**
     * 添加公共的参数
     *
     * @param key
     * @param value
     */
    fun onSharedAttributeAdded(key: String?, value: Any?)
}