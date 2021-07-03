package com.libiao.mushroom.analysis

import android.content.Context
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.libiao.mushroom.R
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.mode.*
import com.libiao.mushroom.utils.LogUtil.i
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import kotlin.collections.ArrayList


class SharesAnalysisHistoryActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SharesAnalysisHistoryActivity"
    }

    private var mSpinner: Spinner? = null

    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: SharesInfoAdater? = null
    private var mModeList: ArrayList<BaseMode> = ArrayList()

    private val mHandler = Handler(Looper.getMainLooper())

    private var timeTv: TextView? = null
    private var progressTv: TextView? = null
    private var loadingPb: ProgressBar? = null

    private val file = File(Environment.getExternalStorageDirectory(), "A_SharesInfo")
    private var file_year = File(file, "2020")
    private var year = "2020"

    private val allData: ArrayList<ArrayList<SharesRecordActivity.ShareInfo>> = ArrayList()
    private var month: String = "01"
    private var dataInitDone = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.analysis_2020)
        year = intent.getStringExtra("year")
        title = year
        if(year == "2020") {
            file_year = File(file, "2020")
        } else if(year == "2021") {
            file_year = File(file, "2021")
        } else if(year == "2019") {
            file_year = File(file, "2019")
        }

        timeTv = findViewById(R.id.time)

        mRecyclerView = findViewById(R.id.recycler_view)
        mRecyclerView?.layoutManager = LinearLayoutManager(this)
        mAdapter = SharesInfoAdater(this)
        mRecyclerView?.adapter = mAdapter

        progressTv = findViewById(R.id.tv_progress)
        loadingPb = findViewById(R.id.loading)

        mSpinner = findViewById(R.id.spinner)
        mSpinner?.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val months = resources.getStringArray(R.array.month_list)
                month = months[position]
                i(TAG, "$month")
                timeTv?.text = "当前选择的月份是：$month 月"
                findOutFitMode()
            }

        }


        readFileData()

        initData()
    }

    private fun findOutFitMode() {
        if(dataInitDone) {
            clear()
            allData.forEach {shares ->
                var start = 0
                var end = 0
                shares.forEachIndexed { index, shareInfo ->
                    if(shareInfo.time?.startsWith("$year-$month") == true) {
                        if(start == 0) { start = index }
                        end = index
                    }
                }
                for(i in start+1 .. end+1) {
                    mModeList.forEach {
                        it.analysis(i, shares)
                    }
                }
            }
            mAdapter?.setData(mModeList)
        }
    }

    private fun readFileData() {
        start()
    }

    private fun initData() {
       // mModeList.add(LeaderMode())

//        mModeList.add(MoreShizixingMode())
//        mModeList.add(MoreMildMode())
//        mModeList.add(HorizontalPlateMode())
//        mModeList.add(LessMildMode())

        mModeList.add(MoreMoreMode1(true))
//        mModeList.add(MoreMoreMode2())
//        mModeList.add(MoreMoreMode3())
//        mModeList.add(MoreMoreMode4())
//        mModeList.add(MoreMoreMode5())
//
//        mModeList.add(UpLine5Mode())
//        mModeList.add(UpLine10Mode())
    }

    var done = 0
    private fun start() {
        loadingPb?.visibility = View.VISIBLE
        Thread {
            i(TAG, "开始初始化")
            val codeList = ArrayList<String>()
            val stream = FileInputStream(File(file, "stock_pool"))
            val reader = BufferedReader(InputStreamReader(stream, Charset.defaultCharset()))
            var str: String?
            str = reader.readLine()
            var count = 0
            while (str != null) {
                //Log.i("libiao", "str: $str, $count")
                //queryInfo(str.split(",")[0])
                codeList.add(str.split(",")[0])

                str = reader.readLine()
            }
            val size = codeList.size
            val avg = size / 4
            i(TAG, "avg: $avg")

            Thread{
                val data = ArrayList<ArrayList<SharesRecordActivity.ShareInfo>>()
                for(i in 0 until avg) {

                    count++
                    if(count % 10 == 0 && count < 601) {
                        val p = (count / 650F * 100).toInt()
                        mHandler.post { progressTv?.text = "$p%, ${count * 4}" }
                    }

                    analysis(codeList[i], data)
                }
                mHandler.post {
                    allData.addAll(data)
                    progressTv?.text = "$100%, $size, ${++done}"
                    loadingPb?.visibility = View.GONE
                    dataInitDone = true
                }
            }.start()

            Thread{
                val data = ArrayList<ArrayList<SharesRecordActivity.ShareInfo>>()
                for(i in avg until avg * 2) {
                    analysis(codeList[i], data)
                }
                mHandler.post {
                    allData.addAll(data)
                    progressTv?.text = "$100%, $size, ${++done}"
                    loadingPb?.visibility = View.GONE
                    dataInitDone = true
                }
            }.start()
            Thread{
                val data = ArrayList<ArrayList<SharesRecordActivity.ShareInfo>>()
                for(i in avg * 2 until avg * 3) {
                    analysis(codeList[i], data)
                }
                mHandler.post {
                    allData.addAll(data)
                }
                mHandler.post{
                    progressTv?.text = "$100%, $size, ${++done}"
                    loadingPb?.visibility = View.GONE
                    dataInitDone = true
                }
            }.start()

            Thread{
                val data = ArrayList<ArrayList<SharesRecordActivity.ShareInfo>>()
                for(i in avg * 3 until codeList.size) {
                    analysis(codeList[i], data)
                }
                mHandler.post {
                    allData.addAll(data)
                }
                i(TAG, "结束初始化")
                mHandler.post{
                    progressTv?.text = "$100%, $size, ${++done}"
                    loadingPb?.visibility = View.GONE
                    dataInitDone = true
                }
            }.start()

        }.start()
    }

    private fun analysis(code: String, dataList: ArrayList<ArrayList<SharesRecordActivity.ShareInfo>>) {
        val f = File(file_year, code)
        if(f.exists()) {
            val stream = FileInputStream(f)
            val reader = BufferedReader(InputStreamReader(stream, Charset.defaultCharset()))
            val lines = reader.readLines()
            //Log.i("libiao", "$lines")
            val shares = ArrayList<SharesRecordActivity.ShareInfo>()
            for(line in lines) {
                shares.add(SharesRecordActivity.ShareInfo(line))
            }
            dataList.add(shares)
        }
    }

    private fun clear() {
        mModeList.forEach {
            it.clear()
        }
        mAdapter?.notifyDataSetChanged()
    }

    class SharesInfoAdater(val context: Context) : RecyclerView.Adapter<SharesInfoHolder>() {

        private var mSharesInfoList: ArrayList<BaseMode> = ArrayList()

        fun setData(sharesInfoList: ArrayList<BaseMode>) {
            mSharesInfoList = sharesInfoList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SharesInfoHolder {
            return SharesInfoHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.shares_info_item,
                    null
                )
            )
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