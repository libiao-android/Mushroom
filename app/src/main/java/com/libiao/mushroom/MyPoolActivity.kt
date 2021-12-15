package com.libiao.mushroom

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity


class MyPoolActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MyPoolActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_pool_main_activity)
        title = "我的股票池"
    }

    fun open20(v: View) {
        val intent = Intent(this, MyPool20Activity::class.java)
        startActivity(intent)
    }

    fun open60(v: View) {
        val intent = Intent(this, MyPool50Activity::class.java)
        startActivity(intent)
    }
}
