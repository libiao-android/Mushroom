package com.libiao.mushroom.mine.fragment

import android.os.Environment
import androidx.fragment.app.Fragment
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.thread.ThreadPoolUtil
import com.libiao.mushroom.utils.LogUtil
import com.libiao.mushroom.utils.ShareParseUtil
import okhttp3.*
import java.io.*

open class BaseFragment(resLayout: Int): Fragment(resLayout) {

    companion object {
        const val TAG = "BaseFragment"
    }

    protected var heartChecked = false
    protected var onLineChecked = false
    protected var loadingStatus = 0

    var mRefreshCount = 0

    val file_2023 = File(Environment.getExternalStorageDirectory(), "A_SharesInfo/2023")
    private val client = OkHttpClient()

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
                            LogUtil.i(TAG, "share: $share")
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
                } finally {
                    ThreadPoolUtil.executeUI(Runnable {
                        shiShiRefresh()
                    })
                }
            }
        })
    }

    open fun shiShiRefresh() {}
}