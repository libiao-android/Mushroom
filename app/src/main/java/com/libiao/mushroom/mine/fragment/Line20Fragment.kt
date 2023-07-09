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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
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
import com.libiao.mushroom.mine.tab.Line20Tab
import com.libiao.mushroom.room.MineShareDatabase
import com.libiao.mushroom.room.MineShareInfo
import com.libiao.mushroom.room.report.ReportShareDatabase
import com.libiao.mushroom.room.report.ReportShareInfo
import com.libiao.mushroom.thread.ThreadPoolUtil
import com.libiao.mushroom.utils.*
import kotlinx.android.synthetic.main.line_20_fragment.*
import java.io.*
import java.nio.charset.Charset
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Line20Fragment: BaseFragment(R.layout.line_20_fragment), ICommand {

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
                            if(lines.size >= 20) {
                                val a = lines.size - 19
                                var total_5 = 0.00
                                var total_10 = 0.00
                                var total_20 = 0.00

                                for(i in a until lines.size) {
                                    val item = SharesRecordActivity.ShareInfo(lines[i])
                                    total_20 += item.nowPrice
                                    if(i >= lines.size - 9) {
                                        total_10 += item.nowPrice
                                    }
                                    if(i >= lines.size - 4) {
                                        total_5 += item.nowPrice
                                    }
                                }
                                total_20 += share.nowPrice
                                total_10 += share.nowPrice
                                total_5 += share.nowPrice
                                share.line_20 = String.format("%.2f", total_20 / 20).toDouble()
                                share.line_10 = String.format("%.2f", total_10 / 10).toDouble()
                                share.line_5 = String.format("%.2f", total_5 / 5).toDouble()
                                //LogUtil.i(TAG, "line_5: ${share.line_5}, line_10: ${share.line_10}, line_20: ${share.line_20}")
                            }
                            val index = it.candleEntryList?.size ?: 0
                            if(share.beginPrice == 0.00) {
                                it.candleEntryList?.add(CandleEntry((index).toFloat(), share.nowPrice.toFloat(), share.nowPrice.toFloat(), share.nowPrice.toFloat(), share.nowPrice.toFloat()))
                            } else {
                                it.candleEntryList?.add(CandleEntry((index).toFloat(), share.maxPrice.toFloat(), share.minPrice.toFloat(), share.beginPrice.toFloat(), share.nowPrice.toFloat()))
                            }

                            val p = share.totalPrice.toFloat() / 100000000
                            it.barEntryList?.add(BarEntry(index.toFloat(), p))

                            it.colorsList?.add(Color.GRAY)

                            it.values_5.add(Entry(index.toFloat(), share.line_5.toFloat()))
                            it.values_10.add(Entry(index.toFloat(), share.line_10.toFloat()))
                            it.values_20.add(Entry(index.toFloat(), share.line_20.toFloat()))

                            updateCurrentData(it, share, share_pre)
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
        private const val TAG = "Line20Fragment"
    }

    private var mAdapter: MyPoolInfoAdater? = null
    private var mRangeStatus = 0
    private var mTodayStatus = 0
    private var mTimeStatus = 0

    private var isYinXianOne = false
    private var isYangYinOne = false
    private var isXianChaOne = false

    private var mData: List<MineShareInfo> = ArrayList()
    private var mTempData: List<MineShareInfo> = ArrayList()

    private var settingDialog: SelfSettingDialog? = null
    private var settingBean: SelfSettingBean? = SelfSettingBean()

    private var currentCb = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogUtil.i(TAG, "onViewCreated")
        initView()
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
        cb_zhang_fu.isChecked = true
        cb_zhang_fu.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                currentCb = 1
                tv_ke_xuan.text = "涨幅"
                mAdapter?.changeCurrentCb(currentCb)
                yin_xian_child_view.visibility = View.GONE
                yang_yin_child_view.visibility = View.GONE
                xian_cha_child_view.visibility = View.GONE
            }

        }
        cb_jing_jia.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                currentCb = 2
                tv_ke_xuan.text = "竞价"
                mAdapter?.changeCurrentCb(currentCb)
                yin_xian_child_view.visibility = View.VISIBLE
                yang_yin_child_view.visibility = View.GONE
                xian_cha_child_view.visibility = View.GONE
            }
        }
        cb_yin_xian_one.setOnCheckedChangeListener { buttonView, isChecked ->
            isYinXianOne = isChecked
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
                xian_cha_child_view.visibility = View.GONE
            }
        }
        cb_yang_yin.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                currentCb = 4
                tv_ke_xuan.text = "阳阴"
                mAdapter?.changeCurrentCb(currentCb)
                yin_xian_child_view.visibility = View.GONE
                yang_yin_child_view.visibility = View.VISIBLE
                xian_cha_child_view.visibility = View.GONE
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
                xian_cha_child_view.visibility = View.VISIBLE
            }
        }
        cb_xian_cha_one.setOnCheckedChangeListener { buttonView, isChecked ->
            isXianChaOne = isChecked
            refreshTempData()
            resetSortUI()
            refreshCount()
        }

        tv_id?.setOnClickListener {
            mAdapter?.changeShiTu()
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
                        1 -> mTempData = ArrayList(mTempData.sortedByDescending { it.totalRange })
                        2 -> mTempData = ArrayList(mTempData.sortedByDescending { it.shareInfo!!.rangeBegin })
                        3 -> mTempData = ArrayList(mTempData.sortedByDescending { it.liangBi })
                        4 -> mTempData = ArrayList(mTempData.sortedByDescending { it.yangYin })
                        5 -> mTempData = ArrayList(mTempData.sortedByDescending { it.cha_20 })
                    }
                    mAdapter?.setData(mTempData)
                }
                1 -> {
                    iv_self_range.setImageResource(R.mipmap.sort_ascending)
                    mRangeStatus = 2
                    //低-高
                    when(currentCb) {
                        1 -> mTempData = ArrayList(mTempData.sortedBy { it.totalRange })
                        2 -> mTempData = ArrayList(mTempData.sortedBy { it.shareInfo!!.rangeBegin })
                        3 -> mTempData = ArrayList(mTempData.sortedBy { it.liangBi })
                        4 -> mTempData = ArrayList(mTempData.sortedBy { it.yangYin })
                        5 -> mTempData = ArrayList(mTempData.sortedBy { it.cha_20 })
                    }
                    mAdapter?.setData(mTempData)
                }
                2 -> {
                    iv_self_range.setImageResource(R.mipmap.sort_descending)
                    mRangeStatus = 1
                    //高-低
                    when(currentCb) {
                        1 -> mTempData = ArrayList(mTempData.sortedByDescending { it.totalRange })
                        2 -> mTempData = ArrayList(mTempData.sortedByDescending { it.shareInfo!!.rangeBegin })
                        3 -> mTempData = ArrayList(mTempData.sortedByDescending { it.liangBi })
                        4 -> mTempData = ArrayList(mTempData.sortedByDescending { it.yangYin })
                        5 -> mTempData = ArrayList(mTempData.sortedByDescending { it.cha_20 })
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

    private fun isContain2(tempData: List<MineShareInfo>, code: String?): Boolean {
        tempData.forEach {
            if(it.code == code) return true
        }
        return false
    }

    private fun isContain(dataOld: MutableList<ReportShareInfo>?, code: String?): Boolean {
        dataOld?.forEach {
            if(it.code == code && it.delete == 0) return true
        }
        return false
    }

    private fun refreshLocalData(data: List<MineShareInfo>, reload: Boolean = false) {
        //(activity as SelfSelectionActivity).showLoading()
        loadingStatus = 1
        ThreadPoolUtil.execute(Runnable {
            data.forEach {
                val f = File(file_2023, it.code)
                if(f.exists()) {
                    val stream = FileInputStream(f)
                    val reader = BufferedReader(InputStreamReader(stream, Charset.defaultCharset()))
                    val lines = reader.readLines()
                    val one = SharesRecordActivity.ShareInfo(lines[lines.size - it.dayCount - 2])
                    val two = SharesRecordActivity.ShareInfo(lines[lines.size - it.dayCount - 1])
                    var allPrice = 0.00
                    var allDay = 0
                    var itemIn = 0
                    var maxLiang = 0.00
                    var maxLiangIndex = -9
                    val maxList = arrayListOf<Int>()
                    var threePre = false
                    if(it.candleEntryList == null || reload) {
                        var begin = lines.size - it.dayCount - 2
                        if(begin < 0) begin = 0
                        val records = lines.subList(begin, lines.size)
                        val entrys = java.util.ArrayList<CandleEntry>()
                        val barEntrys = java.util.ArrayList<BarEntry>()
                        val colorEntrys = java.util.ArrayList<Int>()
                        var maxPrice = one.nowPrice
                        var totalP = 0.00

                        var tempItem: SharesRecordActivity.ShareInfo? = null
                        var greenLittle = true
                        it.values_5.clear()
                        it.values_10.clear()
                        it.values_20.clear()
                        var yang = 0
                        var yin = 0
                        var tempMaxP = 0.00
                        var xinGao = false
                        var xinGaoCount = 0
                        var noXinGao = false
                        it.zhiDie = false
                        var fangLiang = false
                        var shouHong = false
                        it.fangLiangList.clear()

                        it.maxLiang = false
                        val list = ArrayList<SharesRecordActivity.ShareInfo>()
                        //it.youOne = true
                        it.youOne = false
                        it.youThree = true
                        var maxIndex = 0

                        var youOneIndex = 0

                        var lastMaxIndex = -1

                        records.forEachIndexed { index, s ->
                            val item = SharesRecordActivity.ShareInfo(s)

                            if(index > 0) {
                                list.add(item)
                                item.index = index
                            }

                            if(index > 3 && item.minPrice < item.line_20 && !xinGao) {
                                noXinGao = true
                            }

                            if(index > 3 && index == records.size - 1) {
                                if(item.totalPrice > maxLiang && item.range > 0) {
                                    it.maxLiang = true
                                }
                            }


                            if(item.totalPrice > maxLiang) {
                                maxLiang = item.totalPrice
                            }


                            if(!noXinGao && item.maxPrice > tempMaxP * 1.01) {
                                tempMaxP = item.maxPrice
                                if(index > 3) {
                                    xinGao = true
                                    xinGaoCount ++
                                }
                            }

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

//                            if(shouHong) {
//                                if(item.nowPrice > item.beginPrice) {
//                                    it.zhiDie = true
//                                    it.fangLiangList.add(index)
//                                }
//                            }
//                            shouHong = false

                            if(fangLiang) {
                                if(item.totalPrice > tempItem!!.totalPrice && item.range > 0) {
                                    //shouHong = true
                                    it.zhiDie = true
                                    it.fangLiangList.add(index)
                                }
                            }

                            fangLiang = false
                            if(index > 4 && tempItem != null) {
                                if(item.totalPrice > tempItem!!.totalPrice * 2 && item.range > 0) {
                                    fangLiang = true
                                }
                            }

//                            if(tempItem != null && it.youOne) {
//                                if(item.beginPrice > item.nowPrice && item.totalPrice > tempItem!!.totalPrice) {
//                                    it.youOne = false
//                                }
//                            }

                            if(tempItem != null && tempItem!!.beginPrice > tempItem!!.nowPrice && item.beginPrice > item.nowPrice) {
                                it.youThree = false
                            }


                            if(tempItem == null) {

                            } else {
                                if(greenLittle) {
                                    if(item.beginPrice > item.nowPrice && tempItem!!.nowPrice >= tempItem!!.beginPrice && item.totalPrice > tempItem!!.totalPrice * 1.1) {
                                        greenLittle = false
                                    }
                                }

                            }

                            if(item.totalPrice > totalP) {
                                totalP = item.totalPrice
                                itemIn = index
                            }
                            if(item.nowPrice > maxPrice) {
                                maxPrice = item.nowPrice
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

                            it.values_5.add(Entry(index.toFloat(), line5.toFloat()))
                            it.values_10.add(Entry(index.toFloat(), line10.toFloat()))
                            it.values_20.add(Entry(index.toFloat(), line20.toFloat()))

                            val p = item.totalPrice.toFloat() / 100000000
                            barEntrys.add(BarEntry(index.toFloat(), p))
                            val green = "#28FF28"
                            val red = "#FF0000"

                            if(item.totalPrice == maxLiang && item.nowPrice > item.beginPrice && index > 2) {

                                if(index - lastMaxIndex == 1) {
                                    //it.youOne = true
                                }
                                lastMaxIndex = index

                                if(tempItem!!.range < 3 && item.totalPrice > 150000000) {
                                    it.youOne = true
                                    youOneIndex = index
                                    colorEntrys.add(Color.BLACK)
                                } else {
                                    colorEntrys.add(Color.GRAY)
                                }

                                maxList.add(index)
                                if(index == records.size - 3) {
                                    threePre = true
                                }
                                maxIndex++
//                                if(maxIndex >= 2) {
//                                    it.youOne = true
//                                }
                                maxLiangIndex = index
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

//                            if(index == records.size - yang_yin_et_count.text.toString().toInt()) {
//                                it.youTwo = false
//                                if(item.line_20 > 0.00) {
//                                    if(item.beginPrice < item.line_20 && item.nowPrice > item.line_20) {
//                                        it.youTwo = true
//                                    }
//                                }
//                            }
                            tempItem = item
                        }
                        it.yangYin = yang.toDouble() / (yang + yin)
                        val tMaxItem = SharesRecordActivity.ShareInfo(records[itemIn])
//                        it.sanLianYin = false
//                        if(tMaxItem.nowPrice > tMaxItem.beginPrice && greenLittle) {
//                            it.sanLianYin = true
//                        }
//                        if(greenLittle) {
//                            it.youXuan = true
//                        }

                        list.sortByDescending { it.totalPrice }
                       // LogUtil.i(TAG, "最大：${list[0].totalPrice}")
                       // LogUtil.i(TAG, "最小：${list.last().totalPrice}")


                        var minP = one.nowPrice
                        if(minP == 0.00) minP = two.yesterdayPrice
                        if(minP == 0.00) minP = two.beginPrice
                        it.maxRange = (maxPrice - minP) / minP * 100
                        it.candleEntryList = entrys
                        it.barEntryList = barEntrys
                        it.colorsList = colorEntrys
                        if(allDay > 0) {
                            it.avgP = allPrice / allDay
                        }
                        if(xinGao) {
                            it.label2 = "新高"
                            it.maxCount = xinGaoCount
                        }
                        it.youTwo = false
                        if(youOneIndex == (records.size - 1) && it.youOne) {
                            it.youTwo = true
                        }

                    }
                    it.price = one.nowPrice
                    val info = lines.get(lines.size - 1)
                    val info_pre = lines.get(lines.size - 2)

                    val share = SharesRecordActivity.ShareInfo(info)
                    if(share.minPrice > 0) {
                        it.cha_20 =  (share.nowPrice - share.line_20) / share.line_20
                    }
                    it.shareInfo = share
                    val share_pre = SharesRecordActivity.ShareInfo(info_pre)
                    //it.youTwo = false

//                    if(share.totalPrice > share_pre.totalPrice
//                        && share.nowPrice > share.beginPrice
//                        && share_pre.nowPrice > share_pre.beginPrice
//                        && share.range > share_pre.range
//                    ) {
//                        it.youThree = true
//                    }

                    //it.youTwo = false

                    if(it.dayCount >= 4) {

                        val ten1 = share
                        val ten2 = share_pre

                        val info_pre2 = lines.get(lines.size - 3)
                        val info_pre3 = lines.get(lines.size - 4)
                        val info_pre4 = lines.get(lines.size - 5)
                        val ten3 = SharesRecordActivity.ShareInfo(info_pre2)
                        val ten4 = SharesRecordActivity.ShareInfo(info_pre3)
                        val ten5 = SharesRecordActivity.ShareInfo(info_pre4)

                        var tM = Math.max(ten4.totalPrice, ten5.totalPrice)
                        tM = Math.max(tM, ten3.totalPrice)
                        tM = Math.max(tM, ten2.totalPrice)
                        tM = Math.max(tM, ten1.totalPrice)



//                        if (threePre) {
//                            if(ten2.totalPrice < ten3.totalPrice && ten1.totalPrice < ten2.totalPrice) {
//                                it.youTwo = true
//                            }
//                        }

//                        if(ten1.range < 0 && ten2.range < 0 && ten1.totalPrice < ten2.totalPrice && ten1.maxPrice < ten2.maxPrice && tM > maxLiang * 0.75
//                        ) {
//                            it.youOne = true
//                        }

//                        if(ten3.range < 0 && ten2.range < 0 && ten1.nowPrice > ten1.beginPrice) {
//                            it.youTwo = true
//                        }
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
                loadingStatus = 0
            })
        })
    }

    private fun refreshTempData() {
        val temp  = ArrayList<MineShareInfo>()
        for (info in mData) {

            if(heartChecked && !info.heart) {
                continue
            }
            if(isYinXianOne && info.todayMaxRange > 3) {
                continue
            }
            if(isYangYinOne && info.yangYin < yang_yin_et_count.text.toString().toInt()) {
                continue
            }
            if(isXianChaOne && (info.cha_20 > 0.02 || info.cha_20 <= 0.00)) {
                continue
            }
            val selfSettingBean = settingBean
            if(selfSettingBean?.timeChecked == true && (info.dayCount < selfSettingBean.timeValue || info.dayCount > selfSettingBean.timeValue2)) {
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

            if(selfSettingBean?.youXuan == true && !info.youXuan) {
                continue
            }

            if(selfSettingBean?.maxRangChecked == true && info.yinXianLength < selfSettingBean.maxRangValue) {
                continue
            }

            if(selfSettingBean?.zhiDie == true && !info.zhiDie) {
                continue
            }

            if(selfSettingBean?.duan_ceng_Checked == true && !info.duanCeng) {
                continue
            }

            if(selfSettingBean?.xin_gao == true && (info.label2 == null)) {
                continue
            }

            if(selfSettingBean?.you_one == true && !info.youOne) {
                continue
            }

            if(selfSettingBean?.you_two == true && !info.youTwo) {
                continue
            }

            if(selfSettingBean?.you_three == true && !info.youThree) {
                continue
            }

            if(selfSettingBean?.maxLiang == true && !info.maxLiang) {
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
        (activity as SelfSelectionActivity).notifyData(1, Line20Tab.TAG, mTempData.size)
    }

    private fun updateCurrentData(
        it: MineShareInfo,
        share: SharesRecordActivity.ShareInfo,
        sharePre: SharesRecordActivity.ShareInfo
    ) {
        if(it.price > 0) {
            val a = min(share.rangeBegin, share.range) - share.rangeMin
            val b = share.rangeMax - max(share.rangeBegin, share.range)

            if(share.range < 5 && share.range > -5) {
                it.yinXianLength = a
            } else {
                it.yinXianLength = 0.00
            }

            //it.youTwo = false
//            if(share.line_20 > 0.00) {
//                if(share.beginPrice < share.line_20 && share.nowPrice > share.line_20) {
//                    it.youTwo = true
//                }
//            }

            it.totalRange = (share.nowPrice - it.price) / it.price * 100
            it.todayRange = share.range
            it.todayMaxRange = share.rangeMax
            it.fangLiang = share.totalPrice > sharePre.totalPrice
            it.todayLiang = String.format("%.2f",share.totalPrice / 100000000).toDouble()
            if(sharePre.totalPrice > 0) {
                it.liangBi = share.totalPrice / sharePre.totalPrice
            }

            it.redLine = sharePre.nowPrice < sharePre.beginPrice && share.nowPrice < share.beginPrice && sharePre.range < 0 && share.range < 0

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

        private var mSharesInfoList: List<MineShareInfo> = ArrayList()

        private var showFenShi = false


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

        fun changeShiTu() {
            showFenShi = !showFenShi
            mSharesInfoList.forEach {
                it.showFenShi = showFenShi
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

        private var mineShareInfo: MineShareInfo? = null
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
                                MineShareDatabase.getInstance()?.getMineShareDao()?.delete(it)
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
                mineShareInfo?.heart = heart
                mineShareInfo?.also {
                    MineShareDatabase.getInstance()?.getMineShareDao()?.update(it)
                }
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

        fun bindData(position: Int, info: MineShareInfo) {
            //LogUtil.i(TAG, "bindData: ${info.code}, ${info.candleEntryList?.size}")
            this.mineShareInfo = info
            mIdTv?.text = position.toString()
            mTimeTv?.text = info.time?.substring(5) + "(${info.dayCount})"
            mNameTv?.text = info.name
            mCodeTv?.text = info.code?.substring(2)



            if(info.currentCb == 1) {
                mRangeTv?.text = baoLiuXiaoShu(info.totalRange)
                if(info.totalRange >= 0) {
                    mRangeTv?.setTextColor(Color.parseColor("#f15b6c"))
                } else {
                    mRangeTv?.setTextColor(Color.parseColor("#7fb80e"))
                }
            }
            if(info.currentCb == 2) {
                mRangeTv?.text = baoLiuXiaoShu(info.shareInfo!!.rangeBegin)
                if(info.shareInfo!!.rangeBegin >= 0) {
                    mRangeTv?.setTextColor(Color.parseColor("#f15b6c"))
                } else {
                    mRangeTv?.setTextColor(Color.parseColor("#7fb80e"))
                }
            }

            if(info.currentCb == 3) {
                mRangeTv?.text = baoLiuXiaoShu(info.liangBi)
                if(info.liangBi >= 0) {
                    mRangeTv?.setTextColor(Color.parseColor("#f15b6c"))
                } else {
                    mRangeTv?.setTextColor(Color.parseColor("#7fb80e"))
                }
            }

            if(info.currentCb == 4) {
                mRangeTv?.text = baoLiuXiaoShu(info.yangYin)
                if(info.yangYin >= 0) {
                    mRangeTv?.setTextColor(Color.parseColor("#f15b6c"))
                } else {
                    mRangeTv?.setTextColor(Color.parseColor("#7fb80e"))
                }
            }

            if(info.currentCb == 5) {
                mRangeTv?.text = baoLiuXiaoShu(info.cha_20 * 100)
                if(info.cha_20 >= 0) {
                    mRangeTv?.setTextColor(Color.parseColor("#f15b6c"))
                } else {
                    mRangeTv?.setTextColor(Color.parseColor("#7fb80e"))
                }
            }



            mTodayTv?.text = info.todayRange.toString()

            if(info.todayRange >= 0) {
                mTodayTv?.setTextColor(Color.parseColor("#ed1941"))
            } else {
                mTodayTv?.setTextColor(Color.parseColor("#7fb80e"))
            }
            moreInfoTv?.text = info.moreInfo

            info.candleEntryList?.also {
//                val data = CandleData(getCanleDataSet(it))
//                val params = chart?.layoutParams
//                params?.width = getWidth(it.size)
//                chart?.layoutParams = params
//                chart?.data = data
//                chart?.invalidate()


                val data = CombinedData()

                val dataSets = ArrayList<ILineDataSet>()
                dataSets.add(getLineDataSet(info.values_5, Color.BLUE, "line 5"))
                dataSets.add(getLineDataSet(info.values_10, Color.RED, "line 10"))
                dataSets.add(getLineDataSet(info.values_20, Color.GREEN, "line 20"))
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
                val barData = BarData(getBarDataSet(info.barEntryList, info.colorsList))
                barData.setDrawValues(false)

                val barParams = bar?.layoutParams
                barParams?.width = getWidth(it.size)
                bar?.layoutParams = barParams
                bar?.data = barData
                bar?.invalidate()
            }

            heart = info.heart
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

            mTypeMaxRangeTv?.text = baoLiuXiaoShu(info.totalRange)
            mTypeYinYangTv?.text = baoLiuXiaoShu(info.yangYin)
            mTypeFangLiangTv?.text = baoLiuXiaoShu(info.liangBi)
            mTypeYinXianTv?.text = baoLiuXiaoShu(info.yinXianLength)

            mAvgP?.text = "${String.format("%.2f",info.avgP / 100000000)}亿"

            if(info.showFenShi) {
                mFenShiIv?.visibility = View.VISIBLE
                Glide.with(context).load("https://image.sinajs.cn/newchart/min/n/${info.code}.gif")
                    .asGif()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(mFenShiIv)
            } else {
                mFenShiIv?.visibility = View.GONE
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

        private fun getLineDataSet(values: ArrayList<Entry>, color: Int, lable: String): ILineDataSet {
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
        (activity as SelfSelectionActivity).notifyData(2, Line20Tab.TAG, mRefreshCount)
        LogUtil.i(TAG, "mRefreshCount: $mRefreshCount")
        if(mRefreshCount == mTempData.size) {
            mAdapter?.setData(mTempData)
            (activity as SelfSelectionActivity).notifyData(3, Line20Tab.TAG, 0)
            resetSortUI()
        }
    }
}