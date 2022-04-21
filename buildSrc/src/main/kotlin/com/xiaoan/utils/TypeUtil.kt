package com.xiaoan.utils

/**
 * 类型判断工具类，用来区分是否是某个特定的类型
 *
 * @author liyunfei
 */
object TypeUtil {

    fun isMatchCondition(name: String): Boolean {
        return name.endsWith(".class")
                && !name.contains("R$")
                && !name.contains("R.class")
                && !name.contains("BuildConfig.class")
    }
}
