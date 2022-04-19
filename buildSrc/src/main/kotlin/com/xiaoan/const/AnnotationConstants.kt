package com.xiaoan.const

/**
 * @Author: liyunfei
 * @Description: 存放注解常数值
 * @Date: 2022-04-16 16:26
 */
object AnnotationConstants {
    // @TrackEvent 注解descriptor
    const val TRACK_EVENT = "Lcom/xiaoan/tracklog/annotation/TrackEvent;"

    // @FixedAttribute 注解descriptor
    const val FIXED_ATTRIBUTE = "Lcom/xiaoan/tracklog/annotation/FixedAttribute;"

    // @LocalVariableAttribute 注解descriptor
    const val LOCAL_VARIABLE_ATTRIBUTE = "Lcom/xiaoan/tracklog/annotation/LocalVariableAttribute;"

    // @ParameterAttribute 注解descriptor
    const val PARAMETER_ATTRIBUTE = "Lcom/xiaoan/tracklog/annotation/ParameterAttribute;"

    // @ReturnAttribute 注解descriptor
    const val RETURN_ATTRIBUTE = "Lcom/xiaoan/tracklog/annotation/ReturnAttribute;"

    // @FixedAttributes 注解descriptor
    const val FIXED_ATTRIBUTES = "Lcom/xiaoan/tracklog/annotation/FixedAttributes;"

    // 注解name属性
    const val NAME = "name"

    // 注解filters属性
    const val FILTERS = "filters"

    // 注解key属性
    const val KEY = "key"

    // 注解value属性key
    const val VALUE = "value"

    // 注解filters属性key
    const val IS_SHARED = "isShared"
}