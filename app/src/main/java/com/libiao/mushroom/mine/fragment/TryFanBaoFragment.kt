package com.libiao.mushroom.mine.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.libiao.mushroom.R
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.kline.KLineActivity
import com.libiao.mushroom.mine.*
import com.libiao.mushroom.mine.tab.FanBaoTab
import com.libiao.mushroom.room.report.ReportShareDatabase
import com.libiao.mushroom.room.report.ReportShareInfo
import com.libiao.mushroom.thread.ThreadPoolUtil
import com.libiao.mushroom.utils.*
import kotlinx.android.synthetic.main.try_fan_bao_fragment.*
import kotlinx.android.synthetic.main.xin_gao_fragment.*
import kotlinx.android.synthetic.main.xin_gao_fragment.btn_start
import kotlinx.android.synthetic.main.xin_gao_fragment.cb_fang_liang
import kotlinx.android.synthetic.main.xin_gao_fragment.cb_first_xin_gao
import kotlinx.android.synthetic.main.xin_gao_fragment.cb_line_20
import kotlinx.android.synthetic.main.xin_gao_fragment.cb_tu_po
import kotlinx.android.synthetic.main.xin_gao_fragment.cb_xin_gao_count
import kotlinx.android.synthetic.main.xin_gao_fragment.cb_yang_yin
import kotlinx.android.synthetic.main.xin_gao_fragment.cb_yang_yin_one
import kotlinx.android.synthetic.main.xin_gao_fragment.cb_yin_xian_one
import kotlinx.android.synthetic.main.xin_gao_fragment.cb_zhang_fu
import kotlinx.android.synthetic.main.xin_gao_fragment.iv_self_range
import kotlinx.android.synthetic.main.xin_gao_fragment.iv_self_time
import kotlinx.android.synthetic.main.xin_gao_fragment.iv_self_today
import kotlinx.android.synthetic.main.xin_gao_fragment.self_selection_rv
import kotlinx.android.synthetic.main.xin_gao_fragment.tv_id
import kotlinx.android.synthetic.main.xin_gao_fragment.tv_ke_xuan
import kotlinx.android.synthetic.main.xin_gao_fragment.view_self_range
import kotlinx.android.synthetic.main.xin_gao_fragment.view_self_time
import kotlinx.android.synthetic.main.xin_gao_fragment.view_self_today
import kotlinx.android.synthetic.main.xin_gao_fragment.view_share_bar
import kotlinx.android.synthetic.main.xin_gao_fragment.yang_yin_child_view
import kotlinx.android.synthetic.main.xin_gao_fragment.yin_xian_child_view
import java.io.*
import java.nio.charset.Charset
import kotlin.collections.ArrayList
import kotlin.math.max
import kotlin.math.min

class TryFanBaoFragment: BaseFragment(R.layout.try_fan_bao_fragment), ICommand {

    override fun order(type: Int, data: Any?) {
        when(type) {
            ICommand.SETTING -> {
                //settingDialog?.show()
            }
            ICommand.HEART -> {
                heartChecked = data as Boolean
                refreshTempData()
                resetSortUI()
                refreshCount()
            }
            ICommand.LOCAL -> {
                onLineChecked = data as Boolean
                if(!onLineChecked) {
                    refreshLocalData(mData, true)
                    resetSortUI()
                }
            }
            ICommand.NETWORK -> {
                mRefreshCount = 0
                mTempData.forEach {
                    shiShiQuery(it.code!!) {share ->
                        val f = File(file_2023, it.code!!)
                        if(f.exists()) {
                            val stream = FileInputStream(f)
                            val reader = BufferedReader(InputStreamReader(stream, Charset.defaultCharset()))
                            val lines = reader.readLines()
                            val share_pre = SharesRecordActivity.ShareInfo(lines.last())
                            val index = it.candleEntryList?.size ?: 0
                            if(share.beginPrice == 0.00) {
                                it.candleEntryList?.add(CandleEntry((index).toFloat(), share.nowPrice.toFloat(), share.nowPrice.toFloat(), share.nowPrice.toFloat(), share.nowPrice.toFloat()))
                            } else {
                                it.candleEntryList?.add(CandleEntry((index).toFloat(), share.maxPrice.toFloat(), share.minPrice.toFloat(), share.beginPrice.toFloat(), share.nowPrice.toFloat()))
                            }

                            val p = share.totalPrice.toFloat() / 100000000
                            it.barEntryList?.add(BarEntry(index.toFloat(), p))

                            it.colorsList?.add(Color.GRAY)

                            updateCurrentData(it, share, share_pre, true)
                        }
                    }
                }
            }
        }
    }

