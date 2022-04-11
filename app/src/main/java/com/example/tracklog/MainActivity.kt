package com.example.tracklog

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.xiaoan.tracklog.annotation.FixedAttribute
import com.xiaoan.tracklog.annotation.ReturnAttribute
import com.xiaoan.tracklog.annotation.TrackEvent
import com.xiaoan.tracklog.runtime.*

@TrackEvent("MainActivity")
class MainActivity : AppCompatActivity() {

//    @ReturnAttribute(key = "test")
//    @FixedAttribute(key = "test", value = "value")
//    @FixedAttribute(key = "test1", value = "value2")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val textview = findViewById<TextView>(R.id.textview).apply {
            setOnClickListener {
                test()
            }
        }
        TrackLog.init(object : EventSubscriber{
            override fun onEventTracked(event: Event) {

            }
        }).setEventLogListener(object : EventLogListener {
            override fun log(message: String) {
                Log.d("Tracker", message)
            }
        })
    }

    @TrackEvent("MainActivity")
    fun test() {
        TrackLogManager.sendEvent(this.javaClass.getAnnotation(TrackEvent::class.java)!!, mutableMapOf());
    }
}