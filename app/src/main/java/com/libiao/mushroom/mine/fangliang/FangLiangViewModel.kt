package com.libiao.mushroom.mine.fangliang

import android.graphics.Color
import android.os.Environment
import com.airbnb.mvrx.MavericksViewModel
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CandleEntry
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.room.FangLiangShareDatabase
import com.libiao.mushroom.utils.LogUtil
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.Charset

class FangLiangViewModel(initial: FangLiangState): MavericksViewModel<FangLiangState>(initial) {

    companion object {
        private const val TAG = "FangLiangViewModel"
    }

    private val file_2021 = File(Environment.getExternalStorageDirectory(), "A_SharesInfo/2021")

    fun fetchInfo() {
        withState {
            LogUtil.i(TAG, "fetchInfo")
            val data = FangLiangShareDatabase.getInstance()?.getFangLiangShareDao()?.getFangLiangShares()
            data?.forEach {
                val f = File(file_2021, it.code)
                if(f.exists()) {
                    val stream = FileInputStream(f)
                    val reader = BufferedReader(InputStreamReader(stream, Charset.defaultCharset()))
                    val lines = reader.readLines()

                    if(it.candleEntryList == null) {

                        val records = lines.subList(lines.size - it.dayCount, lines.size)
                        val candleEntrys = java.util.ArrayList<CandleEntry>()
                        val barEntrys = java.util.ArrayList<BarEntry>()
                        val colorEntrys = java.util.ArrayList<Int>()
                        records.forEachIndexed { index, s ->
                            val item = SharesRecordActivity.ShareInfo(s)
                            if(item.beginPrice == 0.00) {
                                candleEntrys.add(CandleEntry((index).toFloat(), item.nowPrice.toFloat(), item.nowPrice.toFloat(), item.nowPrice.toFloat(), item.nowPrice.toFloat()))
                            } else {
                                candleEntrys.add(CandleEntry((index).toFloat(), item.maxPrice.toFloat(), item.minPrice.toFloat(), item.beginPrice.toFloat(), item.nowPrice.toFloat()))
                            }

                            val p = item.totalPrice.toFloat() / 100000000
                            barEntrys.add(BarEntry(index.toFloat(), p))
                            if(item.beginPrice > item.nowPrice) {
                                colorEntrys.add(Color.GREEN)
                            } else if(item.beginPrice < item.nowPrice) {
                                colorEntrys.add(Color.RED)
                            } else {
                                if(item.range >= 0) {
                                    colorEntrys.add(Color.RED)
                                } else {
                                    colorEntrys.add(Color.GREEN)
                                }
                            }

                            if(index == records.size - 1) {
                                it.lastShareInfo = item
                            }
                        }
                        it.candleEntryList = candleEntrys
                        it.barEntryList = barEntrys
                        it.colorsList = colorEntrys
                    }
                }
            }
            data?.sortBy { it.dayCount }
            data?.also {
                setState {
                    copy(infoList = it)
                }
            }
        }
    }
}