    override fun obtain(type: Int): Any? {
        when(type) {
            ICommand.OBTAIN_SIZE -> {
                return mTempData.size
            }
            ICommand.LOADING_STATUS -> {
                return loadingStatus
            }
            ICommand.HEART_STATUS -> {
                return heartChecked
            }
            ICommand.ONLINE_STATUS -> {
                return onLineChecked
            }
        }
        return 0
    }

    companion object {
        private const val TAG = "TryFanBaoFragment"
        var recordTuPo = false
    }

    private var mAdapter: MyPoolInfoAdater? = null
    private var mRangeStatus = 0
    private var mTodayStatus = 0
    private var mTimeStatus = 0

    private var xinGaoCount = false
    private var tuPoChecked = false
    private var isYangYinOne = false
    private var firstXinGaoChecked = false

    private var mData: List<ReportShareInfo> = ArrayList()
    private var mTempData: List<ReportShareInfo> = ArrayList()

    private var settingDialog: SelfSettingDialog? = null
    private var settingBean: SelfSettingBean? = SelfSettingBean()

    private var currentCb = 1

    private var dayCount = 20

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogUtil.i(TAG, "onViewCreated")
        initView()
    }

    private fun initData() {
        val data = ReportShareDatabase.getInstance()?.getReportShareDao()?.getSharesTest("7")
        data?.also {
            mData = it
            mTempData = it
        }
        refreshLocalData(mData)
    }

    private fun initView() {
        cb_zhang_fu.isChecked = true
        cb_zhang_fu.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                currentCb = 1
                tv_ke_xuan.text = "涨幅"
                mAdapter?.changeCurrentCb(currentCb)
                //yin_xian_child_view.visibility = View.GONE
                yang_yin_child_view.visibility = View.GONE
            }

        }
        cb_xin_gao_count.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                currentCb = 2
                tv_ke_xuan.text = "新高"
                mAdapter?.changeCurrentCb(currentCb)
                yin_xian_child_view.visibility = View.VISIBLE
                yang_yin_child_view.visibility = View.GONE
            }
        }
        cb_yin_xian_one.setOnCheckedChangeListener { buttonView, isChecked ->
            xinGaoCount = isChecked
            refreshTempData()
            resetSortUI()
            refreshCount()
        }
        cb_tu_po.setOnCheckedChangeListener { buttonView, isChecked ->
            tuPoChecked = isChecked
            refreshTempData()
            resetSortUI()
            refreshCount()
        }
        cb_fang_liang.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                currentCb = 3
                tv_ke_xuan.text = "放量"
                mAdapter?.changeCurrentCb(currentCb)
                yin_xian_child_view.visibility = View.GONE
                yang_yin_child_view.visibility = View.GONE
            }
        }
        cb_yang_yin.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                currentCb = 4
                tv_ke_xuan.text = "阳阴"
                mAdapter?.changeCurrentCb(currentCb)
                yin_xian_child_view.visibility = View.GONE
                yang_yin_child_view.visibility = View.VISIBLE
            }
        }
        cb_yang_yin_one.setOnCheckedChangeListener { buttonView, isChecked ->
            isYangYinOne = isChecked
            refreshTempData()
            resetSortUI()
            refreshCount()
        }
        cb_line_20.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                currentCb = 5
                tv_ke_xuan.text = "线差"
                mAdapter?.changeCurrentCb(currentCb)
                yin_xian_child_view.visibility = View.GONE
                yang_yin_child_view.visibility = View.GONE
            }
        }

        cb_first_xin_gao.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                firstXinGaoChecked = isChecked
                refreshTempData()
                resetSortUI()
                refreshCount()
            }
        }

        tv_id?.setOnClickListener {
            mAdapter?.changeShiTu()
        }

        btn_qing_kong.setOnClickListener {
            ReportShareDatabase.getInstance()?.getReportShareDao()?.deleteByExt("7")
            Toast.makeText(context, "删除完成", Toast.LENGTH_SHORT).show()
        }

        self_selection_rv?.layoutManager = LinearLayoutManager(context)
        mAdapter = MyPoolInfoAdater(context!!)
        self_selection_rv?.adapter = mAdapter

        view_share_bar.visibility = View.GONE

        view_self_range.setOnClickListener {
            LogUtil.i(TAG, "currentCb: $currentCb, mRangeStatus: $mRangeStatus")
            when(mRangeStatus) {
                0 -> {
                    iv_self_range.setImageResource(R.mipmap.sort_descending)
                    mRangeStatus = 1
                    //高-低
                    //为什么不用temp， 主要考虑删除时要保持顺序
                    when(currentCb) {
                        1 -> mTempData = ArrayList(mTempData.sortedByDescending { it.maxCount })
                        2 -> mTempData = ArrayList(mTempData.sortedByDescending { it.maxCount})
                        3 -> mTempData = ArrayList(mTempData.sortedByDescending { it.maxCount })
                        4 -> mTempData = ArrayList(mTempData.sortedByDescending { it.maxCount })
                        5 -> mTempData = ArrayList(mTempData.sortedByDescending { it.maxCount })
                    }
                    mAdapter?.setData(mTempData)
                }
                1 -> {
                    iv_self_range.setImageResource(R.mipmap.sort_ascending)
                    mRangeStatus = 2
                    //低-高
                    when(currentCb) {
                        1 -> mTempData = ArrayList(mTempData.sortedBy { it.maxCount })
                        2 -> mTempData = ArrayList(mTempData.sortedBy { it.maxCount })
                        3 -> mTempData = ArrayList(mTempData.sortedBy { it.maxCount })
                        4 -> mTempData = ArrayList(mTempData.sortedBy { it.maxCount })
                        5 -> mTempData = ArrayList(mTempData.sortedBy { it.maxCount })
                    }
                    mAdapter?.setData(mTempData)
                }
                2 -> {
                    iv_self_range.setImageResource(R.mipmap.sort_descending)
                    mRangeStatus = 1
                    //高-低
                    when(currentCb) {
                        1 -> mTempData = ArrayList(mTempData.sortedByDescending { it.maxCount })
                        2 -> mTempData = ArrayList(mTempData.sortedByDescending { it.maxCount })
                        3 -> mTempData = ArrayList(mTempData.sortedByDescending { it.maxCount })
                        4 -> mTempData = ArrayList(mTempData.sortedByDescending { it.maxCount })
                        5 -> mTempData = ArrayList(mTempData.sortedByDescending { it.maxCount })
                    }
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
            if(it.type == 1) {
                LogUtil.i(TAG, "mTempData.size: ${mTempData.size}")

                val dataOld = ReportShareDatabase.getInstance()?.getReportShareDao()?.getShares()
                LogUtil.i(TAG, "fetchReportInfo: ${dataOld?.size}")

                mTempData.forEach {
                    val code = it.code

                    val info = ReportShareInfo()
                    info.code = it.code
                    info.time = it.time
                    info.name = it.name
                    info.updateTime = it.updateTime
                    LogUtil.i(TAG, "新增: ${info.code}")
                    ReportShareDatabase.getInstance()?.getReportShareDao()?.insert(info)

//                    if(!isContain(dataOld, code)) {
//
//                    }
                }

//                dataOld?.forEach {
//                    if(!isContain2(mTempData, it.code)) {
//                        it.delete = 1
//                        LogUtil.i(TAG, "标记删除: ${it.code}")
//                        ReportShareDatabase.getInstance()?.getReportShareDao()?.update(it)
//                    }
//                }

            } else if(it.type == 2){
                mTempData.forEach {
                    val code = it.code
                    val info = ReportShareInfo()
                    info.code = it.code
                    info.time = it.time
                    info.name = it.name
                    info.updateTime = it.updateTime
                    info.ext5 = "1"
                    LogUtil.i(TAG, "新增: ${info.code}")
                    ReportShareDatabase.getInstance()?.getReportShareDao()?.insert(info)
                }
            } else {
                settingBean = it
                refreshTempData()
                refreshCount()
                resetSortUI()
            }
        }

        btn_start.setOnClickListener {
            btn_start.visibility = View.GONE
            initData()
        }
    }

    private fun refreshLocalData(data: List<ReportShareInfo>, reload: Boolean = false) {
        //(activity as SelfSelectionActivity).showLoading()
        loadingStatus = 1
        ThreadPoolUtil.execute(Runnable {
            data.forEach {
                val f = File(file_2023, it.code)
                if(f.exists()) {
                    val stream = FileInputStream(f)
                    val reader = BufferedReader(InputStreamReader(stream, Charset.defaultCharset()))
                    val lines = reader.readLines()
                    var allPrice = 0.00
                    var allDay = 0

                    var begin = 0

                    lines.forEachIndexed{index, s ->
                        if(s.startsWith(it.time!!)) {
                            begin = index
                            return@forEachIndexed
                        }
                    }

                    var maxPrice = 9999.99
                    var maxNowPrice = 9999.99
                    val xinGaoList = ArrayList<String>()
                    val xinGaoIndexList = ArrayList<Int>()
                    var black = false

                    var lastOne = false
                    var lastTwo = false

                    var lastOneMaxNowPrice = false

                    if(it.candleEntryList == null || reload) {
                        var blackIndex = dayCount
                        var start = begin - dayCount
                        if(start < 0) {
                            start = 0
                            blackIndex = begin
                        }
                        it.startIndex = blackIndex
                        val records = lines.subList(start, lines.size)
                        val entrys = java.util.ArrayList<CandleEntry>()
                        val barEntrys = java.util.ArrayList<BarEntry>()
                        val colorEntrys = java.util.ArrayList<Int>()

                        val values_5 = java.util.ArrayList<Entry>()
                        val values_10 = java.util.ArrayList<Entry>()
                        val values_20 = java.util.ArrayList<Entry>()

                        var tempItem: SharesRecordActivity.ShareInfo? = null
                        var greenLittle = true

                        var yang = 0
                        var yin = 0



                        records.forEachIndexed { index, s ->
                            val item = SharesRecordActivity.ShareInfo(s)

                            if(index > 0) {
                                if(item.totalPrice > 0) {
                                    allPrice += item.totalPrice
                                    allDay ++
                                }
                                if(item.beginPrice > 0.00) {
                                    if(item.nowPrice > item.beginPrice) {
                                        yang++
                                    } else if(item.nowPrice == item.beginPrice) {
                                        if(item.range > 0) {
                                            yang++
                                        } else {
                                            yin++
                                        }
                                    } else {
                                        yin++
                                    }
                                }
                            }


                            if(tempItem == null) {
                                tempItem = item
                            } else {
                                if(greenLittle) {
                                    if(item.beginPrice > item.nowPrice && tempItem!!.nowPrice >= tempItem!!.beginPrice && item.totalPrice > tempItem!!.totalPrice * 1.1) {
                                        greenLittle = false
                                    }
                                }
                                tempItem = item
                            }

                            if(item.beginPrice == 0.00) {
                                entrys.add(CandleEntry((index).toFloat(), item.nowPrice.toFloat(), item.nowPrice.toFloat(), item.nowPrice.toFloat(), item.nowPrice.toFloat()))
                            } else {
                                entrys.add(CandleEntry((index).toFloat(), item.maxPrice.toFloat(), item.minPrice.toFloat(), item.beginPrice.toFloat(), item.nowPrice.toFloat()))
                            }

                            var line5 = item.line_5
                            if(line5 == 0.00) { line5 = item.beginPrice }

                            var line10 = item.line_10
                            if(line10 == 0.00) { line10 = item.beginPrice }

                            var line20 = item.line_20
                            if(line20 == 0.00) { line20 = item.beginPrice }

                            values_5.add(Entry(index.toFloat(), line5.toFloat()))
                            values_10.add(Entry(index.toFloat(), line10.toFloat()))
                            values_20.add(Entry(index.toFloat(), line20.toFloat()))

                            val p = item.totalPrice.toFloat() / 100000000
                            barEntrys.add(BarEntry(index.toFloat(), p))
                            val green = "#28FF28"
                            val red = "#FF0000"

                            if(item.maxPrice > maxPrice) {
                                maxPrice = item.maxPrice
                                if(index == records.size - 2) {
                                    lastTwo = true
                                }
                                if(index == records.size - 1) {
                                    lastOne = true
                                }
                                black = true
                            } else {
                                black = false
                            }

                            if(item.nowPrice > maxNowPrice) {
                                maxNowPrice = item.nowPrice

                                if(index == records.size - 1) {
                                    lastOneMaxNowPrice = true
                                }
                            }


                            if(index == blackIndex) {
                                colorEntrys.add(Color.BLACK)
                                maxPrice = item.maxPrice
                                maxNowPrice = item.nowPrice
                                xinGaoList.add(item.time!!)
                                xinGaoIndexList.add(records.size - index)
                            } else if(black){
                                colorEntrys.add(Color.GRAY)
                                xinGaoList.add(item.time!!)
                                xinGaoIndexList.add(records.size - index)
                            } else {
                                if(item.beginPrice > item.nowPrice) {
                                    colorEntrys.add(Color.parseColor(green))
                                } else if(item.beginPrice < item.nowPrice) {
                                    colorEntrys.add(Color.parseColor(red))
                                } else {
                                    if(item.range >= 0) {
                                        colorEntrys.add(Color.parseColor(red))
                                    } else {
                                        colorEntrys.add(Color.parseColor(green))
                                    }
                                }
                            }
                        }

                        it.candleEntryList = entrys
                        it.barEntryList = barEntrys
                        it.colorsList = colorEntrys
                        it.values_5 = values_5
                        it.values_10 = values_10
                        it.values_20 = values_20

                    }

                    val info = lines.get(lines.size - 1)
                    val info_pre = lines.get(lines.size - 2)

                    val share = SharesRecordActivity.ShareInfo(info)

                    it.lastShareInfo = share
                    val share_pre = SharesRecordActivity.ShareInfo(info_pre)

                    updateCurrentData(it, share, share_pre)

                }
            }
            ThreadPoolUtil.executeUI(Runnable {
                refreshTempData()
                refreshCount()
                view_share_bar.visibility = View.VISIBLE
                loadingStatus = 0
            })
        })
    }

    private fun refreshTempData() {
        val temp  = ArrayList<ReportShareInfo>()
        for (info in mData) {

//            if(heartChecked && !info.heart) {
//                continue
//            }
            if(xinGaoCount && info.maxCount <= 1) {
                continue
            }


//            if(isYangYinOne && info.yangYin < yang_yin_et_count.text.toString().toInt()) {
//                continue
//            }
            val selfSettingBean = settingBean
            if(selfSettingBean?.timeChecked == true && (info.dayCount < selfSettingBean.timeValue || info.dayCount > selfSettingBean.timeValue2)) {
                continue
            }

//            if(selfSettingBean?.redLine == true && !info.redLine) {
//                continue
//            }
//
//            if(selfSettingBean?.youXuan == true && !info.youXuan) {
//                continue
//            }

//            if(selfSettingBean?.maxRangChecked == true && info.yinXianLength < selfSettingBean.maxRangValue) {
//                continue
//            }
//
//            if(selfSettingBean?.zhiDie == true && !info.zhiDie) {
//                continue
//            }
//
//            if(selfSettingBean?.duan_ceng_Checked == true && !info.duanCeng) {
//                continue
//            }

            if(selfSettingBean?.xin_gao == true && (info.label2 == null)) {
                continue
            }

//            if(selfSettingBean?.you_one == true && !info.youOne) {
//                continue
//            }
//
//            if(selfSettingBean?.you_two == true && !info.youTwo) {
//                continue
//            }
//
//            if(selfSettingBean?.you_three == true && !info.youThree) {
//                continue
//            }
//
//            if(selfSettingBean?.maxLiang == true && !info.maxLiang) {
//                continue
//            }

            temp.add(info)
        }
        mTempData = temp
        mAdapter?.setData(mTempData)
    }

    private fun deleteItem(mineShareInfo: ReportShareInfo?) {
        (mData as ArrayList).remove(mineShareInfo)
        (mTempData as ArrayList).remove(mineShareInfo)
        mAdapter?.setData(mTempData)
        refreshCount()
    }

    private fun refreshCount() {
        (activity as SelfSelectionActivity).notifyData(1, FanBaoTab.TAG, mTempData.size)
    }

    private fun updateCurrentData(
        it: ReportShareInfo,
        share: SharesRecordActivity.ShareInfo,
        sharePre: SharesRecordActivity.ShareInfo,
        oneline: Boolean = false
    ) {
        if(it.dayCount > 0) {

            it.fanbao = false
            if(share.maxPrice > sharePre.maxPrice) {
                it.fanbao = true
            }
            val a = min(share.rangeBegin, share.range) - share.rangeMin
            val b = share.rangeMax - max(share.rangeBegin, share.range)
            it.todayRange = share.range
            var liangBi = "0"
            if(sharePre.totalPrice > 0) {
                liangBi = baoLiuXiaoShu(share.totalPrice / sharePre.totalPrice)
            }
            it.moreInfo = "${share.rangeBegin},  ${share.rangeMin},  ${share.rangeMax},  ${String.format("%.2f",share.totalPrice / 100000000)}亿,  ${liangBi}"
        }
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

        private var mSharesInfoList: List<ReportShareInfo> = ArrayList()

        private var showFenShi = false


        fun setData(sharesInfoList: List<ReportShareInfo>) {
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

        fun changeShiTu() {
            showFenShi = !showFenShi
            mSharesInfoList.forEach {

            }
            notifyDataSetChanged()
        }

        fun changeCurrentCb(currentCb: Int) {
            mSharesInfoList.forEach {
                it.currentCb = currentCb
            }
            notifyDataSetChanged()
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

        private var mineShareInfo: ReportShareInfo? = null
        private var chart: CandleStickChart? = null
        private var combinedChart: CombinedChart? = null
        private var bar: BarChart? = null
        private var divideView: View? = null

        private var heart: Boolean = false

        private var mTypeXinGaoTv: TextView? = null
        private var mTypeMaxRangeTv: TextView? = null
        private var mTypeYinYangTv: TextView? = null
        private var mTypeFangLiangTv: TextView? = null
        private var mTypeYinXianTv: TextView? = null
        private var mTypeZhiDieTv: TextView? = null

        private var mAvgP: TextView? = null

        private var mFenShiIv: ImageView? = null


        init {
            mIdTv = view.findViewById(R.id.self_item_id)
            mTimeTv = view.findViewById(R.id.self_item_time)
            mNameTv = view.findViewById(R.id.self_item_name)
            mCodeTv = view.findViewById(R.id.self_item_code)
            mRangeTv = view.findViewById(R.id.self_item_range)
            mTodayTv = view.findViewById(R.id.self_item_today)
            mTypeXinGaoTv = view.findViewById(R.id.tv_type_xin_gao)
            mTypeMaxRangeTv = view.findViewById(R.id.tv_type_max_rang)
            mTypeYinYangTv = view.findViewById(R.id.tv_type_yin_yang)
            mTypeFangLiangTv = view.findViewById(R.id.tv_type_fang_liang)
            mTypeYinXianTv = view.findViewById(R.id.tv_type_yin_xian)
            mTypeZhiDieTv = view.findViewById(R.id.tv_type_zhi_die)
            mAvgP = view.findViewById(R.id.tv_type_avg_p)

            mFenShiIv = view.findViewById(R.id.iv_fen_shi)

            divideView = view.findViewById(R.id.divide)

            moreInfoTv = view.findViewById(R.id.tv_more_info)
            heartIv = view.findViewById(R.id.iv_item_heart)
            chart = view.findViewById(R.id.item_candler_chart)
            combinedChart = view.findViewById(R.id.combined_chart)
            bar = view.findViewById(R.id.item_bar_chart)
            //initCandleChart()
            initCombinedChart()
            intBarChart()

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
                                ReportShareDatabase.getInstance()?.getReportShareDao()?.deleteTest(it, "7")
                                deleteItem(mineShareInfo)
                            }
                        }
                    }
                }.show()
                true
            }

