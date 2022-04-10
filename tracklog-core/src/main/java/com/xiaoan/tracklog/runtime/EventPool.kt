package com.xiaoan.tracklog.runtime

/**
 * @Author: TrackLogPlugin
 * @Description: 日志事件缓存池
 * @Date: 2022-04-11 01:12
 */
object EventPool {
    private const val MAX_POOL_SIZE = 50
    private const val FLAG_IN_USE = 1
    private val sPoolSync = Any() // 对象锁
    private var sPool: Event? = null // Event对象池链表的尾节点
    private var sPoolSize = 0

    // 从缓存池获取Event对象
    fun obtain(): Event {
        synchronized(sPoolSync) {
            if (sPool != null) {
                val event = Event()
                sPool = event.next
                event.next = null
                event.flags = 0 // clear in-use flag
                sPoolSize--
                return event
            }
        }
        return Event()
    }

    // 回收缓存池中的对象
    fun recycle(event: Event) {
        if (isInUse(event)) {
            throw IllegalStateException("This message cannot be recycled because it is still in use.")
        }
        recycleUnchecked(event)
    }

    private fun isInUse(event: Event): Boolean {
        return (event.flags and FLAG_IN_USE) == FLAG_IN_USE
    }

    // 回收对象
    private fun recycleUnchecked(event: Event) {
        event.apply {
            flags = FLAG_IN_USE
            name = "default"
            filters = intArrayOf()
            attributes = mutableMapOf()
            sharedAttributes = mutableMapOf()
        }

        synchronized(sPoolSync) {
            if (sPoolSize < MAX_POOL_SIZE) {
                event.next = sPool
                sPool = event
                sPoolSize++
            }
        }
    }
}
