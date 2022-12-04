package com.libiao.mushroom.report

import android.graphics.Color
import android.os.Environment
import com.airbnb.mvrx.MavericksViewModel
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.room.report.ReportShareDatabase
import com.libiao.mushroom.room.report.ReportShareInfo
import com.libiao.mushroom.utils.LogUtil
import com.libiao.mushroom.utils.baoLiuXiaoShu
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.Charset

class ReportViewModel(initial: ReportState): MavericksViewModel<ReportState>(initial) {

    companion object {
        private const val TAG = "ReportViewModel"
    }

    private val file_2021 = File(Environment.getExternalStorageDirectory(), "A_SharesInfo/2021")

    var localList = mutableListOf<ReportShareInfo>()

    var onlyHeart: Boolean = false
    var conOne: Boolean = false


    fun fetchInfo(month: Int) {
        withState {
            val data = ReportShareDatabase.getInstance()?.getReportShareDao()?.getShares()
            LogUtil.i(TAG, "fetchInfo: ${data?.size}")
            val dataT = ArrayList<ReportShareInfo>()
            val dataTime = ArrayList<ReportShareInfo>()

            var time = ""
            data?.forEach {
                if(isFit(month, it.time!!) && !isOnlyHeart(it)) {
                    if(it.time == time) {

                    } else {
                        time = it.time!!
                        dataTime.sortByDescending { it.yinXianLength }
                        if(dataTime.size > 0) {
                            dataTime[0].ext1 = "${dataTime.size}"
                        }
                        dataT.addAll(dataTime)
                        dataTime.clear()
                    }
                    val f = File(file_2021, it.code)
                    if(f.exists()) {
                        val stream = FileInputStream(f)
                        val reader = BufferedReader(InputStreamReader(stream, Charset.defaultCharset()))
                        val lines = reader.readLines()
                        var startIndex = 0
                        var recordIndex = 0
                        lines.forEachIndexed{ind, line ->
                            if(it.label1 != null && line.startsWith(it.label1!!)) {
                                startIndex = ind
                            }
                            if(line.startsWith(it.time!!)) {
                                recordIndex = ind
                                return@forEachIndexed
                            }
                        }
                        val count = recordIndex - startIndex
                        var aaa = if(startIndex > 0) count+1 else 10
                        if(it.candleEntryList == null) {

                            var a: Int
                            if(startIndex > 0) {
                                a = startIndex - 1
                            } else {
                                a = recordIndex - 10
                            }

                            if(a < 0) a = 0
                            var b = recordIndex + 10
                            if(lines.size < b) b = lines.size
                            val records = lines.subList(a, b)
                            val candleEntrys = java.util.ArrayList<CandleEntry>()
                            val barEntrys = java.util.ArrayList<BarEntry>()
                            val colorEntrys = java.util.ArrayList<Int>()
                            val values_5 = java.util.ArrayList<Entry>()
                            val values_10 = java.util.ArrayList<Entry>()
                            val values_20 = java.util.ArrayList<Entry>()

                            var pre: SharesRecordActivity.ShareInfo? = null
                            var current: SharesRecordActivity.ShareInfo? = null
                            var post: SharesRecordActivity.ShareInfo? = null

                            var yin = 0
                            var yang = 0
                            records.forEachIndexed { index, s ->
                                val item = SharesRecordActivity.ShareInfo(s)
                                if(item.beginPrice == 0.00) {
                                    candleEntrys.add(CandleEntry((index).toFloat(), item.nowPrice.toFloat(), item.nowPrice.toFloat(), item.nowPrice.toFloat(), item.nowPrice.toFloat()))
                                } else {
                                    candleEntrys.add(CandleEntry((index).toFloat(), item.maxPrice.toFloat(), item.minPrice.toFloat(), item.beginPrice.toFloat(), item.nowPrice.toFloat()))
                                }

                                if (startIndex > 0 && index < count && index > 0) {
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

                                if(index == aaa - 1) {
                                    pre = item
                                }
                                if(index == aaa) {
                                    colorEntrys.add(Color.BLACK)
                                    it.lastShareInfo = item
                                    current = item
                                } else {
                                    if(index == aaa + 1) {
                                        post = item
                                    }
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
                            }
                            it.candleEntryList = candleEntrys
                            it.barEntryList = barEntrys
                            it.colorsList = colorEntrys
                            it.values_5 = values_5
                            it.values_10 = values_10
                            it.values_20 = values_20

                            if(startIndex > 0) {
                                it.label5 = "$count, $yang, $yin"
                            }

                            var liangBi = "0"
                            if(it.lastShareInfo!!.totalPrice > 0) {
                                liangBi = baoLiuXiaoShu(it.lastShareInfo!!.totalPrice / pre!!.totalPrice)
                            }
                            if(post != null && post!!.totalPrice > 0) {
                                it.moreInfo2 = "${post?.rangeBegin},  ${post?.rangeMin},  ${post?.rangeMax},  ${post?.range}"

                            }
                            it.moreInfo = "${it.lastShareInfo?.rangeBegin},  ${it.lastShareInfo?.rangeMin},  ${it.lastShareInfo?.rangeMax},  ${String.format("%.2f",it.lastShareInfo!!.totalPrice / 100000000)}亿,  ${liangBi}"

                            it.fenShiPath = File(Environment.getExternalStorageDirectory(), "A_SharesInfo/fenshi/${it.code}-${it.time}.jpg").absolutePath
                            it.fenShiPath2 = File(Environment.getExternalStorageDirectory(), "A_SharesInfo/fenshi/${it.code}-${it.time}-2.jpg").absolutePath
                            if(conOne) {
                                if (current!!.rangeMax <= 3) {
                                    dataTime.add(it)
                                }
                            } else {
                                dataTime.add(it)
                            }
                        }
                    }
                }

            }

            dataTime.sortByDescending { it.yinXianLength }
            if(dataTime.size > 0) {
                dataTime[0].ext1 = "${dataTime.size}"
            }
            dataT.addAll(dataTime)
            dataTime.clear()

            dataT.sortByDescending { it.time!!.split("-")[2].toInt() }
            dataT.also {
                localList = it
                setState {
                    copy(infoList = it)
                }
            }
        }
    }

    private fun isOnlyHeart(it: ReportShareInfo): Boolean {
        if(onlyHeart) {
            return it.collect == 0
        }
        return false
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
                return time.startsWith("2022-6") || time.startsWith("2022-06")
            }
            7 -> {
                return time.startsWith("2022-7") || time.startsWith("2022-07")
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

    fun deleteItem(item: ReportShareInfo) {
        withState {
            val list = mutableListOf<ReportShareInfo>()
            list.addAll(it.infoList)
            list.remove(item)
            setState {
                it.copy(infoList = list)
            }
        }
    }

    fun expand(time: String) {
        withState {
            val list = mutableListOf<ReportShareInfo>()
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

    fun shouCang(info: ReportShareInfo) {
        withState {
            val list = mutableListOf<ReportShareInfo>()
            list.addAll(it.infoList)
            list.forEachIndexed {index, item ->
                val temp = item.copy()
                if(temp.time == info.time && temp.code == info.code) {

                    if(temp.collect == 0) {
                        temp.collect = 1
                    } else {
                        temp.collect = 0
                    }
                    ReportShareDatabase.getInstance()?.getReportShareDao()?.update(temp)
                    list[index] = temp
                    return@forEachIndexed
                }
            }
            setState {
                it.copy(infoList = list)
            }
        }
    }

    fun setOnlySeeHeart(checked: Boolean) {
        onlyHeart = checked
    }

    fun setConditionOne(checked: Boolean) {
        conOne = checked
    }
}