package com.example.tracklog

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.xiaoan.tracklog.annotation.*
import com.xiaoan.tracklog.runtime.Event
import com.xiaoan.tracklog.runtime.EventLogListener
import com.xiaoan.tracklog.runtime.EventSubscriber
import com.xiaoan.tracklog.runtime.TrackLog
import java.util.*

@TrackEvent("MainActivity", [1,3])
class MainActivity : AppCompatActivity() {
    val name = "testName"
    private var classAttributes: MutableList<Map<String, String>> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<TextView>(R.id.textview).apply {
            setOnClickListener {
                try {
                    println(test(Date(),"a",123,'a',"b".toShort(), 1.44f, 14546L, 454.6546, false, 'r'))
                } catch (e: Exception) {
                    println(e)
                }

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
    @Throws(Exception::class) //异常需要抛出，暂时未作处理
    fun test(@ParameterAttribute("date", false) date: Date,
             @ParameterAttribute("a", false)a: String,
             @ParameterAttribute("b", true)b: Int,
             @ParameterAttribute("c", true)c: Char,
             @ParameterAttribute("d", true)d: Short,
             @ParameterAttribute("e", true)e: Float,
             @ParameterAttribute("f", true)f: Long,
             @ParameterAttribute("g", true)g: Double,
             @ParameterAttribute("h", true)h: Boolean,
             @ParameterAttribute("i", true)i: Char): Double {
//        println(classTrackEvent)
//        val string  = "test"
//        Log.d("lyf", string)
//        InterningExample.example()
//        val trackEvent = this.javaClass.getAnnotation(TrackEvent::class.java)!!

//        val map: MutableMap<String, Any> = mutableMapOf()
//        map["asd"] = "def"
//        println(map)

//        val filters = intArrayOf(1, 2, 3)
//        val classTrackEvent = TrackEventBean(name, filters)
        val map1: MutableMap<String, Any> = mutableMapOf()
//        println(map1)
//        val map2: MutableMap<String, Any> = mutableMapOf()
//        TrackLogManager.addSharedAttribute(map1)
//        TrackLogManager.sendEvent(classTrackEvent, map2)
//        return "111"
//            val `is`: InputStream = FileInputStream("E:/iodemo/ch01.txt")
        val a = 'a'.code.toShort()
        map1.put("sada",a)
        val b = 1.000f
        map1.put("sada",b)
        val c = 1000L
        map1.put("sada",c)
        val d = 8.55666
        map1.put("sada",d)
        val e = false
        map1.put("sada",e)
        return 1.222
    }

}