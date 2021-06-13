package com.libiao.mushroom

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.libiao.mushroom.mode.*
import com.libiao.mushroom.utils.LogUtil.i
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class SharesAnalysisActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SharesAnalysisActivity"
    }

    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: SharesInfoAdater? = null
    private var mModeList: ArrayList<BaseMode> = ArrayList()

    private val mHandler = Handler(Looper.getMainLooper())

    private var timeTv: TextView? = null
    private var progressTv: TextView? = null
    private var loadingPb: ProgressBar? = null
    private var bigPowerBtn: Button? = null

    private val file = File(Environment.getExternalStorageDirectory(), "SharesInfo")

    private var time = "2021-4-30"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.more_and_less_activity)
        title = "股票分析"

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")// HH:mm:ss
        time = simpleDateFormat.format(Date())

        i(TAG, "t: $time")

        timeTv = findViewById(R.id.time)

        mRecyclerView = findViewById(R.id.recycler_view)
        mRecyclerView?.layoutManager = LinearLayoutManager(this)
        mAdapter = SharesInfoAdater(this)
        mRecyclerView?.adapter = mAdapter

        progressTv = findViewById(R.id.tv_progress)
        loadingPb = findViewById(R.id.loading)
        bigPowerBtn = findViewById(R.id.btn_start_analysis)

        timeTv?.text = "日期：$time"

        initData()
    }

    private fun initData() {
        mModeList.add(LeaderMode())
        mModeList.add(MoreShizixingMode())
        mModeList.add(MoreMildMode())
        mModeList.add(HorizontalPlateMode())
        mModeList.add(LessMildMode())
    }

    private fun start() {
        Thread {
            i(TAG, "开始查询")

            val stream = FileInputStream(File(file, "stock_pool"))
            val reader = BufferedReader(InputStreamReader(stream, Charset.defaultCharset()))
            var str: String?
            str = reader.readLine()
            var count = 0
            while (str != null) {
                count++
                if(count % 100 == 0) {
                    val p = (count / 4000F * 100).toInt()
                    mHandler.post { progressTv?.text = "$p%, $count" }
                }
                //Log.i("libiao", "str: $str, $count")
                //queryInfo(str.split(",")[0])

                analysis(str.split(",")[0])

                str = reader.readLine()
            }

            i(TAG, "结束查询")
            mHandler.post{
                progressTv?.text = "$100%, $count"
                loadingPb?.visibility = View.GONE
                bigPowerBtn?.isEnabled = true
                mAdapter?.setData(mModeList)
            }
        }.start()
    }

    private fun analysis(code: String) {
        val f = File(file, code)
        if(f.exists()) {
            val stream = FileInputStream(f)
            val reader = BufferedReader(InputStreamReader(stream, Charset.defaultCharset()))
            val lines = reader.readLines()
            //Log.i("libiao", "$lines")
            val shares = ArrayList<SharesRecordActivity.ShareInfo>()
            for(line in lines) {
                shares.add(SharesRecordActivity.ShareInfo(line))
            }
            mModeList.forEach {
                it.analysis(shares)
            }
        }
    }

    fun startAnalysis(view: View) {
        start()
        //test()
        loadingPb?.visibility = View.VISIBLE
        bigPowerBtn?.isEnabled = false
    }

    private fun test() {
        analysis("sz300767")
    }

    class SharesInfoAdater(val context: Context) : RecyclerView.Adapter<SharesInfoHolder>() {

        private var mSharesInfoList: ArrayList<BaseMode> = ArrayList()

        fun setData(sharesInfoList: ArrayList<BaseMode>) {
            mSharesInfoList = sharesInfoList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SharesInfoHolder {
            return SharesInfoHolder(LayoutInflater.from(context).inflate(R.layout.shares_info_item, null))
        }

        override fun getItemCount(): Int {
            return mSharesInfoList.size
        }

        override fun onBindViewHolder(holder: SharesInfoHolder, position: Int) {
            holder.bindData(mSharesInfoList.get(position))
        }
    }

    class SharesInfoHolder(view: View) : RecyclerView.ViewHolder(view) {

        private var mTitleTv: TextView? = null
        private var mContentTv: TextView? = null

        init {
            mTitleTv = view.findViewById(R.id.title)
            mContentTv = view.findViewById(R.id.content)
        }

        fun bindData(info: BaseMode) {
            mTitleTv?.text = info.des()
            mContentTv?.text = info.content()
        }

    }
}
