package com.xiaoan.extension

/**
 * 埋点插件参数配置
 */
open class TrackLogConfigExt {

    //gradle 拓展名称
    companion object {
        const val TRACK_LOG_EXT = "TrackLogExt"
    }

    // 多少个事件发送一次数据
    var eventSize : Int = 1

    // 是否只处理带有@TrackEvent注解的类，减少编译速度
    var isHookClassWithTrackEventAnnotation :Boolean= true

    open fun isHookClassWithTrackEventAnnotation(enabled: Boolean) {
        isHookClassWithTrackEventAnnotation = enabled
    }

}