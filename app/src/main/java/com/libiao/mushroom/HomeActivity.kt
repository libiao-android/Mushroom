package com.libiao.mushroom

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.libiao.mushroom.analysis.SharesAnalysisActivity
import com.libiao.mushroom.kline.KLineActivity
import com.libiao.mushroom.mine.CollectActivity
import com.libiao.mushroom.mine.SelfSelectionActivity
import com.libiao.mushroom.mine.jigou.JiGouInfoRecordActivity
import com.libiao.mushroom.report.MyReportActivity
import com.libiao.mushroom.review.MyReviewActivity
import com.libiao.mushroom.utils.CodeUtil
import com.libiao.mushroom.utils.LogUtil
import com.permissionx.guolindev.PermissionX
import kotlinx.android.synthetic.main.home_activity.*
import okhttp3.*
import java.io.*
import java.nio.charset.Charset


// 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

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)

        applyPermission()

//        val intent = Intent(this, KLineActivity::class.java)
//        intent.putExtra("code", "sz002520")
//        intent.putExtra("info", "2021-12-11-6  sz000001  平安银行")
//        startActivity(intent)
        //test()
    }

    private fun test() {

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://hq.sinajs.cn/list=sz000001")
            .build()

        val call = client.newCall(request)

        call.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.i("libiao", "onFailure: ${e}")
            }

            override fun onResponse(call: Call?, response: Response?) {
                val value = response?.body()?.string()
                Log.i("libiao", "response: ${value}")
            }
        })
    }

    private fun applyPermission() {
        PermissionX.init(this)
            .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .onExplainRequestReason { scope, deniedList ->
                val message = "PermissionX需要您同意以下权限才能正常使用"
                scope.showRequestReasonDialog(deniedList, message, "确定", "取消")
            }
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    Toast.makeText(this, "所有申请的权限都已通过", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "您拒绝了如下权限：$deniedList", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun record(v: View) {
        val intent = Intent(this, SharesRecordActivity::class.java)
        startActivity(intent)
    }

    fun update(v: View) {
        val intent = Intent(this, UpdateStockPoolActivity::class.java)
        startActivity(intent)
    }

    fun analysis(v: View) {
        val intent = Intent(this, SharesAnalysisActivity::class.java)
        startActivity(intent)
    }

    fun myPool(v: View) {
//        val intent = Intent(this, MyPoolActivity::class.java)
//        startActivity(intent)
        val intent = Intent(this, SelfSelectionActivity::class.java)
        startActivity(intent)


    }

    fun myCollect(v: View) {
        val intent = Intent(this, CollectActivity::class.java)
        startActivity(intent)
    }

    fun search(v: View) {
        val code = et_search.text.trim().toString()
        LogUtil.i("HomeActivity", "search: $code")
        if(code.length == 6) {
            val intent = Intent(this, KLineActivity::class.java)
            intent.putExtra("code", CodeUtil.getCode(code))
            intent.putExtra("info", "")
            this.startActivity(intent)
        }
    }

    fun jiGou(v: View) {
        val intent = Intent(this, JiGouInfoRecordActivity::class.java)
        startActivity(intent)
    }

    private fun supplyInfo() {
        val time = "2021-5-27-4"
        val file = File(Environment.getExternalStorageDirectory(), "SharesInfo")
        val f = File(file, time)
        if(f.exists()) {
            val stream = FileInputStream(f)
            val reader = BufferedReader(InputStreamReader(stream, Charset.defaultCharset()))
            var str: String?
            str = reader.readLine()
            var count = 0
            while (str != null) {
                count++
                if(count % 100 == 0) {
                    val p = (count / 4000F * 100).toInt()
                    Log.i("libiao", "p: $p")
                }
                //Log.i("libiao", "str: $str, $count")
                val info = SharesRecordActivity.ShareInfo(str)
                info.time = time
                //Log.i("libiao", "count: $count, info: ${info.toFile()}")
                val codeFile = File(file, info.code)
                if(codeFile.exists()) {
                    writeFileAppend(codeFile, info.toFile())
                }
                str = reader.readLine()
                //if(count == 10) {break}
            }
        }
    }

    fun myReport(v: View) {
        val intent = Intent(this, MyReportActivity::class.java)
        startActivity(intent)
    }

    fun myReview(v: View) {
        val intent = Intent(this, MyReviewActivity::class.java)
        startActivity(intent)
    }

    private fun writeFileAppend(recordFile: File, info: String) {
        var fileWriter: FileWriter? = null
        try {
            fileWriter = FileWriter(recordFile, true)
            fileWriter.append("$info\n")
            fileWriter.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}