package com.xiaoan.tracklog.runtime

import com.xiaoan.tracklog.annotation.TrackEvent

/**
 * @Author liyunfei
 * @Description TrackLog入口类
 * @Date 2022/4/10 23:12
 */
class TrackLog private constructor(var eventSubscriber: EventSubscriber) : TrackLogListener {
    private val sharedAttributes = mutableMapOf<String, Any>()
    private lateinit var logger: EventLogListener

    fun init(eventSubscriber: EventSubscriber): TrackLog {
        val trackLog = TrackLog(eventSubscriber)
        TrackLogManager.subscribe(this)
        return trackLog
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

    override fun onEventTriggered(trackEvent: TrackEvent, attributes: MutableMap<String, Any>) {
        val event = EventPool.obtain().apply {
            name = trackEvent.name
            filters = trackEvent.filters
            this.attributes = attributes
            sharedAttributes = this@TrackLog.sharedAttributes
        }
        trackEvent(event)
    }

    override fun onSharedAttributeAdded(key: String?, value: Any?) {
        TODO("Not yet implemented")
    }
}