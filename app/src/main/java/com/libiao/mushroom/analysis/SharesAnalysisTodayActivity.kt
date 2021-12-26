package com.libiao.mushroom.analysis

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.*
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.libiao.mushroom.R
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.kline.KLineActivity
import com.libiao.mushroom.mode.*
import com.libiao.mushroom.setting.SettingActivity
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class SharesAnalysisTodayActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SharesAnalysisTodayActivity"
    }

    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: SharesInfoAdater? = null
    private var mModeList: ArrayList<BaseMode> = ArrayList()

    private val mHandler = Handler(Looper.getMainLooper())

    private var settingIv: ImageView? = null
    private var timeTv: TextView? = null
    private var progressTv: TextView? = null
    private var loadingPb: ProgressBar? = null
    private var bigPowerBtn: Button? = null
    private var oneDayTime: TextView? = null

    private val file = File(Environment.getExternalStorageDirectory(), "A_SharesInfo")
    private val file_2021 = File(file, "2021")

    private var time = "2021-4-30"

    private val allData: ArrayList<ArrayList<SharesRecordActivity.ShareInfo>> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.more_and_less_activity)
        title = "2021"

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")// HH:mm:ss
        time = simpleDateFormat.format(Date())

        i(TAG, "t: $time")

        timeTv = findViewById(R.id.time)
        settingIv = findViewById(R.id.iv_setting)

        mRecyclerView = findViewById(R.id.recycler_view)
        mRecyclerView?.layoutManager = LinearLayoutManager(this)
        mAdapter = SharesInfoAdater(this)
        mRecyclerView?.adapter = mAdapter

        progressTv = findViewById(R.id.tv_progress)
        loadingPb = findViewById(R.id.loading)
        bigPowerBtn = findViewById(R.id.btn_start_analysis)
        oneDayTime = findViewById(R.id.tv_before_one_day_time)

        timeTv?.text = "日期：$time"

        settingIv?.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }

        chooseDate()
        initData()
    }

    private fun chooseDate() {
        val f = File(file_2021, "sz000001")
        var lines : List<String>? = null
        if(f.exists()) {
            val stream = FileInputStream(f)
            val reader = BufferedReader(InputStreamReader(stream, Charset.defaultCharset()))
            lines = reader.readLines()
        }
        timeTv?.setOnClickListener {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                val dialog = DatePickerDialog(this)
                dialog.setOnDateSetListener { view, year, month, dayOfMonth ->
                    i(TAG, "$year, $month, $dayOfMonth")
                    timeTv?.text = "日期：$year-${month + 1}-$dayOfMonth"

                    Constant.PRE = 0
                    lines?.forEachIndexed{ index, s ->
                        val values = s.split(",")[0].split("-")
                        if(values.size > 2) {
                            val y = values[0].toInt()
                            val m = values[1].toInt()
                            val d = values[2].toInt()
                            i(TAG, "y: $y, m: $m, d:$d")
                            if(year == y && m == month+1 && d == dayOfMonth) {
                                Constant.PRE = lines.size - index - 1
                                i(TAG, "pre: ${Constant.PRE}")
                                return@setOnDateSetListener
                            }
                        }
                    }
                }
                dialog.show()
            }
        }
    }

    private fun initData() {


//        mModeList.add(MoreShizixingMode())
//        mModeList.add(MoreMildMode())
//        mModeList.add(HorizontalPlateMode())

        //mModeList.add(CloseToLine10Mode())
//        mModeList.add(CloseToLine20Mode())


//        mModeList.add(ShangYinXian2FitMode())

//        mModeList.add(MoreMoreMode3())

//        mModeList.add(MoreMoreMode5())

//        mModeList.add(UpLine5Mode())
//        mModeList.add(UpLine10Mode())


        //mModeList.add(DuanCengMode())

        //mModeList.add(ChuangYeBanTouJiMode())
        //        mModeList.add(StrongStock10Mode())
        //mModeList.add(StrongStockMode2())
        //mModeList.add(MoreMoreMode4())


        //mModeList.add(LeaderMode())
        mModeList.add(StrongStock50Mode())
        mModeList.add(Strong20Mode())
        mModeList.add(StrongStock10Mode())
        mModeList.add(YiZiBanMode())
        mModeList.add(MoreMore3Mode())
        mModeList.add(Difference2FitMode())
        mModeList.add(UpLine10Mode())
        mModeList.add(LianBan2Mode())
        mModeList.add(LianBan3Mode())
        mModeList.add(LianBan4Mode())
        mModeList.add(LianBanChuang1Mode())
        mModeList.add(LianBanChuang2Mode())
        mModeList.add(LianBan2StrongMode())
        mModeList.add(LianBan3StrongMode())
        mModeList.add(LianBanChuang1StrongMode())
        mModeList.add(LianBan1StrongMode())
        mModeList.add(MineMode())


        val tempList = ArrayList<BaseMode>()
        mModeList.forEach {
            if(it.shouldAnalysis(this)) {
                tempList.add(it)
            }
        }
        mModeList = tempList
    }
    var done = 0
    private fun start() {
        done = 0
        Thread {
            i(TAG, "开始查询")

            val stream = FileInputStream(File(file, "stock_pool"))
            val reader = BufferedReader(InputStreamReader(stream, Charset.defaultCharset()))
            var str: String?
            str = reader.readLine()
            val codeList = ArrayList<String>()
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
                    findOutFitMode()
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
                    findOutFitMode()
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
                    findOutFitMode()
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
                mHandler.post{
                    progressTv?.text = "$100%, $size, ${++done}"
                    loadingPb?.visibility = View.GONE
                    findOutFitMode()

                }
            }.start()

        }.start()
    }

    private fun findOutFitMode() {
        if(done == 4) {
            i(TAG, "结束查询: ${allData.size}")
            if(allData.size > 0) {
                oneDayTime?.text = allData[0][allData[0].size - Constant.PRE - 1].time
            }

            allData.forEach {shares ->
                mModeList.forEach {
                    it.analysis(shares)
                }
            }
            mAdapter?.setData(mModeList)
            bigPowerBtn?.isEnabled = true
        }
    }

    private fun analysis(code: String, dataList: ArrayList<ArrayList<SharesRecordActivity.ShareInfo>>) {
        val f = File(file_2021, code)
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

    private fun analysis(code: String) {
        val f = File(file_2021, code)
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

    fun beforeOneDay(v: View) {
        if(allData.size == 0) return
        Constant.PRE++
        mModeList.forEach {
            it.clear()
        }
        mAdapter?.notifyDataSetChanged()
        findOutFitMode()
    }

    fun afterOneDay(v: View) {
        if(allData.size == 0) return
        Constant.PRE--
        if(Constant.PRE < 0) {
            Constant.PRE = 0
            return
        }
        mModeList.forEach {
            it.clear()
        }
        mAdapter?.notifyDataSetChanged()
        findOutFitMode()
    }

    fun startAnalysis(view: View) {
        mModeList.forEach {
            it.clear()
        }
        mAdapter?.notifyDataSetChanged()
        if(allData.size > 0) {
            findOutFitMode()
            return
        }
        start()
        //test()
        loadingPb?.visibility = View.VISIBLE
        bigPowerBtn?.isEnabled = false
    }

    private fun test() {
        analysis("sz000799")
    }

    class SharesInfoAdater(val context: Context) : RecyclerView.Adapter<SharesInfoHolder>() {

        private var mSharesInfoList: ArrayList<BaseMode> = ArrayList()

        fun setData(sharesInfoList: ArrayList<BaseMode>) {
            mSharesInfoList = sharesInfoList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SharesInfoHolder {
            return SharesInfoHolder(context,
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

    class SharesInfoHolder(context: Context, view: View) : RecyclerView.ViewHolder(view) {

        private var mTitleTv: TextView? = null
        private var mData: ArrayList<SharesRecordActivity.ShareInfo> = ArrayList()
        private var mRv: RecyclerView? =  null
        private var adapter: ShareItemAdater? = null

        init {
            mTitleTv = view.findViewById(R.id.title)
            mTitleTv?.setOnClickListener {
                val intent = Intent(context, SharesRealTimeInfoActivity::class.java)
                //i(TAG, "$mData")
                intent.putParcelableArrayListExtra("data", mData)
                context.startActivity(intent)
            }
            mRv = view.findViewById(R.id.rv)
            mRv?.layoutManager = LinearLayoutManager(context)
            adapter = ShareItemAdater(context)
            mRv?.adapter = adapter

        }

        fun bindData(info: BaseMode) {
            mData.clear()
            itemView.visibility = View.VISIBLE
            info.mFitModeList.sortByDescending { it.first }
            info.mFitModeList.forEach {
                it.second?.also {shareInfo ->
                    mData.add(shareInfo)
                }
            }
            mTitleTv?.text = info.des()
            //mContentTv?.text = info.content()
            adapter?.setData(mData)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Constant.PRE = 0
    }



    class ShareItemHolder(context: Context, view: View) : RecyclerView.ViewHolder(view) {

        private var mItemContentTv: TextView? = null
        private var itemInfo: SharesRecordActivity.ShareInfo? = null

        init {
            mItemContentTv = view.findViewById(R.id.item_content)
            mItemContentTv?.setOnClickListener {
                val intent = Intent(context, KLineActivity::class.java)
                intent.putExtra("code", itemInfo?.code)
                intent.putExtra("info", "${itemInfo?.time}  ${itemInfo?.code}  ${itemInfo?.name}")
                context.startActivity(intent)
            }

        }

        fun bindData(info: SharesRecordActivity.ShareInfo) {
            itemInfo = info
            mItemContentTv?.text = info.simpleInfo()
        }
    }

    class ShareItemAdater(val context: Context) : RecyclerView.Adapter<ShareItemHolder>() {

        private var mData: ArrayList<SharesRecordActivity.ShareInfo> = ArrayList()

        fun setData(data: ArrayList<SharesRecordActivity.ShareInfo>) {
            mData = data
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShareItemHolder {
            return ShareItemHolder(context,
                LayoutInflater.from(context).inflate(
                    R.layout.share_item,
                    null
                )
            )
        }

        override fun getItemCount(): Int {
            return mData.size
        }

        override fun onBindViewHolder(holder: ShareItemHolder, position: Int) {
            holder.bindData(mData.get(position))
        }
    }
}
