package com.example.tracklog

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.xiaoan.tracklog.annotation.FixedAttribute
import com.xiaoan.tracklog.annotation.ReturnAttribute

class MainActivity : AppCompatActivity() {
    @ReturnAttribute(key = "test")
    @FixedAttribute(key = "test", value = "value")
    @FixedAttribute(key = "test1", value = "value2")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}