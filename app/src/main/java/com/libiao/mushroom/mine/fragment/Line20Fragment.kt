package com.libiao.mushroom.mine.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.libiao.mushroom.R
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.kline.KLineActivity
import com.libiao.mushroom.mine.*
import com.libiao.mushroom.mine.tab.Line20Tab
import com.libiao.mushroom.room.CollectShareInfo
import com.libiao.mushroom.room.MineShareDatabase
import com.libiao.mushroom.room.MineShareInfo
import com.libiao.mushroom.thread.ThreadPoolUtil
import com.libiao.mushroom.utils.*
import kotlinx.android.synthetic.main.line_20_fragment.*
import kotlinx.android.synthetic.main.self_selection_activity.*
import okhttp3.*
import java.io.*
import java.nio.charset.Charset

class Line20Fragment: Fragment(R.layout.line_20_fragment), ICommand {

    override fun order(type: Int, data: Any?) {
        when(type) {
            ICommand.SETTING -> {
                settingDialog?.show()
            }
            ICommand.HEART -> {
                heartChecked = data as Boolean
                refreshTempData()
                resetSortUI()
                refreshCount()
            }
            ICommand.LOCAL -> {
                refreshLocalData(mData)
                resetSortUI()
            }
            ICommand.NETWORK -> {
                mRefreshCount = 0
                mTempData.forEach {
                    shiShiQuery(it)
                }
            }
        }
    }

    override fun obtain(type: Int): Any? {
        when(type) {
            ICommand.OBTAIN_SIZE -> {
                return mTempData.size
            }
        }
        return 0
    }

    companion object {
        private const val TAG = "Line20Fragment"
    }

    private var mAdapter: MyPoolInfoAdater? = null
    private var mRangeStatus = 0
    private var mTodayStatus = 0
    private var mTimeStatus = 0

    private val file_2021 = File(Environment.getExternalStorageDirectory(), "A_SharesInfo/2021")
    private val client = OkHttpClient()

    private var mData: List<MineShareInfo> = ArrayList()
    private var mTempData: List<MineShareInfo> = ArrayList()

    private var settingDialog: SelfSettingDialog? = null
    private var settingBean: SelfSettingBean? = null

    private var mRefreshCount = 0

