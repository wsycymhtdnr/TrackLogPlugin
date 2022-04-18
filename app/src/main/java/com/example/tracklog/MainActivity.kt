package com.example.tracklog

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.xiaoan.tracklog.annotation.LocalVariableAttribute
import com.xiaoan.tracklog.annotation.LocalVariableJava
import com.xiaoan.tracklog.annotation.ParameterAttribute
import com.xiaoan.tracklog.annotation.ReturnAttribute
import com.xiaoan.tracklog.annotation.TrackEvent
import com.xiaoan.tracklog.runtime.*

@TrackEvent("MainActivity")
class MainActivity : AppCompatActivity() {

    private var classAttributes: MutableList<Map<String, String>> = mutableListOf()

//    @ReturnAttribute(key = "test")
//    @FixedAttribute(key = "test", value = "value")
//    @FixedAttribute(key = "test1", value = "value2")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    findViewById<TextView>(R.id.textview).apply {
        setOnClickListener {
            test("add")
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
    @ReturnAttribute("ReturnAttribute")
    fun test(@ParameterAttribute("add") add: String) {
        val string  = "test"
        Log.d("lyf", string)
        InterningExample.example()
        TrackLogManager.sendEvent(this.javaClass.getAnnotation(TrackEvent::class.java)!!, mutableMapOf());
    }

}