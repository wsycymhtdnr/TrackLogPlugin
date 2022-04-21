package com.xiaoan.tracklog.runtime

import com.xiaoan.tracklog.annotation.TrackEvent
import com.xiaoan.tracklog.beans.TrackEventBean

/**
 * @Author liyunfei
 * @Description TrackLog入口类
 * @Date 2022/4/10 23:12
 */
object TrackLog : TrackLogListener {
    private var sharedAttributes = mutableMapOf<String, Any>()
    private lateinit var logger: EventLogListener
    private lateinit var eventSubscriber: EventSubscriber

    fun init(eventSubscriber: EventSubscriber): TrackLog {
        TrackLogManager.subscribe(this)
        this.eventSubscriber = eventSubscriber
        return this
    }


    fun setEventLogListener(logger: EventLogListener) {
        this.logger = logger
    }

    private fun trackEvent(event: Event) {
        eventSubscriber.onEventTracked(event)
        log(event)
        EventPool.recycle(event)
    }

    private fun log(event: Event) {
        val builder: StringBuilder = StringBuilder()
            .append(event.name)
            .append("-> ")
            .append(event.attributes.toString())
            .append(", shared attrs: ")
            .append(sharedAttributes.toString())
            .append(", filters: ")
            .append(event.filters.contentToString())
        logger.log(builder.toString())
    }

    override fun onEventTriggered(trackEvent: TrackEventBean, attributes: MutableMap<String, Any>) {
        val event = EventPool.obtain().apply {
            name = trackEvent.name
            filters = trackEvent.filters
            this.attributes = attributes
            sharedAttributes = this@TrackLog.sharedAttributes
        }
        trackEvent(event)
    }

    override fun onSharedAttributeAdded(sharedAttributes: MutableMap<String, Any>) {
        this.sharedAttributes.putAll(sharedAttributes)
    }
}