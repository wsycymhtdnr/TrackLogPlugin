package com.xiaoan.tracklog.runtime

/**
 * @Author liyunfei
 * @Description 日志的的实体类
 * @Date 2022/4/9 17:58
 */
data class Event (
    var name: String = "default", // 事件名称
    var filters: IntArray = intArrayOf(), // 用于过滤日志
    var attributes: MutableMap<String, Any> = mutableMapOf(),// 通过@TrackLog注解解析出来的参数
    var sharedAttributes: MutableMap<String, Any> = mutableMapOf(),// 公共的参数
    var next: Event? = null,
    var flags: Int = 0 // 当前对象是否被使用 0:未被使用 1:正在被使用
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false


        other as Event

        if (name != other.name) return false
        if (!filters.contentEquals(other.filters)) return false
        if (attributes != other.attributes) return false
        if (sharedAttributes != other.sharedAttributes) return false
        if (next != other.next) return false
        if (flags != other.flags) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + filters.contentHashCode()
        result = 31 * result + attributes.hashCode()
        result = 31 * result + sharedAttributes.hashCode()
        result = 31 * result + (next?.hashCode() ?: 0)
        result = 31 * result + flags
        return result
    }
}