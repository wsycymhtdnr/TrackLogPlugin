package com.xiaoan.tracklog.runtime

interface EventLogListener {
    fun log(message: String)
}