    private var heartChecked = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogUtil.i(TAG, "onViewCreated")
        initView()
        initData()
    }

    private fun initData() {
        val data = MineShareDatabase.getInstance()?.getMineShareDao()?.getMineShares()
        data?.also {
            mData = it
            mTempData = it
        }
        refreshLocalData(mData)
    }

    private fun initView() {
        self_selection_rv?.layoutManager = LinearLayoutManager(context)
        mAdapter = MyPoolInfoAdater(context!!)
        self_selection_rv?.adapter = mAdapter

        view_share_bar.visibility = View.GONE

        view_self_range.setOnClickListener {
            when(mRangeStatus) {
                0 -> {
                    iv_self_range.setImageResource(R.mipmap.sort_descending)
                    mRangeStatus = 1
                    //高-低
                    //为什么不用temp， 主要考虑删除时要保持顺序
                    mTempData = ArrayList(mTempData.sortedByDescending { it.totalRange })
                    mAdapter?.setData(mTempData)
                }
                1 -> {
                    iv_self_range.setImageResource(R.mipmap.sort_ascending)
                    mRangeStatus = 2
                    //低-高
                    mTempData = ArrayList(mTempData.sortedBy { it.totalRange })
                    mAdapter?.setData(mTempData)
                }
                2 -> {
                    iv_self_range.setImageResource(R.mipmap.sort_descending)
                    mRangeStatus = 1
                    //高-低
                    mTempData = ArrayList(mTempData.sortedByDescending { it.totalRange })
                    mAdapter?.setData(mTempData)
                }
            }
            mTodayStatus = 0
            iv_self_today.setImageResource(R.mipmap.sort_normal)
            mTimeStatus = 0
            iv_self_time.setImageResource(R.mipmap.sort_normal)
        }

        view_self_today.setOnClickListener {
            when(mTodayStatus) {
                0 -> {
                    iv_self_today.setImageResource(R.mipmap.sort_descending)
                    mTodayStatus = 1
                    //高-低
                    mTempData = ArrayList(mTempData.sortedByDescending { it.todayRange })
                    mAdapter?.setData(mTempData)

                }
                1 -> {
                    iv_self_today.setImageResource(R.mipmap.sort_ascending)
                    mTodayStatus = 2
                    //低-高
                    mTempData = ArrayList(mTempData.sortedBy { it.todayRange })
                    mAdapter?.setData(mTempData)

                }
                2 -> {
                    iv_self_today.setImageResource(R.mipmap.sort_descending)
                    mTodayStatus = 1
                    //高-低
                    mTempData = ArrayList(mTempData.sortedByDescending { it.todayRange })
                    mAdapter?.setData(mTempData)
                }
            }
            mRangeStatus = 0
            iv_self_range.setImageResource(R.mipmap.sort_normal)
            mTimeStatus = 0
            iv_self_time.setImageResource(R.mipmap.sort_normal)
        }

        view_self_time.setOnClickListener {
            when(mTimeStatus) {
                0 -> {
                    iv_self_time.setImageResource(R.mipmap.sort_descending)
                    mTimeStatus = 1
                    //高-低
                    mTempData = ArrayList(mTempData.sortedByDescending { it.dayCount })
                    mAdapter?.setData(mTempData)
                }
                1 -> {
                    iv_self_time.setImageResource(R.mipmap.sort_ascending)
                    mTimeStatus = 2
                    //低-高
                    mTempData = ArrayList(mTempData.sortedBy { it.dayCount })
                    mAdapter?.setData(mTempData)
                }
                2 -> {
                    iv_self_time.setImageResource(R.mipmap.sort_descending)
                    mTimeStatus = 1
                    //高-低
                    mTempData = ArrayList(mTempData.sortedByDescending { it.dayCount })
                    mAdapter?.setData(mTempData)
                }
            }
            mRangeStatus = 0
            iv_self_range.setImageResource(R.mipmap.sort_normal)

            mTodayStatus = 0
            iv_self_today.setImageResource(R.mipmap.sort_normal)
        }

        settingDialog = SelfSettingDialog(context!!) {
            LogUtil.i(TAG, "$it")
            settingBean = it
            refreshTempData()
            refreshCount()
            resetSortUI()
        }
    }

    private fun refreshLocalData(data: List<MineShareInfo>) {
        //(activity as SelfSelectionActivity).showLoading()

        ThreadPoolUtil.execute(Runnable {
            data.forEach {
                val f = File(file_2021, it.code)
                if(f.exists()) {
                    val stream = FileInputStream(f)
                    val reader = BufferedReader(InputStreamReader(stream, Charset.defaultCharset()))
                    val lines = reader.readLines()
                    val one = SharesRecordActivity.ShareInfo(lines[lines.size - it.dayCount - 2])
                    val two = SharesRecordActivity.ShareInfo(lines[lines.size - it.dayCount - 1])
                    if(it.candleEntryList == null) {

                        if(it.dayCount >= 2) {
                            val three = SharesRecordActivity.ShareInfo(lines[lines.size - it.dayCount])
                            val four = SharesRecordActivity.ShareInfo(lines[lines.size - it.dayCount + 1])
                            it.historyFangLiang = ShareParseUtil.isFangLiang(one, two, three, four)
                        }

                        val records = lines.subList(lines.size - it.dayCount - 2, lines.size)
                        val entrys = java.util.ArrayList<CandleEntry>()
                        var maxPrice = one.nowPrice
                        records.forEachIndexed { index, s ->
                            val item = SharesRecordActivity.ShareInfo(s)
                            if(item.nowPrice > maxPrice) maxPrice = item.nowPrice
                            if(item.beginPrice == 0.00) {
                                entrys.add(CandleEntry((index).toFloat(), item.nowPrice.toFloat(), item.nowPrice.toFloat(), item.nowPrice.toFloat(), item.nowPrice.toFloat()))
                            } else {
                                entrys.add(CandleEntry((index).toFloat(), item.maxPrice.toFloat(), item.minPrice.toFloat(), item.beginPrice.toFloat(), item.nowPrice.toFloat()))
                            }
                        }
                        var minP = one.nowPrice
                        if(minP == 0.00) minP = two.yesterdayPrice
                        if(minP == 0.00) minP = two.beginPrice
                        it.maxRange = (maxPrice - minP) / minP * 100
                        it.candleEntryList = entrys
                    }
                    it.price = one.nowPrice
                    val info = lines.get(lines.size - 1)
                    val info_pre = lines.get(lines.size - 2)
                    val share = SharesRecordActivity.ShareInfo(info)
                    val share_pre = SharesRecordActivity.ShareInfo(info_pre)
                    if(it.dayCount >= 2) {
                        val info_pre2 = lines.get(lines.size - 3)
                        val info_pre3 = lines.get(lines.size - 4)
                        val share_pre2 = SharesRecordActivity.ShareInfo(info_pre2)
                        val share_pre3 = SharesRecordActivity.ShareInfo(info_pre3)
                        if(share.nowPrice >= share.beginPrice
                            && share_pre.beginPrice > share_pre.nowPrice
                            && share_pre2.beginPrice > share_pre2.nowPrice
                            && share_pre3.beginPrice > share_pre3.nowPrice
                        ) {
                            it.zhiDie = true
                        }
                    }

                    updateCurrentData(it, share, share_pre)

                    //LogUtil.i(TAG, "info: $info")
                    //LogUtil.i(TAG, "info_pre: $info_pre")
                }
            }
            ThreadPoolUtil.executeUI(Runnable {
                refreshTempData()
                refreshCount()
                view_share_bar.visibility = View.VISIBLE
            })
        })
    }

    private fun refreshTempData() {
        val temp  = ArrayList<MineShareInfo>()
        for (info in mData) {

            if(heartChecked && !info.heart) {
                continue
            }
            val selfSettingBean = settingBean
            if(selfSettingBean?.timeChecked == true && info.dayCount < selfSettingBean.timeValue) {
                continue
            }

            if(selfSettingBean?.rangeLeftChecked == true && info.todayRange < selfSettingBean.rangeLeftValue) {
                continue
            }

            if(selfSettingBean?.rangeRightChecked == true && info.todayRange > selfSettingBean.rangeRightValue) {
                continue
            }

            if(selfSettingBean?.liangLeftChecked == true && info.todayLiang < selfSettingBean.liangLeftValue) {
                continue
            }

            if(selfSettingBean?.liangRightChecked == true && info.todayLiang > selfSettingBean.liangRightValue) {
                continue
            }

            if(selfSettingBean?.redLine == true && !info.redLine) {
                continue
            }

            if(selfSettingBean?.fangLiang == true && !info.historyFangLiang) {
                continue
            }

            if(selfSettingBean?.maxRangChecked == true && info.maxRange < selfSettingBean.maxRangValue) {
                continue
            }

            if(selfSettingBean?.zhiDie == true && !info.zhiDie) {
                continue
            }

            temp.add(info)
        }
        mTempData = temp
        mAdapter?.setData(mTempData)
    }

    private fun deleteItem(mineShareInfo: MineShareInfo?) {
        (mData as ArrayList).remove(mineShareInfo)
        (mTempData as ArrayList).remove(mineShareInfo)
        mAdapter?.setData(mTempData)
        refreshCount()
    }

    private fun refreshCount() {
        (activity as SelfSelectionActivity).notifyData(Line20Tab.TAG, mTempData.size)
    }

    private fun updateCurrentData(
        it: MineShareInfo,
        share: SharesRecordActivity.ShareInfo,
        sharePre: SharesRecordActivity.ShareInfo
    ) {
        if(it.price > 0) {
            it.totalRange = (share.nowPrice - it.price) / it.price * 100
            it.todayRange = share.range
            it.fangLiang = share.totalPrice > sharePre.totalPrice
            it.todayLiang = String.format("%.2f",share.totalPrice / 100000000).toDouble()
            if(share.nowPrice > share.beginPrice) {
                it.redLine = true
            } else if(share.nowPrice == share.beginPrice) {
                it.redLine = share.range >= 0
            } else {
                it.redLine = false
            }
            if(it.redLine && it.fangLiang && it.todayRange > 0) {
                if(it.todayRange <= 6) {
                    it.priority = 4
                } else {
                    it.priority = 3
                }
            } else if(it.redLine) {
                it.priority = 2
            } else {
                it.priority = 1
            }
            var liangBi = "0"
            if(sharePre.totalPrice > 0) {
                liangBi = baoLiuXiaoShu(share.totalPrice / sharePre.totalPrice)
            }
            it.moreInfo = "${share.rangeBegin},  ${share.rangeMin},  ${share.rangeMax},  ${String.format("%.2f",share.totalPrice / 100000000)}亿,  ${liangBi}"
            it.heart = MineShareDatabase.getInstance()?.getCollectShareDao()?.find(it.code!!) ?: false
        }
    }

    private fun shiShiQuery(info: MineShareInfo) {
        val code = info.code
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
                            val share = ShareParseUtil.parseFromNetwork(code!!, strs)
                            LogUtil.i(TAG, "share: $share")
                            val f = File(file_2021, code)
                            if(f.exists()) {
                                val stream = FileInputStream(f)
                                val reader = BufferedReader(InputStreamReader(stream, Charset.defaultCharset()))
                                val lines = reader.readLines()
                                val share_pre = SharesRecordActivity.ShareInfo(lines.last())
                                updateCurrentData(info, share, share_pre)
                            }
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
                ThreadPoolUtil.executeUI(Runnable {
                    mRefreshCount ++
                    tv_counting.text = "$mRefreshCount"
                    LogUtil.i(TAG, "mRefreshCount: $mRefreshCount")
                    if(mRefreshCount == mTempData.size) {
                        mAdapter?.setData(mTempData)
                        self_loading.visibility = View.GONE
                        btn_refresh.isEnabled = true
                        cb_network.isEnabled = true
                        tv_counting.visibility = View.GONE
                        resetSortUI()
                    }
                })
            }
        })
    }

    private fun resetSortUI() {
        mTodayStatus = 0
        mRangeStatus = 0
        mTimeStatus = 0
        iv_self_range.setImageResource(R.mipmap.sort_normal)
        iv_self_today.setImageResource(R.mipmap.sort_normal)
        iv_self_time.setImageResource(R.mipmap.sort_normal)
    }

    inner class MyPoolInfoAdater(val context: Context) : RecyclerView.Adapter<MyPoolHolder>() {

        private var mSharesInfoList: List<MineShareInfo> = ArrayList()


        fun setData(sharesInfoList: List<MineShareInfo>) {
            mSharesInfoList = sharesInfoList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPoolHolder {
            return MyPoolHolder(context,
                LayoutInflater.from(context).inflate(
                    R.layout.self_selection_item,
                    null
                )
            )
        }

        override fun getItemCount(): Int {
            return mSharesInfoList.size
        }

        override fun onBindViewHolder(holder: MyPoolHolder, position: Int) {
            holder.bindData(position, mSharesInfoList[position])
        }
    }

    inner class MyPoolHolder(context: Context, view: View) : RecyclerView.ViewHolder(view) {

        private var mIdTv: TextView? = null
        private var mNameTv: TextView? = null
        private var mCodeTv: TextView? = null
        private var mTimeTv: TextView? = null
        private var mRangeTv: TextView? = null
        private var mTodayTv: TextView? = null

        private var moreInfoTv: TextView? = null
        private var heartIv: ImageView? = null

        private var mineShareInfo: MineShareInfo? = null
        private var chart: CandleStickChart? = null

        private var heart: Boolean = false

        private var mTypeFangLiangTv: TextView? = null
        private var mTypeMaxRangeTv: TextView? = null
        private var mTypeZhiDieTv: TextView? = null


        init {
            mIdTv = view.findViewById(R.id.self_item_id)
            mTimeTv = view.findViewById(R.id.self_item_time)
            mNameTv = view.findViewById(R.id.self_item_name)
            mCodeTv = view.findViewById(R.id.self_item_code)
            mRangeTv = view.findViewById(R.id.self_item_range)
            mTodayTv = view.findViewById(R.id.self_item_today)
            mTypeFangLiangTv = view.findViewById(R.id.tv_type_fangliang)
            mTypeMaxRangeTv = view.findViewById(R.id.tv_type_max_rang)
            mTypeZhiDieTv = view.findViewById(R.id.tv_type_zhi_die)

            moreInfoTv = view.findViewById(R.id.tv_more_info)
            heartIv = view.findViewById(R.id.iv_item_heart)
            chart = view.findViewById(R.id.item_candler_chart)
            initCandleChart()

            view.setOnClickListener {
                val intent = Intent(context, KLineActivity::class.java)
                intent.putExtra("code", mineShareInfo?.code)
                intent.putExtra("info", mineShareInfo.toString())
                context.startActivity(intent)
            }

            view.setOnLongClickListener {
                LogUtil.i(TAG, "长按")
                MoreDialog(context){
                    when(it.id) {
                        R.id.btn_dialog_delete -> {
                            mineShareInfo?.code?.also {
                                MineShareDatabase.getInstance()?.getMineShareDao()?.delete(it)
                                deleteItem(mineShareInfo)
                            }
                        }
                    }
                }.show()
                true
            }

            mIdTv?.setOnClickListener {
                ClipboardUtil.clip(context, mineShareInfo?.code)
            }

            mTimeTv?.setOnClickListener {
                //http://image.sinajs.cn/newchart/min/n/sh000001.gif
                FenShiDialog(context, mineShareInfo?.code!!).show()
            }

            heartIv?.setOnClickListener {
                heart = !heart
                mineShareInfo?.heart = heart
                if(heart) {
                    heartIv?.setImageResource(R.mipmap.heart_selected)
                    val info = CollectShareInfo()
                    info.code = mineShareInfo?.code
                    info.time = System.currentTimeMillis()
                    info.name = mineShareInfo?.name
                    MineShareDatabase.getInstance()?.getCollectShareDao()?.insert(info)
                } else {
                    heartIv?.setImageResource(R.mipmap.heart)
                    MineShareDatabase.getInstance()?.getCollectShareDao()?.delete(mineShareInfo?.code!!)
                }
            }
        }

        private fun initCandleChart() {
            chart?.also {
                it.setTouchEnabled(false)
                it.description.isEnabled = false
                it.setDrawGridBackground(false) // 是否显示表格颜色
                //it.setBackgroundColor(Color.WHITE) // 设置背景
                it.setPinchZoom(false) // if disabled, scaling can be done on x- and y-axis separately
                it.isLogEnabled = true
                it.isSelected = false

                val xAxis: XAxis = it.xAxis
                xAxis.isEnabled = false

                val leftAxis: YAxis = it.axisLeft
                leftAxis.isEnabled = false

                val rightAxis: YAxis = it.axisRight
                rightAxis.isEnabled = false

                val l: Legend = it.legend // 设置比例图标示
                l.isEnabled = false //决定显不显示标签

            }
        }

        fun bindData(position: Int, info: MineShareInfo) {
            //LogUtil.i(TAG, "bindData: ${info.code}, ${info.candleEntryList?.size}")
            this.mineShareInfo = info
            mIdTv?.text = position.toString()
            mTimeTv?.text = info.time?.substring(5) + "(${info.dayCount})"
            mNameTv?.text = info.name
            mCodeTv?.text = info.code?.substring(2)

            mRangeTv?.text = baoLiuXiaoShu(info.totalRange)
            if(info.totalRange >= 0) {
                mRangeTv?.setTextColor(Color.parseColor("#f15b6c"))
            } else {
                mRangeTv?.setTextColor(Color.parseColor("#7fb80e"))
            }
            mTodayTv?.text = info.todayRange.toString()

            if(info.priority >= 3) {
                mTodayTv?.setTextColor(Color.parseColor("#ed1941"))
            } else if(info.priority == 2) {
                mTodayTv?.setTextColor(Color.parseColor("#80ed1941"))
            } else {
                mTodayTv?.setTextColor(Color.parseColor("#7fb80e"))
            }
            moreInfoTv?.text = info.moreInfo

            info.candleEntryList?.also {
                val data = CandleData(getCanleDataSet(it))
                val params = chart?.layoutParams
                params?.width = getWidth(it.size)
                chart?.layoutParams = params
                chart?.data = data
                chart?.invalidate()
            }

            heart = info.heart
            if(heart) {
                heartIv?.setImageResource(R.mipmap.heart_selected)
            } else {
                heartIv?.setImageResource(R.mipmap.heart)
            }

            if(info.historyFangLiang) {
                mTypeFangLiangTv?.visibility = View.VISIBLE
            } else {
                mTypeFangLiangTv?.visibility = View.GONE
            }

            if(info.zhiDie) {
                mTypeZhiDieTv?.visibility = View.VISIBLE
            } else {
                mTypeZhiDieTv?.visibility = View.GONE
            }

            mTypeMaxRangeTv?.text = baoLiuXiaoShu(info.maxRange)
        }

        private fun getWidth(size: Int): Int {
            if(size == 2) return 120
            if(size == 3) return 140
            var w:Int
            if(size > 10) {
                w = 300 + (size - 10) * 20
            } else {
                w = size * 30
            }
            if(w < 150) w = 150
            if(w > 800) w = 800
            return w
        }

        private fun getCanleDataSet(values: List<CandleEntry>): CandleDataSet {
            val set1 = CandleDataSet(values, "")
            set1.formSize = 0f
            set1.isHighlightEnabled = false
            set1.enableDashedHighlightLine(10f, 5f, 0f)
            set1.setDrawValues(false)
            set1.setDrawIcons(false)
            set1.shadowColorSameAsCandle = true
            //set1.shadowColor = Color.BLUE
            set1.shadowWidth = 1f
            set1.decreasingColor = Color.GREEN
            set1.decreasingPaintStyle = Paint.Style.FILL
            set1.increasingColor = Color.RED
            set1.increasingPaintStyle = Paint.Style.STROKE
            set1.neutralColor = Color.RED
            //set1.highLightColor = Color.BLACK
            return set1
        }
    }
}