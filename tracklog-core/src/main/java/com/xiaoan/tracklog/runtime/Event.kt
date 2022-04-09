package com.xiaoan.tracklog.runtime

/**
 * @Author liyunfei
 * @Description 单个日志的的实体类
 * @Date 2022/4/9 17:58
 */
data class Event(
    val name: String,
    val filter: IntArray,
    val attributes: MutableMap<String, Any>, // 通过@TrackLog注解解析出来的参数
    val sharedAttributes: MutableMap<String, Any> // 公共的参数
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Event

        if (name != other.name) return false
        if (!filter.contentEquals(other.filter)) return false
        if (attributes != other.attributes) return false
        if (sharedAttributes != other.sharedAttributes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + filter.contentHashCode()
        result = 31 * result + attributes.hashCode()
        result = 31 * result + sharedAttributes.hashCode()
        return result
    }
}