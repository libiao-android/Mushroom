package com.libiao.mushroom.fanbao

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.room.report.ReportShareInfo
import com.libiao.mushroom.utils.LogUtil
import com.libiao.mushroom.utils.ShareParseUtil
import okhttp3.*
import java.io.*
import java.nio.charset.Charset
import android.app.NotificationChannel
import android.os.*
import androidx.core.app.NotificationCompat
import android.content.Context.VIBRATOR_SERVICE
import android.os.Vibrator




@SuppressLint("StaticFieldLeak")
object FanBaoShiShiManager {
    private const val TAG = "FanBaoShiShiManager"
    val file_2023 = File(Environment.getExternalStorageDirectory(), "A_SharesInfo/2023")
    private val client = OkHttpClient()
    private var mData: List<ReportShareInfo> = ArrayList()
    private val handlerThread = HandlerThread("FanBaoShiShiManager")
    private var handler: Handler? = null

    private var mContext: Context? = null


    private var mFanBaoCode: ArrayList<String> = ArrayList()

    private var noticeManager: NotificationManager? = null
    private var vibrator: Vibrator? = null

    private var id = 0


    fun setData(context: Context?, data: ArrayList<ReportShareInfo>) {
        if(mData.isNotEmpty()) return
        mContext = context
        noticeManager = context?.getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
        vibrator = context?.getSystemService(VIBRATOR_SERVICE) as Vibrator?
        LogUtil.i(TAG, "setData：${mData.size}")
        mData = data
        handlerThread.start()
        handler = Handler(handlerThread.looper)
        handler?.post(queryRun)
    }

    private val queryRun = object : Runnable{
        override fun run() {
            LogUtil.i(TAG, "开始轮询。。。。。。。。。。")
            mData.forEachIndexed { index, it ->
                shiShiQuery(it.code!!) {share ->
                    val f = File(file_2023, it.code!!)
                    if(f.exists()) {
                        val stream = FileInputStream(f)
                        val reader = BufferedReader(InputStreamReader(stream, Charset.defaultCharset()))
                        val lines = reader.readLines()
                        val share_pre = SharesRecordActivity.ShareInfo(lines.last())
                        LogUtil.i(TAG, "${share_pre.code}, ${share_pre.maxPrice}, ${share.maxPrice}")
                        if(share.maxPrice > share_pre.maxPrice) {
                            // fan反包
                            if(!mFanBaoCode.contains(share.code)) {
                                mFanBaoCode.add(share.code!!)
                                LogUtil.i(TAG, "fan bao : ${share.code}")
                                // 通知 + 震动
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                                    val notificationChannel = NotificationChannel(
                                        "mushroom",
                                        "fangliang",
                                        NotificationManager.IMPORTANCE_HIGH
                                    )
                                    noticeManager?.createNotificationChannel(notificationChannel)
                                }
                                val notification = NotificationCompat.Builder(mContext!!, "mushroom")
                                    .setContentTitle("反包")
                                    .setContentText("${share.code}, ${share.name}, ${share.range}")
                                    .setWhen(System.currentTimeMillis())
                                    .setSmallIcon(com.libiao.mushroom.R.mipmap.ic_launcher)
                                   // .setContentIntent(pendingIntent)
                                    .setAutoCancel(true)  // 自动取消通知，点击通知之后通知就消失了
                                    .build()
                                noticeManager?.notify(id++, notification)

                                vibrator?.vibrate(500)

                            }
                        }

                    }
                    if (index == mData.size - 1) {
                        handler?.postDelayed(this, 2000)
                    }
                }
            }
        }
    }

    fun shiShiQuery(code: String, result: (info: SharesRecordActivity.ShareInfo) -> Unit) {
        //http://qt.gtimg.cn/q=s_sz000858
        val request = Request.Builder()
            .url("https://qt.gtimg.cn/q=$code")
            .build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                LogUtil.i(TAG, "onFailure: ${e}, $code")
            }

            override fun onResponse(call: Call?, response: Response?) {
                val value = response?.body()?.string()
                //Log.i("libiao_A", "response: ${value}")
                try {
                    if (value != null) {
                        //LogUtil.i(TAG, "response: ${value}")
                        val strs = value.split("~")
                        if(strs.size > 45) {
                            val share = ShareParseUtil.parseFromNetwork(code, strs)
                            //LogUtil.i(TAG, "share: $share")
                            result(share)
                        }
                    } else {
                        LogUtil.i(
                            TAG,
                            "exception value: $value, $code"
                        )
                    }
                } catch (e: Exception) {
                    LogUtil.i(
                        TAG,
                        "parseSharesInfo exception: ${e.message}, $code, $value"
                    )
                }
            }
        })
    }
}