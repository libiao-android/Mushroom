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
import com.libiao.mushroom.room.test.TestShareDatabase2
import com.libiao.mushroom.utils.LogUtil
import com.libiao.mushroom.utils.ShareParseUtil
import com.libiao.mushroom.utils.baoLiuXiaoShu
import com.libiao.mushroom.utils.huanSuanYi
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import kotlin.math.max

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

    var xinDi = false

    var test = false

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

    fun fetchInfo(month: Int, result: (info: String)->Unit) {

        withState {

            if(month == 6) {
               //TestShareDatabase.getInstance()?.getTestShareDao()?.deleteTest("8")
            }
            val data: MutableList<TestShareInfo>?
            if (twoLianBan) {
                data = TestShareDatabase.getInstance()?.getTestShareDao()?.getTwoLianBanShares()
            } else if(xinDi) {
                data = TestShareDatabase.getInstance()?.getTestShareDao()?.getXindiShares()
            } else {
                data = TestShareDatabase.getInstance()?.getTestShareDao()?.getMineTestShares()
            }

            LogUtil.i(TAG, "fetchInfo: ${data?.size}")
            val dataT = ArrayList<TestShareInfo>()
            var totalRange = 0.00
            var totalRangeZhuan = 0.00
            var totalRangeKui = 0.00
            var zhuan = 0
            var kui = 0
            var fanbao = 0
            var fanBaokui = 0.00
            var fanBaozhuan = 0.00
            data?.forEach {
                if(it.dayCount >= 0 && isFit(month, it.time!!)) {
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

                            var blackIndex = it.dayCount + 1
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
                            var itemBlack2: SharesRecordActivity.ShareInfo? = null
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

                                if(index == 123) {
                                    itemBlackPost3 = item
                                }


                                if(index == blackIndex) {
                                    colorEntrys.add(Color.BLACK)
                                    itemBlack = item
                                } else if(!twoLianBan && !all && index == blackIndex + 1){
                                    colorEntrys.add(Color.BLACK)
                                } else if(index > 1 && item.totalPrice > maxLiang && item.nowPrice > item.beginPrice && !twoLianBan){
                                    colorEntrys.add(Color.GRAY)
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

                            if(twoLianBan) {
                                if(itemBlack?.code?.startsWith("sh688") != true) {
                                    if(itemBlack!!.totalPrice > itemBlackPre!!.totalPrice) {
                                        if(itemBlackPost != null && itemBlackPost2 != null) {
                                            val r = itemBlackPost!!.range - itemBlackPost!!.rangeBegin + itemBlackPost2!!.range
                                            if(r > 0) {
                                                zhuan ++
                                            } else {
                                                kui++
                                            }
                                            totalRange += r
                                        }
                                        dataT.add(it)
                                    }
                                }
                                return@forEach
                            }

                            if(xinDi) {
                                val aa = "${itemBlack!!.range}, ${huanSuanYi(itemBlack!!.totalPrice)}"
                                it.moreInfo = "$aa, ${it.ext4}"
                                if(itemBlackPost != null &&
                                    (itemBlackPost!!.totalPrice > itemBlack!!.totalPrice || itemBlackPost!!.nowPrice > itemBlack!!.nowPrice)) {
                                    if(itemBlack!!.range >= 7.5) {
                                        dataT.add(it)
                                    }
                                }
                                return@forEach
                            }

                            if(all) {
                                it.moreInfo = "${itemBlack?.range}, ${huanSuanYi(itemBlack?.totalPrice ?: 0.00)}"

                                if(itemBlack!!.rangeMax - itemBlack!!.range < itemBlack!!.range) {
                                    if(!ShareParseUtil.zhangTing(itemBlack!!) && !ShareParseUtil.zhangTingMax(itemBlack!!) && itemBlack!!.rangeMax > 7) {
                                        if(itemBlackPost != null) {
                                            LogUtil.i(TAG, "${itemBlackPost!!.code}, ${itemBlackPost!!.range}")
                                            totalRange += itemBlackPost!!.range
                                            it.moreInfo += " / ${itemBlackPost?.range}, ${itemBlackPost?.rangeMax}"
                                            if(itemBlackPost!!.range > 0) {
                                                if(zhuanChencked) {
                                                    dataT.add(it)
                                                }
                                                zhuan++
                                                totalRangeZhuan += itemBlackPost!!.range
                                            } else {
                                                kui++
                                                if(kuiChecked) {
                                                    dataT.add(it)
                                                }
                                                totalRangeKui += itemBlackPost!!.range
                                                if(itemBlackPost2 != null) {
                                                    if(itemBlackPost2!!.range > 0) {
                                                        fanbao ++
                                                        fanBaozhuan += itemBlackPost2!!.range
                                                    } else {
                                                        fanBaokui += itemBlackPost2!!.range
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }


                            } else {
                                dataT.add(it)
                            }
                        }
                    }
                }

            }
            handler.post {
                result("赚${zhuan}, 亏${kui}, 赚${baoLiuXiaoShu(totalRangeZhuan)}, 亏${baoLiuXiaoShu(totalRangeKui)}, 总${baoLiuXiaoShu(totalRange)}," +
                        "\n反包${fanbao}, ${baoLiuXiaoShu(fanBaozhuan)}, ${baoLiuXiaoShu(fanBaokui)}")
            }
            LogUtil.i(TAG, "totalRange: $totalRange")
            dataT.sortByDescending { it.time!!.split("-")[2].toInt() }

            oneDayCount(dataT)

            dataT.also {
                localList = it
                setState {
                    copy(infoList = it)
                }
            }
        }
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

    private fun oneDayCount(dataT: java.util.ArrayList<TestShareInfo>) {
        var count = 0
        var time = ""
        val dataTime = ArrayList<TestShareInfo>()
        dataT.forEach {
            if(it.updateTime == time) {
                dataTime.add(it)
            } else {
                count = 0
                time = it.updateTime!!
                if(dataTime.size > 0) {
                    dataTime[0].count = "${dataTime.size}"
                }
                dataTime.clear()
                dataTime.add(it)
            }
            count++
            it.index = count
        }
        if(dataTime.size > 0) {
            dataTime[0].count = "${dataTime.size}"
        }
        dataTime.clear()
    }

    private fun isfitCheck(ext5: String?): Boolean {
//        if(test && ext5 == "5") return true
//        if(xingao && ext5 == "1") return true
//        if(fangliang && ext5 == "2") return true
        if(xinDi && ext5 == "4") return true
        if(!xinDi && ext5 == "7") return true
        return false
    }

    private fun isFit(month: Int, time: String): Boolean {
        when(month) {
            1 -> {
                return time.startsWith("2023-1") || time.startsWith("2023-01")
            }
            2 -> {
                return time.startsWith("2023-2") || time.startsWith("2023-02")
            }
            3 -> {
                return time.startsWith("2023-3") || time.startsWith("2023-03")
            }
            4 -> {
                return time.startsWith("2023-4") || time.startsWith("2023-04")
            }
            5 -> {
                return time.startsWith("2023-5") || time.startsWith("2023-05")
            }
            6 -> {
                return time.startsWith("2023-6") || time.startsWith("2023-06")
            }
            7 -> {
                return time.startsWith("2023-7") || time.startsWith("2023-07")
            }
            8 -> {
                return time.startsWith("2022-8") || time.startsWith("2022-08")
            }
            9 -> {
                return time.startsWith("2022-9") || time.startsWith("2022-09")
            }
            10 -> {
                return time.startsWith("2022-10")
            }
            11 -> {
                return time.startsWith("2022-11")
            }
            12 -> {
                return time.startsWith("2022-12")
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
        xinDi = checked
    }

    fun setWenLiang(checked: Boolean) {
        test = checked
        if(test) {
            xinDi = false
            gengQiang = false
            xingao2 = false
            xingao = false
        }
    }
}