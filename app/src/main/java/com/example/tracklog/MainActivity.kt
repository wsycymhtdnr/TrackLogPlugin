package com.example.tracklog

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.xiaoan.tracklog.annotation.TrackEvent
import com.xiaoan.tracklog.beans.TrackEventBean
import com.xiaoan.tracklog.runtime.*

@TrackEvent("MainActivity", [1,3])
class MainActivity : AppCompatActivity() {
    val name = "testName"
    private var classAttributes: MutableList<Map<String, String>> = mutableListOf()

    //    @ReturnAttribute(key = "test")
//    @FixedAttribute(key = "test", value = "value")
//    @FixedAttribute(key = "test1", value = "value2")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<TextView>(R.id.textview).apply {
            setOnClickListener {
                test()
            }
        }
        TrackLog.init(object : EventSubscriber {
            override fun onEventTracked(event: Event) {

            }
        }).setEventLogListener(object : EventLogListener {
            override fun log(message: String) {
                Log.d("Tracker", message)
            }
        })
    }

    @TrackEvent("MainActivity")
//    @ReturnAttribute("ReturnAttribute")
    fun test(/*@ParameterAttribute("add") add: String*/) {
//        val filters = intArrayOf(1, 2, 3)
//        val classTrackEvent = TrackEventBean(name, filters)
//        println(classTrackEvent)
//        val string  = "test"
//        Log.d("lyf", string)
//        InterningExample.example()
//        val trackEvent = this.javaClass.getAnnotation(TrackEvent::class.java)!!
//        TrackLogManager.sendEvent(classTrackEvent, mutableMapOf());
    }

}