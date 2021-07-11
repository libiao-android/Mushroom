package com.libiao.mushroom.analysis

import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.libiao.mushroom.R
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.mode.BaseMode
import com.libiao.mushroom.utils.LogUtil.i
import okhttp3.*
import java.io.IOException
import java.lang.Exception


class SharesRealTimeInfoActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SharesRealTimeInfoActivity"
    }

    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: SharesRealTimeInfoAdater? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.real_time_activity)
        title = "实时分析"

        val data = intent.getCharSequenceArrayListExtra("data")
        //i(TAG, "$data")

        mRecyclerView = findViewById(R.id.real_time_rv)
        mRecyclerView?.layoutManager = LinearLayoutManager(this)
        mAdapter = SharesRealTimeInfoAdater(this)
        mRecyclerView?.adapter = mAdapter
        mAdapter?.setData(data as ArrayList<SharesRecordActivity.ShareInfo>)
    }

    override fun onDestroy() {
        super.onDestroy()
        mAdapter?.destory()
    }

    class SharesRealTimeInfoAdater(val context: Context) : RecyclerView.Adapter<SharesRealTimeInfoHolder>() {

        private var mSharesInfoList: ArrayList<SharesRecordActivity.ShareInfo> = ArrayList()
        private val mHandler = Handler()

        fun setData(sharesInfoList: ArrayList<SharesRecordActivity.ShareInfo>) {
            sharesInfoList.sortByDescending { it.totalPrice }
            mSharesInfoList = sharesInfoList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SharesRealTimeInfoHolder {
            return SharesRealTimeInfoHolder(mHandler,
                LayoutInflater.from(context).inflate(
                    R.layout.shares_real_time_info_item,
                    null
                )
            )
        }

        override fun getItemCount(): Int {
            return mSharesInfoList.size
        }

        override fun onBindViewHolder(holder: SharesRealTimeInfoHolder, position: Int) {
            holder.bindData(mSharesInfoList.get(position))
        }

        fun destory() {
            mHandler.removeCallbacksAndMessages(null)
        }
    }

    class SharesRealTimeInfoHolder(val handler: Handler, view: View) : RecyclerView.ViewHolder(view) {

        private var mNameTv: TextView? = null
        private var mCodeTv: TextView? = null
        private var mZongShiZhiTv: TextView? = null
        private var mChengJiaoLiangTv: TextView? = null
        private var mZhangDieFuTv: TextView? = null
        private var mChengJiaoZhanBiTv: TextView? = null

        private val client = OkHttpClient()
        private var totalPrice = 1.00

        init {
            mNameTv = view.findViewById(R.id.name)
            mCodeTv = view.findViewById(R.id.code)
            mZongShiZhiTv = view.findViewById(R.id.zongshizhi)
            mChengJiaoLiangTv = view.findViewById(R.id.chengjiaoliang)
            mZhangDieFuTv = view.findViewById(R.id.zhangdiefu)
            mChengJiaoZhanBiTv = view.findViewById(R.id.chengjiaozhanbi)
        }

        fun bindData(info: SharesRecordActivity.ShareInfo) {
            mNameTv?.text = info.name
            mCodeTv?.text = info.code?.substring(2)
            mZongShiZhiTv?.text = info.zongShiZhi.toString()
            mChengJiaoLiangTv?.text = String.format("%.2f",info.totalPrice / 100000000)
            totalPrice = info.totalPrice

            timingQuery(info.code)

        }

        private fun timingQuery(code: String?) {
            handler.post(object : Runnable{
                override fun run() {
                    shiShiQuery(code)
                    handler.postDelayed(this, 5000)
                }
            })
        }

        private fun shiShiQuery(code: String?) {
            //http://qt.gtimg.cn/q=s_sz000858
            val request = Request.Builder()
                .url("https://qt.gtimg.cn/q=s_$code")
                .build()
            val call = client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                    i(TAG, "onFailure: ${e}, $code")
                }

                override fun onResponse(call: Call?, response: Response?) {
                    val value = response?.body()?.string()
                    //Log.i("libiao_A", "response: ${value}")
                    try {
                        if (value != null) {
                            i(TAG, "response: ${value}")
                            val strs = value.split("~")
                            if(strs.size > 8) {
                                val range = strs[5]
                                val price = strs[7]
                              //  i(TAG, "range: ${range}")
                              //  i(TAG, "price: ${price}")
                                handler.post {
                                    mZhangDieFuTv?.text = range
                                    try {
                                        val p = price.toDouble() * 10000 / totalPrice * 100
                                        mChengJiaoZhanBiTv?.text = "${String.format("%.2f",p)}"
                                    } catch (e: Exception) {
                                        i(TAG, "toDouble exception: ${e.message}, $code, $value")
                                        e.printStackTrace()
                                    }
                                }
                            }
                        } else {
                            i(TAG, "exception value: $value, $code")
                        }
                    } catch (e: Exception) {
                        i(TAG, "parseSharesInfo exception: ${e.message}, $code, $value")
                    }
                }
            })

        }

    }
}
