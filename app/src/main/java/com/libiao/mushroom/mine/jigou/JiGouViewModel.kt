package com.libiao.mushroom.mine.jigou

import android.graphics.Color
import android.os.Environment
import com.airbnb.mvrx.MavericksViewModel
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.mine.fragment.BaseFragment
import com.libiao.mushroom.room.jigou.JiGouShareDatabase
import com.libiao.mushroom.room.jigou.JiGouShareInfo
import com.libiao.mushroom.utils.LogUtil
import com.libiao.mushroom.utils.baoLiuXiaoShu
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.Charset

class JiGouViewModel(initial: JiGouState): MavericksViewModel<JiGouState>(initial) {

    companion object {
        private const val TAG = "JiGouViewModel"
    }

    private val file_2023 = File(Environment.getExternalStorageDirectory(), "A_SharesInfo/2023")

    var localList = mutableListOf<JiGouShareInfo>()

    fun fetchInfo() {
        withState {
            LogUtil.i(TAG, "fetchInfo")
            val data = JiGouShareDatabase.getInstance()?.getJiGouShareDao()?.getShares()
            val dataT = ArrayList<JiGouShareInfo>()
            data?.forEach {
                val f = File(file_2023, it.code)
                if(f.exists()) {
                    val stream = FileInputStream(f)
                    val reader = BufferedReader(InputStreamReader(stream, Charset.defaultCharset()))
                    val lines = reader.readLines()

                    val info = it.jiGouInfo
                    var moreInfo = ""

                    val map = HashMap<String, String>()
                    val times = arrayListOf<String>()
                    info?.also { i ->
                        val vs = i.split(",")
                        vs.forEach { j ->
                            val v = j.split("#")
                            map[v[0].trim()] = v[1].trim()
                            times.add(v[0].trim())
                            moreInfo += "${v[1].trim()} "
                        }
                    }
                    it.moreInfo = moreInfo
                    var ind = 0
                    lines.forEachIndexed { index, s1 ->
                        val si = SharesRecordActivity.ShareInfo(s1)
                        if(si.time == times[0]) {
                            ind = index
                            return@forEachIndexed
                        }
                    }
                    if(it.candleEntryList == null) {
                        it.map = map
                        var a = ind - 10
                        if(a < 0) a = 0
                        var b = lines.size
                        val records = lines.subList(a, b)
                        val candleEntrys = java.util.ArrayList<CandleEntry>()
                        val barEntrys = java.util.ArrayList<BarEntry>()
                        val colorEntrys = java.util.ArrayList<Int>()
                        val values_5 = java.util.ArrayList<Entry>()
                        val values_10 = java.util.ArrayList<Entry>()
                        val values_20 = java.util.ArrayList<Entry>()
                        records.forEachIndexed { index, s ->
                            val item = SharesRecordActivity.ShareInfo(s)
                            if(item.beginPrice == 0.00) {
                                candleEntrys.add(CandleEntry((index).toFloat(), item.nowPrice.toFloat(), item.nowPrice.toFloat(), item.nowPrice.toFloat(), item.nowPrice.toFloat()))
                            } else {
                                candleEntrys.add(CandleEntry((index).toFloat(), item.maxPrice.toFloat(), item.minPrice.toFloat(), item.beginPrice.toFloat(), item.nowPrice.toFloat()))
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
                            if(times.contains(item.time)) {
                                colorEntrys.add(Color.BLACK)
                            } else {

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

                            if(index == records.size - 1) {
                                it.lastShareInfo = item
                                it.name = item.name
                            }
                        }
                        it.candleEntryList = candleEntrys
                        it.barEntryList = barEntrys
                        it.colorsList = colorEntrys
                        it.values_5 = values_5
                        it.values_10 = values_10
                        it.values_20 = values_20
                        dataT.add(it)
                    }
                }

            }
            dataT.sortByDescending { it.time }

//            dataT.sortedWith(Comparator<JiGouShareInfo> { o1, o2 ->
//                val time1 = o1.time!!.split("-")
//                val time2 = o2.time!!.split("-")
//                time1[2].toInt().compareTo(time2[2].toInt())
//
////                if(time1[0].toInt() == time2[0].toInt()) {
////                    if(time1[1].toInt() == time2[1].toInt()) {
////                        time1[2].toInt().compareTo(time2[2].toInt())
////                    } else {
////                        time1[1].toInt().compareTo(time2[1].toInt())
////                    }
////                } else {
////                    time1[0].toInt().compareTo(time2[0].toInt())
////                }
//            })
            dataT.also {
                localList = it
                setState {
                    copy(infoList = it)
                }
            }
        }
    }

    private fun isFit(month: Int, time: String): Boolean {
        when(month) {
            1 -> {
                return time.startsWith("2022-1") || time.startsWith("2022-01")
            }
            2 -> {
                return time.startsWith("2022-2") || time.startsWith("2022-02")
            }
            3 -> {
                return time.startsWith("2022-3") || time.startsWith("2022-03")
            }
            4 -> {
                return time.startsWith("2022-4") || time.startsWith("2022-04")
            }
            5 -> {
                return time.startsWith("2022-5") || time.startsWith("2022-05")
            }
            6 -> {
                return time.startsWith("2023-6") || time.startsWith("2023-06")
            }
            7 -> {
                return time.startsWith("2022-7") || time.startsWith("2022-07")
            }
            8 -> {
                return time.startsWith("2022-8") || time.startsWith("2022-08")
            }
        }
        return false
    }

    fun deleteItem(item: JiGouShareInfo) {
        withState {
            val list = mutableListOf<JiGouShareInfo>()
            list.addAll(it.infoList)
            list.remove(item)
            setState {
                it.copy(infoList = list)
            }
        }
    }

    fun sortMaxPrice(callback: (s: String) -> Unit) {
        withState {
            val list = mutableListOf<JiGouShareInfo>()
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
            val temp = mutableListOf<JiGouShareInfo>()
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
                    fangInfo.moreInfo = "${share.rangeBegin},  ${share.rangeMin},  ${share.rangeMax},  $liangBi"
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
            val list = mutableListOf<JiGouShareInfo>()
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
}