package com.libiao.mushroom.mine.test

import android.graphics.Color
import android.os.Environment
import android.os.Handler
import android.os.Looper
import com.airbnb.mvrx.MavericksViewModel
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.mine.fragment.BaseFragment
import com.libiao.mushroom.room.TestShareDatabase
import com.libiao.mushroom.room.TestShareInfo
import com.libiao.mushroom.utils.LogUtil
import com.libiao.mushroom.utils.baoLiuXiaoShu
import com.libiao.mushroom.utils.huanSuanYi
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.Charset

class TestViewModel(initial: TestState): MavericksViewModel<TestState>(initial) {

    companion object {
        private const val TAG = "TestViewModel"
    }

    private val file_2023 = File(Environment.getExternalStorageDirectory(), "A_SharesInfo/2023")

    var localList = mutableListOf<TestShareInfo>()
    val handler = Handler(Looper.getMainLooper())

    var isMorePrice = false
    var isLessPrice = false

    var xingao = false
    var all = false
    var xingao2 = false
    var gengQiang = false

    var zhenDang = false

    var dazhangcheck = false

    var maxPrice = false
    var xinGaoAgain = false

    var dayCheck = false

    var gao2XinLiang = false
    var gao2LiangChecked = false
    var gao2Liang = 0

    var time = false

    var paiXu = 0
    var zhengXu = false
    var heXin = false

    val day1 = 60

    var twoLianBan = false

    var kuiChecked = false
    var zhuanChencked = false


    var yearY = 2024

