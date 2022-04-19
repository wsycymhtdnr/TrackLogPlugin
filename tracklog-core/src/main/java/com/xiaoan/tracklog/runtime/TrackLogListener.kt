package com.xiaoan.tracklog.runtime

import com.xiaoan.tracklog.beans.TrackEventBean

interface TrackLogListener {
    /**
     * 触发日志
     *
     * @param trackEvent
     * @param attributes 参数
     */
    fun onEventTriggered(trackEvent : TrackEventBean, attributes: MutableMap<String, Any>)

    /**
     * 添加公共的参数
     *
     * @param key
     * @param value
     */
    fun onSharedAttributeAdded(key: String?, value: Any?)
}