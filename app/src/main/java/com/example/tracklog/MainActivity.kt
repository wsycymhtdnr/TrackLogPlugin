package com.example.tracklog

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.xiaoan.tracklog.annotation.*
import com.xiaoan.tracklog.beans.TrackEventBean
import com.xiaoan.tracklog.runtime.*
import java.util.*

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
                test(Date())
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

    @FixedAttributes([
        FixedAttribute(key = "key1", value = "value1", false),
        FixedAttribute(key = "key2", value = "value2", true)])
    @TrackEvent("test", [2,5,3])
    @ReturnAttribute("return")
//    @FixedAttribute(key = "key1", value = "value1", false)
//    @ReturnAttribute("ReturnAttribute")
    fun test(@ParameterAttribute("add", false) add: Date): String {
//        println(classTrackEvent)
//        val string  = "test"
//        Log.d("lyf", string)
//        InterningExample.example()
//        val trackEvent = this.javaClass.getAnnotation(TrackEvent::class.java)!!

//        val map: MutableMap<String, Any> = mutableMapOf()
//        map["asd"] = "def"
//        println(map)

        val filters = intArrayOf(1, 2, 3)
        val classTrackEvent = TrackEventBean(name, filters)
        val map1: MutableMap<String, Any> = mutableMapOf()
        val map2: MutableMap<String, Any> = mutableMapOf()
        TrackLogManager.addSharedAttribute(map1)
        TrackLogManager.sendEvent(classTrackEvent, map2)
        return "111"
    }

}