//            mIdTv?.setOnClickListener {
//                mineShareInfo!!.showFenShi = !mineShareInfo!!.showFenShi
//                if(mineShareInfo!!.showFenShi) {
//                    mFenShiIv?.visibility = View.VISIBLE
//                    combinedChart?.visibility = View.GONE
//                    bar?.visibility = View.GONE
//                    divideView?.visibility = View.GONE
//                    Glide.with(context).load("https://image.sinajs.cn/newchart/min/n/${mineShareInfo!!.code}.gif")
//                        .asGif()
//                        .skipMemoryCache(true)
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .into(mFenShiIv)
//                } else {
//                    mFenShiIv?.visibility = View.GONE
//                    combinedChart?.visibility = View.VISIBLE
//                    bar?.visibility = View.VISIBLE
//                    divideView?.visibility = View.VISIBLE
//                }
//            }
            mCodeTv?.setOnClickListener {
                ClipboardUtil.clip(context, mineShareInfo?.code)
            }

            mTimeTv?.setOnClickListener {
                //http://image.sinajs.cn/newchart/min/n/sh000001.gif
                FenShiDialog(context, mineShareInfo?.code!!).show()
            }

            heartIv?.setOnClickListener {
                heart = !heart
//                mineShareInfo?.heart = heart
//                mineShareInfo?.also {
//                    MineShareDatabase.getInstance()?.getMineShareDao()?.update(it)
//                }
                if(heart) {
                    heartIv?.setImageResource(R.mipmap.heart_selected)
                } else {
                    heartIv?.setImageResource(R.mipmap.heart)
                }
            }
        }

        var xAxis: XAxis? = null
        private fun initCombinedChart() {
            combinedChart?.also {
                it.setTouchEnabled(false)
                it.description.isEnabled = false
                it.setDrawGridBackground(false) // 是否显示表格颜色
                //it.setBackgroundColor(Color.WHITE) // 设置背景
                it.setPinchZoom(false) // if disabled, scaling can be done on x- and y-axis separately
                it.isLogEnabled = true
                it.isSelected = false
                xAxis = it.xAxis
                xAxis?.isEnabled = false

                val leftAxis: YAxis = it.axisLeft
                leftAxis.isEnabled = false

                val rightAxis: YAxis = it.axisRight
                rightAxis.isEnabled = false

                val l: Legend = it.legend // 设置比例图标示
                l.isEnabled = false //决定显不显示标签
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

        fun bindData(position: Int, info: ReportShareInfo) {
            //LogUtil.i(TAG, "bindData: ${info.code}, ${info.candleEntryList?.size}")
            this.mineShareInfo = info
            mIdTv?.text = position.toString()
            mTimeTv?.text = info.time?.substring(5) + "(${info.dayCount})"
            mNameTv?.text = info.name
            mCodeTv?.text = info.code?.substring(2)



            if(info.currentCb == 1) {

            }
            if(info.currentCb == 2) {
                mRangeTv?.text = "${info.maxCount}"
                if(info.maxCount >= 10) {
                    mRangeTv?.setTextColor(Color.parseColor("#f15b6c"))
                } else {
                    mRangeTv?.setTextColor(Color.parseColor("#7fb80e"))
                }
            }

            if(info.currentCb == 3) {
//                mRangeTv?.text = baoLiuXiaoShu(info.liangBi)
//                if(info.liangBi >= 0) {
//                    mRangeTv?.setTextColor(Color.parseColor("#f15b6c"))
//                } else {
//                    mRangeTv?.setTextColor(Color.parseColor("#7fb80e"))
//                }
            }

            if(info.currentCb == 4) {
//                mRangeTv?.text = "${info.yangYin}"
//                if(info.yangYin >= 0) {
//                    mRangeTv?.setTextColor(Color.parseColor("#f15b6c"))
//                } else {
//                    mRangeTv?.setTextColor(Color.parseColor("#7fb80e"))
//                }
            }

            if(info.currentCb == 5) {
//                mRangeTv?.text = baoLiuXiaoShu(info.cha_20 * 100)
//                if(info.cha_20 >= 0) {
//                    mRangeTv?.setTextColor(Color.parseColor("#f15b6c"))
//                } else {
//                    mRangeTv?.setTextColor(Color.parseColor("#7fb80e"))
//                }
            }



            mTodayTv?.text = info.todayRange.toString()

            if(info.todayRange >= 0) {
                mTodayTv?.setTextColor(Color.parseColor("#ed1941"))
            } else {
                mTodayTv?.setTextColor(Color.parseColor("#7fb80e"))
            }
            moreInfoTv?.text = info.moreInfo

            refreshChart(0)

            //heart = info.heart
            if(heart) {
                heartIv?.setImageResource(R.mipmap.heart_selected)
            } else {
                heartIv?.setImageResource(R.mipmap.heart)
            }

//            if(info.zhiDie) {
//                mTypeZhiDieTv?.visibility = View.VISIBLE
//            } else {
//                mTypeZhiDieTv?.visibility = View.GONE
//            }

            if(info.label2 == "新高") {
                mTypeXinGaoTv?.visibility = View.VISIBLE
                mTypeXinGaoTv?.text = "新高 ${info.maxCount}"
            } else {
                mTypeXinGaoTv?.visibility = View.GONE
            }

            mTypeMaxRangeTv?.visibility = View.GONE
            mTypeYinYangTv?.visibility = View.GONE
            mTypeFangLiangTv?.visibility = View.GONE
            mTypeYinXianTv?.visibility = View.GONE

            if(info.fanbao) {
                mAvgP?.visibility = View.VISIBLE
                mAvgP?.text = "反包"
            } else {
                mAvgP?.visibility = View.GONE
            }

           //mAvgP?.text = "${info.range}"

            mFenShiIv?.visibility = View.GONE
        }

        private fun refreshChart(index: Int) {
            val info = mineShareInfo!!
            info.candleEntryList?.also {
                //                val data = CandleData(getCanleDataSet(it))
//                val params = chart?.layoutParams
//                params?.width = getWidth(it.size)
//                chart?.layoutParams = params
//                chart?.data = data
//                chart?.invalidate()


                val data = CombinedData()

                val dataSets = ArrayList<ILineDataSet>()
                dataSets.add(getLineDataSet(info.values_5!!.subList(index, info.values_5!!.size), Color.BLUE, "line 5"))
                dataSets.add(getLineDataSet(info.values_10!!.subList(index, info.values_10!!.size), Color.RED, "line 10"))
                dataSets.add(getLineDataSet(info.values_20!!.subList(index, info.values_20!!.size), Color.GREEN, "line 20"))
                data.setData(LineData(dataSets))

                data.setData(CandleData(getCanleDataSet(it)))

                xAxis?.axisMaximum = data.xMax + 1f
                xAxis?.axisMinimum = data.xMin - 1f

                val params = combinedChart?.layoutParams
                params?.width = getWidth(it.size)
                params?.height = getHeight(it.size)
                combinedChart?.layoutParams = params

                combinedChart?.data = data
                combinedChart?.invalidate()

            }

            info.barEntryList?.also {
                val barData = BarData(getBarDataSet(info.barEntryList!!.subList(index, info.barEntryList!!.size), info.colorsList!!.subList(index, info.colorsList!!.size)))
                barData.setDrawValues(false)

                val barParams = bar?.layoutParams
                barParams?.width = getWidth(it.size)
                bar?.layoutParams = barParams
                bar?.data = barData
                bar?.invalidate()
            }
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

        private fun getHeight(size: Int): Int {
            if(size < 10) return 240
            if(size > 100) return 600
            return 240 + (size - 10) * 4
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

        private fun intBarChart() {
            bar?.also {
                it.description.isEnabled = false
                it.setTouchEnabled(false)
                //it.setBackgroundColor(Color.WHITE)
                it.setDrawGridBackground(false)
                it.setDrawBarShadow(false)
                it.isHighlightFullBarEnabled = false

                it.legend.isEnabled = false

                var xAxis = it.xAxis
                xAxis.isEnabled = false

                var yAxis = it.axisLeft
                yAxis.isEnabled = false
                //yAxis.labelCount = 2
                yAxis.axisMinimum = 0f
                it.axisRight.isEnabled = false

            }
        }

        private fun getBarDataSet(barData: List<BarEntry>?, colorData: List<Int>?): MutableList<IBarDataSet>? {
            val set = BarDataSet(barData, "liang neng")
            set.setDrawIcons(false)
            set.isHighlightEnabled = false
            set.colors = colorData
            val dataSets = ArrayList<IBarDataSet>()
            dataSets.add(set)

            return dataSets
        }

        private fun getLineDataSet(values: List<Entry>, color: Int, lable: String): ILineDataSet {
            // create a dataset and give it a type
            val set1 = LineDataSet(values, lable)
            set1.isHighlightEnabled = false
            set1.setDrawIcons(false)
            set1.setDrawCircles(false)
            set1.color = color
            set1.lineWidth = 0.5f
//        set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            set1.formSize = 10f
            //set1.valueTextSize = 9f
            set1.setDrawFilled(false)
            set1.setDrawValues(false)

            return set1
        }
    }

    override fun shiShiRefresh() {
        mRefreshCount ++
        (activity as SelfSelectionActivity).notifyData(2, FanBaoTab.TAG, mRefreshCount)
        LogUtil.i(TAG, "mRefreshCount: $mRefreshCount")
        if(mRefreshCount == mTempData.size) {
            mAdapter?.setData(mTempData)
            (activity as SelfSelectionActivity).notifyData(3, FanBaoTab.TAG, 0)
            resetSortUI()
        }
    }
}