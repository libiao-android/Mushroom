package com.libiao.mushroom.analysis

import android.content.Intent
import android.os.*
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.libiao.mushroom.R


class SharesAnalysisActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SharesAnalysisActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.analysis_activity)
        title = "股票分析"
    }

    fun route_2019(v: View) {
        val intent = Intent(this, SharesAnalysisHistoryActivity::class.java)
        intent.putExtra("year", "2019")
        startActivity(intent)
    }

    fun route_2020(v: View) {
        val intent = Intent(this, SharesAnalysisHistoryActivity::class.java)
        intent.putExtra("year", "2020")
        startActivity(intent)
    }

    fun route_2021(v: View) {
        val intent = Intent(this, SharesAnalysisHistoryActivity::class.java)
        intent.putExtra("year", "2021")
        startActivity(intent)
    }

    fun today(v: View) {
        val intent = Intent(this, SharesAnalysisTodayActivity::class.java)
        startActivity(intent)
    }
}
