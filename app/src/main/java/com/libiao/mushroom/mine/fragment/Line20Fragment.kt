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
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.libiao.mushroom.R
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.kline.KLineActivity
import com.libiao.mushroom.mine.*
import com.libiao.mushroom.mine.tab.Line20Tab
import com.libiao.mushroom.room.MineShareDatabase
import com.libiao.mushroom.room.MineShareInfo
import com.libiao.mushroom.thread.ThreadPoolUtil
import com.libiao.mushroom.utils.*
import kotlinx.android.synthetic.main.line_20_fragment.*
import java.io.*
import java.nio.charset.Charset

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
                        val f = File(file_2021, it.code!!)
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

    private var mData: List<MineShareInfo> = ArrayList()
    private var mTempData: List<MineShareInfo> = ArrayList()

    private var settingDialog: SelfSettingDialog? = null
    private var settingBean: SelfSettingBean? = SelfSettingBean()

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
                    //???-???
                    //???????????????temp??? ????????????????????????????????????
                    mTempData = ArrayList(mTempData.sortedByDescending { it.totalRange })
                    mAdapter?.setData(mTempData)
                }
                1 -> {
                    iv_self_range.setImageResource(R.mipmap.sort_ascending)
                    mRangeStatus = 2
                    //???-???
                    mTempData = ArrayList(mTempData.sortedBy { it.totalRange })
                    mAdapter?.setData(mTempData)
                }
                2 -> {
                    iv_self_range.setImageResource(R.mipmap.sort_descending)
                    mRangeStatus = 1
                    //???-???
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
                    //???-???
                    mTempData = ArrayList(mTempData.sortedByDescending { it.todayRange })
                    mAdapter?.setData(mTempData)

                }
                1 -> {
                    iv_self_today.setImageResource(R.mipmap.sort_ascending)
                    mTodayStatus = 2
                    //???-???
                    mTempData = ArrayList(mTempData.sortedBy { it.todayRange })
                    mAdapter?.setData(mTempData)

                }
                2 -> {
                    iv_self_today.setImageResource(R.mipmap.sort_descending)
                    mTodayStatus = 1
                    //???-???
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
                    //???-???
                    mTempData = ArrayList(mTempData.sortedByDescending { it.dayCount })
                    mAdapter?.setData(mTempData)
                }
                1 -> {
                    iv_self_time.setImageResource(R.mipmap.sort_ascending)
                    mTimeStatus = 2
                    //???-???
                    mTempData = ArrayList(mTempData.sortedBy { it.dayCount })
                    mAdapter?.setData(mTempData)
                }
                2 -> {
                    iv_self_time.setImageResource(R.mipmap.sort_descending)
                    mTimeStatus = 1
                    //???-???
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

    private fun refreshLocalData(data: List<MineShareInfo>, reload: Boolean = false) {
        //(activity as SelfSelectionActivity).showLoading()
        loadingStatus = 1
        ThreadPoolUtil.execute(Runnable {
            data.forEach {
                val f = File(file_2021, it.code)
                if(f.exists()) {
                    val stream = FileInputStream(f)
                    val reader = BufferedReader(InputStreamReader(stream, Charset.defaultCharset()))
                    val lines = reader.readLines()
                    val one = SharesRecordActivity.ShareInfo(lines[lines.size - it.dayCount - 2])
                    val two = SharesRecordActivity.ShareInfo(lines[lines.size - it.dayCount - 1])
                    var allPrice = 0.00
                    var allDay = 0
                    if(it.candleEntryList == null || reload) {
                        var begin = lines.size - it.dayCount - 2
                        if(begin < 0) begin = 0
                        val records = lines.subList(begin, lines.size)
                        val entrys = java.util.ArrayList<CandleEntry>()
                        val barEntrys = java.util.ArrayList<BarEntry>()
                        val colorEntrys = java.util.ArrayList<Int>()
                        var maxPrice = one.nowPrice
                        var totalP = 0.00
                        var itemIn = 0
                        records.forEachIndexed { index, s ->
                            val item = SharesRecordActivity.ShareInfo(s)

                            if(index > 0) {
                                if(item.totalPrice > 0) {
                                    allPrice += item.totalPrice
                                    allDay ++
                                }
                            }

                            if(item.totalPrice > totalP) {
                                totalP = item.totalPrice
                                itemIn = index
                            }
                            if(item.nowPrice > maxPrice) maxPrice = item.nowPrice
                            if(item.beginPrice == 0.00) {
                                entrys.add(CandleEntry((index).toFloat(), item.nowPrice.toFloat(), item.nowPrice.toFloat(), item.nowPrice.toFloat(), item.nowPrice.toFloat()))
                            } else {
                                entrys.add(CandleEntry((index).toFloat(), item.maxPrice.toFloat(), item.minPrice.toFloat(), item.beginPrice.toFloat(), item.nowPrice.toFloat()))
                            }

                            val p = item.totalPrice.toFloat() / 100000000
                            barEntrys.add(BarEntry(index.toFloat(), p))
                            val green = "#28FF28"
                            val red = "#FF0000"
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
                        val tMaxItem = SharesRecordActivity.ShareInfo(records[itemIn])
                        it.sanLianYin = false
                        if(tMaxItem.nowPrice > tMaxItem.beginPrice) {
                            it.sanLianYin = true
                        }

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
                    }
                    it.price = one.nowPrice
                    val info = lines.get(lines.size - 1)
                    val info_pre = lines.get(lines.size - 2)

                    val share = SharesRecordActivity.ShareInfo(info)
                    it.shareInfo = share
                    val share_pre = SharesRecordActivity.ShareInfo(info_pre)
                    if(it.dayCount >= 2) {

                        val ten1 = share
                        val ten2 = share_pre

                        if(ten1.totalPrice > ten2.totalPrice) {
                            it.zhiDie = true
                        }

                        //????????????
//                        it.sanLianYin = false
//                        it.tips = ""
//                        if(ten1.nowPrice >= ten1.beginPrice && ten2.nowPrice >= ten2.beginPrice) {
//                            //??????
//                            if(!ShareParseUtil.zhangTing(ten1) && !ShareParseUtil.zhangTing(ten2)) {
//                                if(ten1.totalPrice >= ten2.totalPrice && ten1.range > 0 || ten1.totalPrice < ten2.totalPrice && ten1.range < 0) {
//                                    it.sanLianYin = true
//                                    it.tips += "?????? "
//                                }
//                            }
//                        }
//
//                        if(lines.size >= 3) {
//                            //??????????????????
//                            val info_pre2 = lines.get(lines.size - 3)
//                            val share_pre2 = SharesRecordActivity.ShareInfo(info_pre2)
//                            val ten3 = share_pre2
//                            if(ten1.nowPrice >= ten1.beginPrice && ten2.beginPrice >= ten2.nowPrice && ten1.totalPrice > ten2.totalPrice * 1.3) {
//                                if(!ShareParseUtil.zhangTing(ten1) && !ShareParseUtil.zhangTing(ten2)) {
//                                    if(ten3.beginPrice >= ten3.nowPrice) {
//                                        it.sanLianYin = true
//                                        it.tips += "?????????????????? "
//                                    }
//                                }
//                            }
//                            if(lines.size >= 4) {
//                                val info_pre3 = lines.get(lines.size - 4)
//                                val share_pre3 = SharesRecordActivity.ShareInfo(info_pre3)
//                                val ten4 = share_pre3
//                                // ????????????
//                                if(ten4.nowPrice > ten4.beginPrice && ten3.nowPrice > ten3.beginPrice && ten2.beginPrice >= ten2.nowPrice && ten1.beginPrice >= ten1.nowPrice) {
//                                    if(ten3.totalPrice > ten2.totalPrice && ten2.totalPrice > ten1.totalPrice) {
//                                        it.sanLianYin = true
//                                        it.tips += "???????????? "
//                                    }
//                                }
//                            }
//
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

            if(selfSettingBean?.sanLianYin == true && !info.sanLianYin) {
                continue
            }

            if(selfSettingBean?.maxRangChecked == true && info.maxRange < selfSettingBean.maxRangValue) {
                continue
            }

            if(selfSettingBean?.zhiDie == true && !info.zhiDie) {
                continue
            }

            if(selfSettingBean?.duan_ceng_Checked == true && info.label1 != "??????") {
                continue
            }

            if(selfSettingBean?.xin_gao == true && (info.maxRange != info.totalRange)) {
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
            it.moreInfo = "${share.rangeBegin},  ${share.rangeMin},  ${share.rangeMax},  ${String.format("%.2f",share.totalPrice / 100000000)}???,  ${liangBi}"
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
        private var bar: BarChart? = null

        private var heart: Boolean = false

        private var mTypeXinGaoTv: TextView? = null
        private var mTypeMaxRangeTv: TextView? = null
        private var mTypeZhiDieTv: TextView? = null

        private var mAvgP: TextView? = null


        init {
            mIdTv = view.findViewById(R.id.self_item_id)
            mTimeTv = view.findViewById(R.id.self_item_time)
            mNameTv = view.findViewById(R.id.self_item_name)
            mCodeTv = view.findViewById(R.id.self_item_code)
            mRangeTv = view.findViewById(R.id.self_item_range)
            mTodayTv = view.findViewById(R.id.self_item_today)
            mTypeXinGaoTv = view.findViewById(R.id.tv_type_xin_gao)
            mTypeMaxRangeTv = view.findViewById(R.id.tv_type_max_rang)
            mTypeZhiDieTv = view.findViewById(R.id.tv_type_zhi_die)
            mAvgP = view.findViewById(R.id.tv_type_avg_p)

            moreInfoTv = view.findViewById(R.id.tv_more_info)
            heartIv = view.findViewById(R.id.iv_item_heart)
            chart = view.findViewById(R.id.item_candler_chart)
            bar = view.findViewById(R.id.item_bar_chart)
            initCandleChart()
            intBarChart()

            view.setOnClickListener {
                val intent = Intent(context, KLineActivity::class.java)
                intent.putExtra("code", mineShareInfo?.code)
                intent.putExtra("info", mineShareInfo.toString())
                context.startActivity(intent)
            }

            view.setOnLongClickListener {
                LogUtil.i(TAG, "??????")
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

        private fun initCandleChart() {
            chart?.also {
                it.setTouchEnabled(false)
                it.description.isEnabled = false
                it.setDrawGridBackground(false) // ????????????????????????
                //it.setBackgroundColor(Color.WHITE) // ????????????
                it.setPinchZoom(false) // if disabled, scaling can be done on x- and y-axis separately
                it.isLogEnabled = true
                it.isSelected = false

                val xAxis: XAxis = it.xAxis
                xAxis.isEnabled = false

                val leftAxis: YAxis = it.axisLeft
                leftAxis.isEnabled = false

                val rightAxis: YAxis = it.axisRight
                rightAxis.isEnabled = false

                val l: Legend = it.legend // ?????????????????????
                l.isEnabled = false //????????????????????????

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

            if(info.maxRange == info.totalRange) {
                mTypeXinGaoTv?.visibility = View.VISIBLE
            } else {
                mTypeXinGaoTv?.visibility = View.GONE
            }

            mTypeMaxRangeTv?.text = baoLiuXiaoShu(info.maxRange)

            mAvgP?.text = "${String.format("%.2f",info.avgP / 100000000)}???"
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