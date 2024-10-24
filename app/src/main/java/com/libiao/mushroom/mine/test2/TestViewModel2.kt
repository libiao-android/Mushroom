package com.libiao.mushroom.mine.test2

import android.graphics.Color
import android.os.Environment
import com.airbnb.mvrx.MavericksViewModel
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.mine.fragment.BaseFragment
import com.libiao.mushroom.mine.test.TestState
import com.libiao.mushroom.room.test.TestShareDatabase2
import com.libiao.mushroom.room.TestShareInfo
import com.libiao.mushroom.utils.LogUtil
import com.libiao.mushroom.utils.baoLiuXiaoShu
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import kotlin.math.min

class TestViewModel2(initial: TestState): MavericksViewModel<TestState>(initial) {

    companion object {
        private const val TAG = "TestViewModel"
    }

    private val file_2023 = File(Environment.getExternalStorageDirectory(), "A_SharesInfo/2023")

    var localList = mutableListOf<TestShareInfo>()

    var isMorePrice = false
    var isLessPrice = false

    fun fetchInfo(month: Int) {
        withState {
            LogUtil.i(TAG, "fetchInfo")
            val data = TestShareDatabase2.getInstance()?.getTestShareDao()?.getShares()
            val dataT = ArrayList<TestShareInfo>()
            data?.forEach {
                if(it.dayCount > 0 && isFit(month, it.time!!)) {
                    val f = File(file_2023, it.code)
                    if(f.exists()) {
                        val stream = FileInputStream(f)
                        val reader = BufferedReader(InputStreamReader(stream, Charset.defaultCharset()))
                        val lines = reader.readLines()

                        if(it.candleEntryList == null) {

                            var a = it.startIndex - 20
                            if(a < 0) a = 0
                            var b = it.startIndex + 20
                            if(lines.size < b) b = lines.size
                            val records = lines.subList(a, b)
                            val candleEntrys = java.util.ArrayList<CandleEntry>()
                            val barEntrys = java.util.ArrayList<BarEntry>()
                            val colorEntrys = java.util.ArrayList<Int>()
                            val values_5 = java.util.ArrayList<Entry>()
                            val values_10 = java.util.ArrayList<Entry>()
                            val values_20 = java.util.ArrayList<Entry>()
                            var one: SharesRecordActivity.ShareInfo? = null
                            var two: SharesRecordActivity.ShareInfo? = null
                            var three: SharesRecordActivity.ShareInfo? = null
                            var four: SharesRecordActivity.ShareInfo? = null
                            var minPrice = 99999.00
                            var zhangFu = 0.00
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
                                if(index in 10..19) {
                                    val tempM = min(item.beginPrice, item.nowPrice)
                                    if(tempM != 0.00 && tempM < minPrice) {
                                        minPrice = tempM
                                    }
                                }
                                if(index == 20) {
                                    colorEntrys.add(Color.BLACK)
                                } else {
                                    if(index == 21) {
                                        one = item
                                    }
                                    if(index == 22) {
                                        two = item
                                    }
                                    if(index == 23) {
                                        three = item
                                        if(minPrice != 0.00) {
                                            zhangFu = (item.nowPrice - minPrice) / minPrice * 100
                                        }

                                    }
                                    if(index == 24) {
                                        four = item
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
//                            if(one != null && two != null) {
//                                if(two!!.minPrice > one!!.minPrice && two!!.maxPrice < one!!.maxPrice && one!!.totalPrice > two!!.totalPrice) {
//
//                                }
//                            }
                            it.moreInfo = String.format("%.1f", zhangFu)

//                            if(three != null && yin(two!!) && three!!.totalPrice > two!!.totalPrice && three!!.maxPrice < one!!.maxPrice && two!!.maxPrice < one!!.maxPrice) {
//                                val m = (two!!.minPrice + two!!.maxPrice) / 2
//                                val n = (three!!.minPrice + three!!.maxPrice) / 2
//                                val p = (n - m) / m * 100
//                                it.label1 = String.format("%.2f", p)
//
//                            }

                            dataT.add(it)

//                            if(three != null && yang(one!!) && yang(two!!) && yang(three!!)) {
//
//                            }

                            //dataT.add(it)

//                            if(three != null && two!!.maxPrice < one!!.maxPrice && zhangFu < 20) {
//                                val liangBi = baoLiuXiaoShu(three!!.totalPrice / two!!.totalPrice)
//                                val f = "${three!!.rangeBegin},  ${three!!.rangeMin},  ${three!!.rangeMax},  $liangBi"
//                                if(isMorePrice) {
//                                    if(three!!.totalPrice >= two!!.totalPrice * 0.95) {
//                                        dataT.add(it)
//                                        it.label1 = f
//                                    }
//
//                                } else if(isLessPrice) {
//                                    if(three!!.totalPrice < two!!.totalPrice * 0.95) {
//                                        dataT.add(it)
//                                        it.label1 = f
//                                    }
//                                } else {
//                                    dataT.add(it)
//                                }
//                            }
                        }
                    }
                }

            }
            dataT.sortByDescending { it.time!!.split("-")[2].toInt() }
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
                return time.startsWith("2023-7") || time.startsWith("2023-07")
            }
            8 -> {
                return time.startsWith("2024-8") || time.startsWith("2024-08")
            }
            9 -> {
                return time.startsWith("2024-9") || time.startsWith("2024-09")
            }
            10 -> {
                return time.startsWith("2024-10")
            }
            11 -> {
                return time.startsWith("2023-11")
            }
            12 -> {
                return time.startsWith("2023-12")
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

    private fun yin(two: SharesRecordActivity.ShareInfo): Boolean {
        return two.beginPrice > two.nowPrice
    }

    private fun yang(two: SharesRecordActivity.ShareInfo): Boolean {
        return two.nowPrice > two.beginPrice
    }
}