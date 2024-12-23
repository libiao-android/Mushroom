package com.libiao.mushroom.mine.up50

import android.graphics.Color
import android.os.Environment
import com.airbnb.mvrx.MavericksViewModel
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.mine.fragment.BaseFragment
import com.libiao.mushroom.room.Up50ShareDatabase
import com.libiao.mushroom.room.Up50ShareInfo
import com.libiao.mushroom.utils.LogUtil
import com.libiao.mushroom.utils.baoLiuXiaoShu
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.Charset

class Up50ViewModel(initial: Up50State): MavericksViewModel<Up50State>(initial) {

    companion object {
        private const val TAG = "TestViewModel"
    }

    private val file_2023 = File(Environment.getExternalStorageDirectory(), "A_SharesInfo/2023")

    var localList = mutableListOf<Up50ShareInfo>()

    fun fetchInfo() {
        withState {
            LogUtil.i(TAG, "fetchInfo")
            val data = Up50ShareDatabase.getInstance()?.getUp50ShareDao()?.getShares()
            data?.forEach {
                val f = File(file_2023, it.code)
                if(f.exists()) {
                    val stream = FileInputStream(f)
                    val reader = BufferedReader(InputStreamReader(stream, Charset.defaultCharset()))
                    val lines = reader.readLines()

                    if(it.candleEntryList == null) {

                        var c = it.dayCount + 5
                        if(lines.size < c) c = lines.size
                        val records = lines.subList(lines.size - c, lines.size)
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
                            if(index == 10) {
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
                            }
                        }
                        it.candleEntryList = candleEntrys
                        it.barEntryList = barEntrys
                        it.colorsList = colorEntrys
                        it.values_5 = values_5
                        it.values_10 = values_10
                        it.values_20 = values_20

                    }
                }
            }
            data?.sortBy { it.dayCount }
            data?.also {
                localList = it
                setState {
                    copy(infoList = it)
                }
            }
        }
    }

    fun deleteItem(item: Up50ShareInfo) {
        withState {
            val list = mutableListOf<Up50ShareInfo>()
            list.addAll(it.infoList)
            list.remove(item)
            setState {
                it.copy(infoList = list)
            }
        }
    }

    fun updateNetWork(baseFragment: BaseFragment) {
        withState {s ->
            val temp = mutableListOf<Up50ShareInfo>()
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
                    val adP= baoLiuXiaoShu(share.yesterdayPrice * (1 + (share.rangeBegin - 1)/100)) //建议挂单价格
                    fangInfo.moreInfo = "${share.rangeBegin},  ${share.rangeMin},  ${share.rangeMax},  $liangBi, $adP"
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
            val list = mutableListOf<Up50ShareInfo>()
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