    fun fetchInfo(month: Int, dixi: Double, result: (info: String)->Unit) {

        withState {

            if(month == 11) {
               // TestShareDatabase.getInstance()?.getTestShareDao()?.deleteTest("8888")
            }
            val data: MutableList<TestShareInfo>?
            data = TestShareDatabase.getInstance()?.getTestShareDao()?.getZhenDang()

            LogUtil.i(TAG, "fetchInfo: ${data?.size}, $kuiChecked")
            val dataT = ArrayList<TestShareInfo>()
            var totalRange = 0.00
            var totalRangeZhuan = 0.00
            var totalRangeKui = 0.00
            var zhuan = 0
            var kui = 0
            var fanbao = 0
            var fanBaokui = 0.00
            var fanBaozhuan = 0.00


            var zhang = 0.00

            data?.forEach {
                if(it.dayCount >= 0 && isFit(month, it.time!!, yearY)) {
                    val f = File(file_2023, it.code)
                    if(f.exists()) {
                        val stream = FileInputStream(f)
                        val reader = BufferedReader(InputStreamReader(stream, Charset.defaultCharset()))
                        val lines = reader.readLines()

                        var updateIndex = 0

                        lines.forEachIndexed{ index, line ->
                            if(line.startsWith(it.updateTime!!)) {
                                updateIndex = index
                                return@forEachIndexed
                            }
                        }

                        if(it.candleEntryList == null) {

                            var blackIndex = 20
                            var a = updateIndex - blackIndex
                            if(a < 0) {
                                a = 0
                                blackIndex = updateIndex
                            }
                            var b = updateIndex + 20

                            if(lines.size < b) b = lines.size
                            val records = lines.subList(a, b)
                            val candleEntrys = java.util.ArrayList<CandleEntry>()
                            val barEntrys = java.util.ArrayList<BarEntry>()
                            val colorEntrys = java.util.ArrayList<Int>()
                            val values_5 = java.util.ArrayList<Entry>()
                            val values_10 = java.util.ArrayList<Entry>()
                            val values_20 = java.util.ArrayList<Entry>()

                            var itemBlack: SharesRecordActivity.ShareInfo? = null
                            var itemBlackPost: SharesRecordActivity.ShareInfo? = null
                            var itemBlackPost2: SharesRecordActivity.ShareInfo? = null
                            var itemBlackPost3: SharesRecordActivity.ShareInfo? = null
                            var itemBlackPre: SharesRecordActivity.ShareInfo? = null
                            var itemBlackPre2: SharesRecordActivity.ShareInfo? = null


                            var maxLiang = 0.00

                            records.forEachIndexed { index, s ->
                                //if(index == 0) return@forEachIndexed
                                val item = SharesRecordActivity.ShareInfo(s)

                                if(dayCheck && records.size > day1) {
                                    if(index > day1) {
                                        if(item.beginPrice == 0.00) {
                                            candleEntrys.add(CandleEntry((index).toFloat(), item.nowPrice.toFloat(), item.nowPrice.toFloat(), item.nowPrice.toFloat(), item.nowPrice.toFloat()))
                                        } else {
                                            candleEntrys.add(CandleEntry((index).toFloat(), item.maxPrice.toFloat(), item.minPrice.toFloat(), item.beginPrice.toFloat(), item.nowPrice.toFloat()))
                                        }
                                    }
                                } else {
                                    if(item.beginPrice == 0.00) {
                                        candleEntrys.add(CandleEntry((index).toFloat(), item.nowPrice.toFloat(), item.nowPrice.toFloat(), item.nowPrice.toFloat(), item.nowPrice.toFloat()))
                                    } else {
                                        candleEntrys.add(CandleEntry((index).toFloat(), item.maxPrice.toFloat(), item.minPrice.toFloat(), item.beginPrice.toFloat(), item.nowPrice.toFloat()))
                                    }
                                }

                                var line5 = item.line_5
                                if(line5 == 0.00) { line5 = item.beginPrice }

                                var line10 = item.line_10
                                if(line10 == 0.00) { line10 = item.beginPrice }

                                var line20 = item.line_20
                                if(line20 == 0.00) { line20 = item.beginPrice }

                                if(dayCheck && records.size > day1) {
                                    if(index > day1) {
                                        values_5.add(Entry(index.toFloat(), line5.toFloat()))
                                        values_10.add(Entry(index.toFloat(), line10.toFloat()))
                                        values_20.add(Entry(index.toFloat(), line20.toFloat()))
                                    }
                                } else {
                                    values_5.add(Entry(index.toFloat(), line5.toFloat()))
                                    values_10.add(Entry(index.toFloat(), line10.toFloat()))
                                    values_20.add(Entry(index.toFloat(), line20.toFloat()))
                                }


                                val p = item.totalPrice.toFloat() / 100000000
                                if(dayCheck && records.size > day1) {
                                    if(index > day1) {
                                        barEntrys.add(BarEntry(index.toFloat(), p))
                                    }
                                } else {
                                    barEntrys.add(BarEntry(index.toFloat(), p))
                                }

                                if(index == blackIndex - 1) {
                                    itemBlackPre = item
                                }
                                if(index == blackIndex - 2) {
                                    itemBlackPre2 = item
                                }

                                if(index == blackIndex + 1) {
                                    itemBlackPost = item
                                }

                                if(index == blackIndex + 2) {
                                    itemBlackPost2 = item
                                }

                                if(index == blackIndex + 3) {
                                    itemBlackPost3 = item
                                }


                                if(index == blackIndex) {
                                    colorEntrys.add(Color.BLACK)
                                    itemBlack = item
                                } else {
                                    val green = "#28FF28"
                                    val red = "#FF0000"

                                    if(dayCheck && records.size > day1) {
                                        if(index > day1) {
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

                                if(item.totalPrice > maxLiang) {
                                    maxLiang = item.totalPrice
                                }

                                if(index == records.size - 1) {
                                    it.lastShareInfo = item
                                }
                            }
                            it.candleEntryList = candleEntrys
                            it.barEntryList = barEntrys
                            it.colorsList = colorEntrys
                            it.values_5 = values_5
                            it.values_10 = values_10
                            it.values_20 = values_20


                            it.moreInfo = "${itemBlack?.rangeBegin}, ${itemBlack?.rangeMin}, ${itemBlack?.rangeMax}, ${itemBlack?.range}, ${huanSuanYi(itemBlack?.totalPrice ?: 0.00)}"

                            if (itemBlackPost != null) {
                                it.moreInfo3 = "${baoLiuXiaoShu(itemBlackPost!!.rangeBegin)}, ${baoLiuXiaoShu(itemBlackPost!!.range)}"
                            }

                            if (itemBlackPost2 != null) {
                                it.moreInfo3 = "${baoLiuXiaoShu(itemBlackPost!!.range - itemBlackPost!!.rangeBegin + itemBlackPost2!!.range)}"
                            }

                            if (itemBlackPost != null) {
                                dataT.add(it)
                                if(itemBlackPost != null && itemBlackPost2 != null) {
                                    val b = itemBlackPost!!.rangeBegin
                                    val r = itemBlackPost!!.range - b + itemBlackPost2!!.range
                                    it.rT = r
                                    zhang += r
                                }
                            } else {
                                dataT.add(it)
                            }



//                            itemBlackPre2?.also { pre2 ->
//                                val a = itemBlackPre!!.rangeMax - itemBlackPre!!.rangeMin < 7
//                                val b = itemBlack!!.rangeMax - itemBlack!!.rangeMin < 7
//                                if (pre2.nowPrice < pre2.beginPrice && a && b) {
//
//                                }
//                            }

                        }
                    }
                }

            }

            LogUtil.i(TAG, "totalRange: $totalRange")
            dataT.sortByDescending { it.time!!.split("-")[2].toInt() }

            val r = oneDayCount(dataT)

            handler.post {
                result("涨 ${baoLiuXiaoShu(zhang)}, ${baoLiuXiaoShu(r)}")
            }
            dataT.also {
                localList = it
                setState {
                    copy(infoList = it)
                }
            }
        }
    }

    fun zhangBan(info: SharesRecordActivity.ShareInfo): Boolean {
        var maxRange = 1.1
        if(info.code?.startsWith("sz3") == true || info.code?.startsWith("sh688") == true) {
            maxRange = 1.2
        }
        var zhangTingPrice = info.yesterdayPrice * maxRange - 0.005
        zhangTingPrice = String.format("%.2f",zhangTingPrice).toDouble()
        val a = info.nowPrice > 0 && info.maxPrice >= zhangTingPrice && zhangTingPrice > 0
        return a
    }

    fun isChuang(code: String?): Boolean {
        return code?.startsWith("sz3") ?: false
    }

    fun isKeChuang(code: String?): Boolean {
        return code?.startsWith("sh688") ?: false
    }

    private fun oneDayCount2(dataT: java.util.ArrayList<TestShareInfo>) {
        var time = ""
        val dataTime = ArrayList<TestShareInfo>()
        var count = 0
        dataT.forEach {
            if(this.time) {
                if(it.time == time) {
                    dataTime.add(it)
                } else {
                    count = 0
                    time = it.time!!
                    if(dataTime.size > 0) {
                        dataTime[0].count = "${dataTime.size}"
                    }
                    dataTime.clear()
                    dataTime.add(it)
                }
            }
            count++
            it.index = count
        }
        if(dataTime.size > 0) {
            dataTime[0].count = "${dataTime.size}"
        }
        dataTime.clear()
    }

    private fun oneDayCount(dataT: java.util.ArrayList<TestShareInfo>): Double {
        var count = 0
        var time = ""
        val dataTime = ArrayList<TestShareInfo>()
        var r = 0.00
        var rT = 0.00
        dataT.forEach {
            if(it.updateTime == time) {
                dataTime.add(it)
                r += it.rT
            } else {
                count = 0
                time = it.updateTime!!
                if(dataTime.size > 0) {
                    dataTime[0].count = "${dataTime.size}"
                    val s = if (dataTime.size > 4) 4 else dataTime.size
                    val oneR = (r / dataTime.size * s)
                    dataTime[0].oneR = oneR
                    rT += oneR
                }
                dataTime.clear()
                r = 0.00
                dataTime.add(it)
                r += it.rT
            }
            count++
            it.index = count
        }
        if(dataTime.size > 0) {
            dataTime[0].count = "${dataTime.size}"
            val s = if (dataTime.size > 4) 4 else dataTime.size
            val oneR = (r / dataTime.size * s)
            dataTime[0].oneR = oneR
            rT += oneR
        }
        dataTime.clear()
        return rT
    }

    private fun isfitCheck(ext5: String?): Boolean {
//        if(test && ext5 == "5") return true
//        if(xingao && ext5 == "1") return true
//        if(fangliang && ext5 == "2") return true
        if(zhenDang && ext5 == "4") return true
        if(!zhenDang && ext5 == "7") return true
        return false
    }

    private fun isFit(month: Int, time: String, year: Int): Boolean {
        when(month) {
            1 -> {
                return (time.startsWith("$year-1") || time.startsWith("$year-01")) && time.startsWith("$year-10").not() && time.startsWith("$year-11").not() && time.startsWith("$year-12").not()
            }
            2 -> {
                return time.startsWith("$year-2") || time.startsWith("$year-02")
            }
            3 -> {
                return time.startsWith("$year-3") || time.startsWith("$year-03")
            }
            4 -> {
                return time.startsWith("$year-4") || time.startsWith("$year-04")
            }
            5 -> {
                return time.startsWith("$year-5") || time.startsWith("$year-05")
            }
            6 -> {
                return time.startsWith("$year-6") || time.startsWith("$year-06")
            }
            7 -> {
                return time.startsWith("$year-7") || time.startsWith("$year-07")
            }
            8 -> {
                return time.startsWith("$year-8") || time.startsWith("$year-08")
            }
            9 -> {
                return time.startsWith("$year-9") || time.startsWith("$year-09")
            }
            10 -> {
                return time.startsWith("$year-10")
            }
            11 -> {
                return time.startsWith("$year-11")
            }
            12 -> {
                return time.startsWith("$year-12")
            }
        }
        return false
    }

    fun deleteItem(item: TestShareInfo) {
        withState {
            val list = mutableListOf<TestShareInfo>()
            list.addAll(it.infoList)
            list.remove(item)
            setState {
                it.copy(infoList = list)
            }
        }
    }

    fun sortMaxPrice(callback: (s: String) -> Unit) {
        withState {
            val list = mutableListOf<TestShareInfo>()
            list.addAll(it.infoList)
            list.sortByDescending { info -> info.lastShareInfo?.totalPrice }
            val s1 = "${list[0].lastShareInfo?.name}, ${String.format("%.1f",(list[0].lastShareInfo?.totalPrice ?: 0.00) / 100000000)}亿"
            val s2 = "${list[1].lastShareInfo?.name}, ${String.format("%.1f",(list[1].lastShareInfo?.totalPrice ?: 0.00) / 100000000)}亿"
            val s3 = "${list[2].lastShareInfo?.name}, ${String.format("%.1f",(list[2].lastShareInfo?.totalPrice ?: 0.00) / 100000000)}亿"
            val s4 = "${list[3].lastShareInfo?.name}, ${String.format("%.1f",(list[3].lastShareInfo?.totalPrice ?: 0.00) / 100000000)}亿"
            val s5 = "${list[4].lastShareInfo?.name}, ${String.format("%.1f",(list[4].lastShareInfo?.totalPrice ?: 0.00) / 100000000)}亿"
            val s = "$s1\n$s2\n$s3\n$s4\n$s5"
            callback.invoke(s)
        }
    }

    fun updateNetWork(baseFragment: BaseFragment) {
        withState {s ->
            val temp = mutableListOf<TestShareInfo>()
            temp.addAll(s.infoList)
            var count = 0
            temp.forEachIndexed { index, fangLiangShareInfo ->
                baseFragment.shiShiQuery(fangLiangShareInfo.code!!) {share ->
                    LogUtil.i(TAG, "share: ${share}")
                    count ++

                    val fangInfo = fangLiangShareInfo.copy()

                    val candleSize = fangInfo.candleEntryList?.size ?: 0
                    val lastShare = fangInfo.lastShareInfo

                    var liangBi = "0"
                    if(share.totalPrice > 0) {
                        liangBi = baoLiuXiaoShu(share.totalPrice / lastShare!!.totalPrice)
                    }

                    fangInfo.lastShareInfo = share

                    if(share.beginPrice == 0.00) {
                        fangInfo.candleEntryList?.add(CandleEntry(candleSize.toFloat(), share.nowPrice.toFloat(), share.nowPrice.toFloat(), share.nowPrice.toFloat(), share.nowPrice.toFloat()))
                    } else {
                        fangInfo.candleEntryList?.add(CandleEntry(candleSize.toFloat(), share.maxPrice.toFloat(), share.minPrice.toFloat(), share.beginPrice.toFloat(), share.nowPrice.toFloat()))
                    }
                    //fangInfo.moreInfo = "${share.rangeBegin},  ${share.rangeMin},  ${share.rangeMax},  $liangBi"
                    val p = share.totalPrice.toFloat() / 100000000
                    fangInfo.barEntryList?.add(BarEntry(candleSize.toFloat(), p))
                    fangInfo.colorsList?.add(Color.GRAY)
                    temp[index] = fangInfo

                    if(count == temp.size) {
                        LogUtil.i(TAG, "setState.......................")
                        setState {
                            copy(infoList = temp)
                        }
                    }
                }
            }
        }
    }

    fun updateLocal() {
        withState {s ->
            setState {
                copy(infoList = localList)
            }
        }
    }

    fun expand(time: String) {
        withState {
            val list = mutableListOf<TestShareInfo>()
            list.addAll(it.infoList)
            list.forEachIndexed {index, info ->
                val temp = info.copy()
                if(temp.time == time) {
                    temp.expand = !temp.expand
                }
                list[index] = temp
            }
            setState {
                it.copy(infoList = list)
            }
        }
    }

    fun setMorePriceChecked(checked: Boolean) {
        isMorePrice = checked
    }

    fun setLessPricechecked(checked: Boolean) {
        isLessPrice = checked
    }

    fun setAllChecked(checked: Boolean) {
        all = checked
    }

    fun setZhuanCheck(checked: Boolean) {
        zhuanChencked = checked
    }
    fun setKuiCheck(checked: Boolean) {
        kuiChecked = checked
    }

    fun setYear(y: Int) {
        yearY = y
    }

    fun setMaxPriceChecked(checked: Boolean) {
        maxPrice = checked
    }

    fun setXinGaoAgainChecked(checked: Boolean) {
        xinGaoAgain = checked
    }

    fun set120Checked(checked: Boolean) {
        dayCheck = checked
    }

    fun setXinGao2Checked(checked: Boolean) {
        xingao2 = checked
    }

    fun setGao2XinLiangChecked(checked: Boolean) {
        gao2XinLiang = checked
    }

    fun setGao2LiangChecked(checked: Boolean, liang: Int) {
        LogUtil.i(TAG, "setGao2LiangChecked: $checked, $liang")
        gao2LiangChecked = checked
        gao2Liang = liang
    }

    fun setTimeChecked(checked: Boolean) {
        time = checked
    }

    fun setPaiXuSelect(position: Int) {
        paiXu = position
    }

    fun setZhengXuChecked(checked: Boolean) {
        zhengXu = checked
    }

    private fun getPaiXuType(info: TestShareInfo): Double {
        return when(paiXu) {
            0 -> info.zuiDaLiangCount.toDouble()
            1 -> info.maxCount.toDouble()
            2 -> info.lastShareInfo?.range ?: 0.00
            3 -> info.dayCount.toDouble()
            else -> info.zuiDaLiangCount.toDouble()
        }
    }

    fun setHeXinChecked(checked: Boolean) {
        heXin = checked
    }

    fun setXindi(checked: Boolean) {
        zhenDang = checked
    }

    fun setDaZhang(checked: Boolean) {
        dazhangcheck = checked
    }
}