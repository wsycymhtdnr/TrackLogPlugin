package com.xiaoan.beans

/**
 * @Author: liyunfei
 * @Description: @TrackEven注解属性实体类
 * @Date: 2022-04-19 01:39
 */
data class TrackEventBean(
    var name: String = "default",
    var filters: IntArray = IntArray(0)
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TrackEventBean

        if (name != other.name) return false
        if (!filters.contentEquals(other.filters)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + filters.contentHashCode()
        return result
    }
}