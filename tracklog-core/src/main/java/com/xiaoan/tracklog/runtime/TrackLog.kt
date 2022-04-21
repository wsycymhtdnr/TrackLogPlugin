package com.xiaoan.tracklog.runtime

import com.xiaoan.tracklog.beans.TrackEventBean

/**
 * @Author liyunfei
 * @Description TrackLog入口类
 * @Date 2022/4/10 23:12
 */
object TrackLog : TrackLogListener {
    private var sharedAttributes = mutableMapOf<String, Any>()
    private var logger: EventLogListener? = null
    private var filter: IntArray? = null
    private lateinit var eventSubscriber: EventSubscriber

    fun init(eventSubscriber: EventSubscriber): TrackLog {
        TrackLogManager.subscribe(this)
        this.eventSubscriber = eventSubscriber
        return this
    }


    fun addEventLogListener(logger: EventLogListener) : TrackLog{
        this.logger = logger
        return this
    }

    fun addFilter(filter: IntArray) : TrackLog{
        this.filter = filter
        return this
    }

    private fun trackEvent(event: Event) {
        if (filterEvent(event)) {
            eventSubscriber.onEventTracked(event)
        }

        if (logger != null && filterEvent(event)) {
            log(event)
        }
        EventPool.recycle(event)
    }

    private fun filterEvent(event: Event) :Boolean {
        if (filter == null) {
            return true
        }
        filter!!.asSequence()
            .count {
                event.filters.contains(it)
            }.run {
                return this != 0
            }
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
        logger?.log(builder.toString())
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