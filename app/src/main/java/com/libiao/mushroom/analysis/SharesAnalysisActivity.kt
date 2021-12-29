package com.libiao.mushroom.analysis

import android.content.Intent
import android.os.*
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.libiao.mushroom.MyPool10Activity
import com.libiao.mushroom.MyPool20Activity
import com.libiao.mushroom.MyPool50Activity
import com.libiao.mushroom.R
import com.libiao.mushroom.setting.SettingActivity

class SharesAnalysisActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SharesAnalysisActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.analysis_activity)
        title = "--分析"
    }

    fun open10(v: View) {
        val intent = Intent(this, MyPool10Activity::class.java)
        startActivity(intent)
    }

    fun open20(v: View) {
        val intent = Intent(this, MyPool20Activity::class.java)
        startActivity(intent)
    }

    fun open60(v: View) {
        val intent = Intent(this, MyPool50Activity::class.java)
        startActivity(intent)
    }


    fun today(v: View) {
        val intent = Intent(this, SharesAnalysisTodayActivity::class.java)
        startActivity(intent)
    }

    fun setting(v: View) {
        startActivity(Intent(this, SettingActivity::class.java))
    }
}
