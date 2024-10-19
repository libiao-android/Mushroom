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
import com.libiao.mushroom.mine.fangliang.FangLiangSettingBean
import com.libiao.mushroom.mine.fangliang.FangLiangSettingDialog
import com.libiao.mushroom.mine.tab.XinGaoTab
import com.libiao.mushroom.room.FangLiangShareDatabase2
import com.libiao.mushroom.room.FangLiangShareInfo
import com.libiao.mushroom.thread.ThreadPoolUtil
import com.libiao.mushroom.utils.*
import kotlinx.android.synthetic.main.xin_gao_fragment.*
import java.io.*
import java.nio.charset.Charset
import kotlin.collections.ArrayList

class XinGaoFragment: BaseFragment(R.layout.xin_gao_fragment), ICommand {

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
        private const val TAG = "Line20Fragment"
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

    private var mData: List<FangLiangShareInfo> = ArrayList()
    private var mTempData: List<FangLiangShareInfo> = ArrayList()

    private var settingDialog: FangLiangSettingDialog? = null
    private var settingBean: FangLiangSettingBean? = FangLiangSettingBean()

    private var currentCb = 1

    private var dayCount = 17

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogUtil.i(TAG, "onViewCreated")
        settingBean?.timeValue = 4
        initView()
    }

    private fun initData() {
        val data = FangLiangShareDatabase2.getInstance()?.getFangLiangShareDao()?.getFangLiangShares()
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
        cb_xin_gao_liang_bi.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                currentCb = 2
                tv_ke_xuan.text = "最大量"
                mAdapter?.changeCurrentCb(currentCb)
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
        cb_today_liang.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                currentCb = 3
                tv_ke_xuan.text = "当天量"
                mAdapter?.changeCurrentCb(currentCb)
            }
        }
        cb_yang_yin.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                currentCb = 4
                tv_ke_xuan.text = "阳阴"
                mAdapter?.changeCurrentCb(currentCb)
            }
        }
        cb_yang_yin_one.setOnCheckedChangeListener { buttonView, isChecked ->
            isYangYinOne = isChecked
            refreshTempData()
            resetSortUI()
            refreshCount()
        }
        cb_max_price_count.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                currentCb = 5
                tv_ke_xuan.text = "次数"
                mAdapter?.changeCurrentCb(currentCb)
            }
        }

        cb_liang_bi.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                currentCb = 6
                tv_ke_xuan.text = "量比"
                mAdapter?.changeCurrentCb(currentCb)
            }
        }
        cb_shou_hong_day.setOnCheckedChangeListener { buttonView, isChecked ->
            refreshTempData()
            resetSortUI()
            refreshCount()
        }

        cb_first_xin_gao.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                firstXinGaoChecked = isChecked
                refreshTempData()
                resetSortUI()
                refreshCount()
            }
        }

        cb_you_xuan.setOnCheckedChangeListener { buttonView, isChecked ->
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
                        2 -> mTempData = ArrayList(mTempData.sortedByDescending { it.maxPrice})
                        3 -> mTempData = ArrayList(mTempData.sortedByDescending { it.todayLiang })
                        4 -> mTempData = ArrayList(mTempData.sortedByDescending { it.yinYang })
                        5 -> mTempData = ArrayList(mTempData.sortedByDescending { it.maxCount })
                        6 -> mTempData = ArrayList(mTempData.sortedByDescending { it.liangBi })
                    }
                    mAdapter?.setData(mTempData)
                }
                1 -> {
                    iv_self_range.setImageResource(R.mipmap.sort_ascending)
                    mRangeStatus = 2
                    //低-高
                    when(currentCb) {
                        1 -> mTempData = ArrayList(mTempData.sortedBy { it.totalRange })
                        2 -> mTempData = ArrayList(mTempData.sortedBy { it.maxPrice })
                        3 -> mTempData = ArrayList(mTempData.sortedBy { it.todayLiang })
                        4 -> mTempData = ArrayList(mTempData.sortedBy { it.yinYang })
                        5 -> mTempData = ArrayList(mTempData.sortedBy { it.maxCount })
                        6 -> mTempData = ArrayList(mTempData.sortedBy { it.liangBi })
                    }
                    mAdapter?.setData(mTempData)
                }
                2 -> {
                    iv_self_range.setImageResource(R.mipmap.sort_descending)
                    mRangeStatus = 1
                    //高-低
                    when(currentCb) {
                        1 -> mTempData = ArrayList(mTempData.sortedByDescending { it.totalRange })
                        2 -> mTempData = ArrayList(mTempData.sortedByDescending { it.maxPrice })
                        3 -> mTempData = ArrayList(mTempData.sortedByDescending { it.todayLiang })
                        4 -> mTempData = ArrayList(mTempData.sortedByDescending { it.yinYang })
                        5 -> mTempData = ArrayList(mTempData.sortedByDescending { it.maxCount })
                        6 -> mTempData = ArrayList(mTempData.sortedByDescending { it.liangBi })
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

        settingDialog = FangLiangSettingDialog(context!!) {
            LogUtil.i(TAG, "$it")
            settingBean = it
            refreshTempData()
            refreshCount()
            resetSortUI()
        }

        btn_start.setOnClickListener {
            qian_zhi_view.visibility = View.GONE
            initData()
        }
    }

    private fun refreshLocalData(data: List<FangLiangShareInfo>, reload: Boolean = false) {
        //(activity as SelfSelectionActivity).showLoading()
        loadingStatus = 1
        val preference = context?.getSharedPreferences("record_fang_liang", Context.MODE_PRIVATE)
        val edit = preference?.edit()
        var hasRecord = false
        var first = false
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

                    var totalP1 = 0.00
                    var countP1 = 0
                    var totalP2 = 0.00
                    var countP2 = 0
                    var avgP1 = 0.00
                    var avgP2 = 0.00

                    var add = true

                    lines.forEachIndexed{index, s ->
                        if(s.startsWith(it.time!!)) {
                            begin = index
                            return@forEachIndexed
                        }
                    }


                    if(it.candleEntryList == null || reload) {
                        var blackIndex = dayCount
                        var start = begin - dayCount
                        if(start < 0) {
                            start = 0
                            blackIndex = begin
                        }
                        var end = lines.size
                        if (cb_not_include_today.isChecked) end--
                        val records = lines.subList(start, end)
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

                        var maxLiang = 0.00
                        var maxPrice = 0.00
                        var firstMax = true
                        var firstIndex = -1
                        var maxIndex = -1
                        var maxPriceCount = 0

                        var noXinGaoDay = 0

                        records.forEachIndexed { index, s ->
                            val item = SharesRecordActivity.ShareInfo(s)

                            if(index > blackIndex) {
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

                            if(index < blackIndex - 2) {
                                totalP1 += item.totalPrice
                                countP1++
                                if (index == blackIndex - 3) {
                                    avgP1 = totalP1 / countP1
                                }
                            } else {
                                totalP2 += item.totalPrice
                                countP2++
                                if (!ShareParseUtil.zhangTing(item) && !ShareParseUtil.dieTing(item) && item.totalPrice < avgP1 * 3) {
                                    add = false
                                }
                            }

                            if(index <= blackIndex) {
                                if (item.totalPrice > maxLiang) {
                                    maxLiang = item.totalPrice
                                }
                                if (item.maxPrice > maxPrice) {
                                    maxPrice = item.maxPrice
                                }
                            }
                            if (index > blackIndex) {
                                if (item.totalPrice > maxLiang) {
                                    if (firstMax) {
                                        firstMax = false
                                        firstIndex = index
                                    }
                                    maxIndex = index
                                    maxPriceCount++
                                    maxLiang = item.totalPrice
                                    noXinGaoDay = 0
                                } else {
                                    noXinGaoDay++
                                }
                                if (item.maxPrice >= maxPrice) {
                                    noXinGaoDay = 0
                                    maxPrice = item.maxPrice
                                }
                            }
                            if (ShareParseUtil.zhangTing(item)) {
                                noXinGaoDay = 0
                            }

                            if(index == blackIndex) {
                                colorEntrys.add(Color.BLACK)
                            } else if (index == maxIndex) {
                                colorEntrys.add(Color.GRAY)
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

                        it.todayMax = firstIndex == records.size - 1
                        it.maxCount = maxPriceCount
                        it.noXinGaoDay = noXinGaoDay

                        it.candleEntryList = entrys
                        it.barEntryList = barEntrys
                        it.colorsList = colorEntrys
                        it.values_5 = values_5
                        it.values_10 = values_10
                        it.values_20 = values_20
                        it.yinYang = yang - yin
                        it.add = add

                        avgP1 = totalP1 / countP1
                        avgP2 = totalP2 / countP2

                        it.liangBi = avgP2 / avgP1

                    }

                    var today = lines.size - 1
                    if (cb_not_include_today.isChecked) today--

                    val info = lines.get(today)
                    val info_pre = lines.get(today - 1)
                    val info_pre2 = lines.get(today - 2)

                    val share = SharesRecordActivity.ShareInfo(info)

                    it.shouRed = share.nowPrice >= share.beginPrice

                    it.lastShareInfo = share
                    val share_pre = SharesRecordActivity.ShareInfo(info_pre)
                    val share_pre2 = SharesRecordActivity.ShareInfo(info_pre2)

                    val time = share.time

                    if (!first) {
                        hasRecord = preference?.getBoolean(time, false) ?: false
                        LogUtil.i(TAG, "time: $time, hasRecord: $hasRecord")
                        first = true
                        if(!hasRecord) {
                            edit?.putBoolean(time, true)
                            edit?.commit()
                        }
                    }

                    val zuiDaLiang = share.range > 0 && share.totalPrice >= it.maxPrice
                    it.zuiDaLiang = zuiDaLiang

                    val fangLiang = share.range > 0 && share.totalPrice > share_pre.totalPrice
                    it.fangLiang = fangLiang

                    //val a = share_pre.totalPrice > share_pre2.totalPrice  &&  share_pre.range > 0 && share_pre.nowPrice >= share_pre.beginPrice
                    val b = share.totalPrice > share_pre.totalPrice && share.range > 0 && share.nowPrice >= share.beginPrice

                    it.youXuan =  b

                    it.weekOne = it.label1 == "1"
                    it.weekTwo = it.label2 == "1"
                    it.weekThree = it.label3 == "1"
                    it.weekFour = it.label4 == "1"
                    it.weekFive = it.label5 == "1"

                    if (!hasRecord) {

                        time?.also { t ->
                            if(t.endsWith("1")) {
                                if (zuiDaLiang) {
                                    it.label1 = "1"
                                } else {
                                    it.label1 = "0"
                                }
                            } else if (t.endsWith("2")) {
                                if (zuiDaLiang) {
                                    it.label2 = "1"
                                } else {
                                    it.label2 = "0"
                                }
                            } else if (t.endsWith("3")) {
                                if (zuiDaLiang) {
                                    it.label3 = "1"
                                } else {
                                    it.label3 = "0"
                                }
                            } else if (t.endsWith("4")) {
                                if (zuiDaLiang) {
                                    it.label4 = "1"
                                } else {
                                    it.label4 = "0"
                                }
                            } else if (t.endsWith("5")) {
                                if (zuiDaLiang) {
                                    it.label5 = "1"
                                } else {
                                    it.label5 = "0"
                                }
                            }
                        }
                        FangLiangShareDatabase2.getInstance()?.getFangLiangShareDao()?.update(it)
                    }


                    it.totalRange = ((share.nowPrice) - it.beginPrice) / it.beginPrice * 100

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
        val temp  = ArrayList<FangLiangShareInfo>()
        for (info in mData) {

//            if(info.dayCount <= 2) {
//                continue
//            }


            if (cb_you_xuan.isChecked || cb_shou_hong_day.isChecked) {
                if (cb_you_xuan.isChecked && cb_shou_hong_day.isChecked.not()) {
                    if (info.youXuan.not()) continue
                } else if (cb_you_xuan.isChecked.not() && cb_shou_hong_day.isChecked) {
                    if (info.shouRed.not()) continue
                } else {
                    if(info.youXuan.not() && info.shouRed.not()) continue
                }
            }

            val selfSettingBean = settingBean
            if(selfSettingBean?.timeChecked == true && (info.dayCount < selfSettingBean.timeValue || info.dayCount > selfSettingBean.timeValue2)) {
                continue
            }

            if(selfSettingBean?.zuiDaLiang == true && !info.zuiDaLiang) {
                continue
            }
            if(selfSettingBean?.fangLiang == true && !info.fangLiang) {
                continue
            }
            if(selfSettingBean?.weekOne == true && !info.weekOne) {
                continue
            }
            if(selfSettingBean?.weekTwo == true && !info.weekTwo) {
                continue
            }
            if(selfSettingBean?.weekThree == true && !info.weekThree) {
                continue
            }
            if(selfSettingBean?.weekFour == true && !info.weekFour) {
                continue
            }
            if(selfSettingBean?.weekFive == true && !info.weekFive) {
                continue
            }
            if(selfSettingBean?.firstMax == true && !info.todayMax) {
                continue
            }

//
//            if(selfSettingBean?.rangeRightChecked == true && info.todayRange > selfSettingBean.rangeRightValue) {
//                continue
//            }
//
//            if(selfSettingBean?.liangLeftChecked == true && info.todayLiang < selfSettingBean.liangLeftValue) {
//                continue
//            }
//
//            if(selfSettingBean?.liangRightChecked == true && info.todayLiang > selfSettingBean.liangRightValue) {
//                continue
//            }

//            if(selfSettingBean?.redLine == true && !info.redLine) {
//                continue
//            }
//

            temp.add(info)
        }
        mTempData = temp
        mAdapter?.setData(mTempData)
    }

    private fun deleteItem(mineShareInfo: FangLiangShareInfo?) {
        (mData as ArrayList).remove(mineShareInfo)
        (mTempData as ArrayList).remove(mineShareInfo)
        mAdapter?.setData(mTempData)
        refreshCount()
    }

    private fun refreshCount() {
        (activity as SelfSelectionActivity).notifyData(1, XinGaoTab.TAG, mTempData.size)
    }

    private fun updateCurrentData(
        it: FangLiangShareInfo,
        share: SharesRecordActivity.ShareInfo,
        sharePre: SharesRecordActivity.ShareInfo,
        oneline: Boolean = false
    ) {
        it.todayRange = share.range
        it.todayMaxRange = share.rangeMax

        it.todayLiang = String.format("%.2f",share.totalPrice / 100000000).toDouble()

        var liangBi = "0"
        if(sharePre.totalPrice > 0) {
            liangBi = baoLiuXiaoShu(share.totalPrice / sharePre.totalPrice)
        }
        it.moreInfo = "${share.rangeBegin},  ${share.rangeMin},  ${share.rangeMax},  ${String.format("%.2f",share.totalPrice / 100000000)}亿,  ${liangBi}"

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

        private var mSharesInfoList: List<FangLiangShareInfo> = ArrayList()

        private var showFenShi = false


        fun setData(sharesInfoList: List<FangLiangShareInfo>) {
            mSharesInfoList = sharesInfoList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPoolHolder {
            return MyPoolHolder(context,
                LayoutInflater.from(context).inflate(
                    R.layout.self_selection_item_fang_liang,
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

        private var mineShareInfo: FangLiangShareInfo? = null
        private var chart: CandleStickChart? = null
        private var combinedChart: CombinedChart? = null
        private var bar: BarChart? = null
        private var divideView: View? = null

        private var heart: Boolean = false

        private var mFenShiIv: ImageView? = null

        private var tiCaiView: View? = null
        private var addTiCaiIv: ImageView? = null
        private var tiCaiTv: TextView? = null

        private var otherInfoTv: TextView? = null


        init {
            mIdTv = view.findViewById(R.id.self_item_id)
            mTimeTv = view.findViewById(R.id.self_item_time)
            mNameTv = view.findViewById(R.id.self_item_name)
            mCodeTv = view.findViewById(R.id.self_item_code)
            mRangeTv = view.findViewById(R.id.self_item_range)
            mTodayTv = view.findViewById(R.id.self_item_today)

            tiCaiView = view.findViewById(R.id.ti_cai_view)
            addTiCaiIv = view.findViewById(R.id.iv_add_ti_cai)
            tiCaiTv = view.findViewById(R.id.tv_ti_cai)

            otherInfoTv = view.findViewById(R.id.other_info)


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
                                FangLiangShareDatabase2.getInstance()?.getFangLiangShareDao()?.delete(it)
                                deleteItem(mineShareInfo)
                            }
                        }
                    }
                }.show()
                true
            }

            tiCaiView?.setOnClickListener {
                AddTiCaiDialog(context, mineShareInfo?.tiCai) {
                    if (it.isNotEmpty()) {
                        mineShareInfo?.also { info ->
                            info.tiCai = it
                            FangLiangShareDatabase2.getInstance()?.getFangLiangShareDao()?.update(info)
                            refreshTiCaiView(info)
                        }
                    }
                }.show()
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

        fun bindData(position: Int, info: FangLiangShareInfo) {
            //LogUtil.i(TAG, "bindData: ${info.code}, ${info.candleEntryList?.size}")
            this.mineShareInfo = info
            mIdTv?.text = position.toString()
            mTimeTv?.text = info.time?.substring(5) + "(${info.dayCount})"
            mNameTv?.text = info.name
            if (info.delete == 1) {
                mIdTv?.alpha = 0.4f
                mTimeTv?.alpha = 0.4f
                mNameTv?.alpha = 0.4f
                mCodeTv?.alpha = 0.4f
            } else {
                mIdTv?.alpha = 1f
                mTimeTv?.alpha = 1f
                mNameTv?.alpha = 1f
                mCodeTv?.alpha = 1f
            }

            mCodeTv?.text = info.code?.substring(2)

            otherInfoTv?.text = "${huanSuanYi(info.maxPrice)}, ${info.maxCount}, ${huanSuanYi(info.preAvg)}, ${info.noXinGaoDay}"



            if(info.currentCb == 1) {
                //LogUtil.i(TAG, "bindData: ${info.name}, ${info.lastShareInfo?.nowPrice}, ${info.beginPrice}")
                mRangeTv?.text = baoLiuXiaoShu(info.totalRange)
                if(info.totalRange >= 0) {
                    mRangeTv?.setTextColor(Color.parseColor("#f15b6c"))
                } else {
                    mRangeTv?.setTextColor(Color.parseColor("#7fb80e"))
                }
            }
            if(info.currentCb == 2) {
                mRangeTv?.text = huanSuanYi(info.maxPrice)
                if(info.maxPrice >= 500000000) {
                    mRangeTv?.setTextColor(Color.parseColor("#f15b6c"))
                } else {
                    mRangeTv?.setTextColor(Color.parseColor("#7fb80e"))
                }
            }

            if(info.currentCb == 3) {
                mRangeTv?.text = baoLiuXiaoShu(info.todayLiang)
                mRangeTv?.setTextColor(Color.parseColor("#f15b6c"))
            }

            if(info.currentCb == 4) {
                mRangeTv?.text = "${info.yinYang}"
                if(info.yinYang >= 0) {
                    mRangeTv?.setTextColor(Color.parseColor("#f15b6c"))
                } else {
                    mRangeTv?.setTextColor(Color.parseColor("#7fb80e"))
                }
            }

            if(info.currentCb == 5) {
                mRangeTv?.text = "${info.maxCount}"
                mRangeTv?.setTextColor(Color.parseColor("#f15b6c"))
            }

            if(info.currentCb == 6) {
                mRangeTv?.text = baoLiuXiaoShu(info.liangBi)
                mRangeTv?.setTextColor(Color.parseColor("#f15b6c"))
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

            refreshTiCaiView(info)

            mFenShiIv?.visibility = View.GONE
        }

        private fun refreshTiCaiView(info: FangLiangShareInfo) {
            if (info.tiCai.isEmpty()) {
                tiCaiTv?.visibility = View.GONE
                addTiCaiIv?.visibility = View.VISIBLE
            } else {
                tiCaiTv?.visibility = View.VISIBLE
                addTiCaiIv?.visibility = View.GONE
                tiCaiTv?.text = info.tiCai
            }
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
        (activity as SelfSelectionActivity).notifyData(2, XinGaoTab.TAG, mRefreshCount)
        LogUtil.i(TAG, "mRefreshCount: $mRefreshCount")
        if(mRefreshCount == mTempData.size) {
            mAdapter?.setData(mTempData)
            (activity as SelfSelectionActivity).notifyData(3, XinGaoTab.TAG, 0)
            resetSortUI()
        }
    }
}