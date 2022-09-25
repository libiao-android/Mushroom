package com.libiao.mushroom.mine.ban

import android.graphics.Color
import android.os.Environment
import com.airbnb.mvrx.MavericksViewModel
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CandleEntry
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.mine.fragment.BaseFragment
import com.libiao.mushroom.room.ban.BanShareInfo
import com.libiao.mushroom.utils.LogUtil
import com.libiao.mushroom.utils.ShareParseUtil
import com.libiao.mushroom.utils.baoLiuXiaoShu
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.Charset

class BanViewModel(initial: BanState): MavericksViewModel<BanState>(initial) {

    companion object {
        private const val TAG = "BanViewModel"
    }

    private val file_2021 = File(Environment.getExternalStorageDirectory(), "A_SharesInfo/2021")

    var localList = mutableListOf<BanShareInfo>()

    fun fetchInfo(data: MutableList<BanShareInfo>?, type: Int = 0) {
        withState {
            LogUtil.i(TAG, "fetchInfo")
            val dataT = ArrayList<BanShareInfo>()
            data?.forEach {
                val f = File(file_2021, it.code)
                if(f.exists()) {
                    val stream = FileInputStream(f)
                    val reader = BufferedReader(InputStreamReader(stream, Charset.defaultCharset()))
                    val lines = reader.readLines()

                    if(it.dayCount > 3) {
                        val one = SharesRecordActivity.ShareInfo(lines[lines.size - it.dayCount + 1])
                        val two = SharesRecordActivity.ShareInfo(lines[lines.size - it.dayCount + 2])
                        val three = SharesRecordActivity.ShareInfo(lines[lines.size - it.dayCount + 3])
                        if(two.totalPrice > one.totalPrice && three.nowPrice > three.beginPrice) {
                            it.ext1 = "show"
                        }
                    } else {
                        it.ext1 = "show"
                    }

                    if(it.candleEntryList == null) {

                        var c = it.dayCount + 9
                        if(lines.size < c) c = lines.size

                        val records = lines.subList(lines.size - c, lines.size)
                        val candleEntrys = java.util.ArrayList<CandleEntry>()
                        val barEntrys = java.util.ArrayList<BarEntry>()
                        val colorEntrys = java.util.ArrayList<Int>()
                        var two: SharesRecordActivity.ShareInfo? = null
                        var three: SharesRecordActivity.ShareInfo? = null
                        var four: SharesRecordActivity.ShareInfo? = null
                        var five: SharesRecordActivity.ShareInfo? = null
                        var six: SharesRecordActivity.ShareInfo? = null
                        records.forEachIndexed { index, s ->
                            val item = SharesRecordActivity.ShareInfo(s)
                            if(item.beginPrice == 0.00) {
                                candleEntrys.add(CandleEntry((index).toFloat(), item.nowPrice.toFloat(), item.nowPrice.toFloat(), item.nowPrice.toFloat(), item.nowPrice.toFloat()))
                            } else {
                                candleEntrys.add(CandleEntry((index).toFloat(), item.maxPrice.toFloat(), item.minPrice.toFloat(), item.beginPrice.toFloat(), item.nowPrice.toFloat()))
                            }

                            val p = item.totalPrice.toFloat() / 100000000
                            barEntrys.add(BarEntry(index.toFloat(), p))
                            if(index == c - it.dayCount + it.ban) {
                                colorEntrys.add(Color.BLACK)
                            } else {
                                if(index == c - it.dayCount + it.ban + 1) {
                                    two = item
                                }
                                if(index == c - it.dayCount + it.ban + 2) {
                                    three = item
                                }
                                if(index == c - it.dayCount + it.ban + 3) {
                                    four = item
                                }
                                if(index == c - it.dayCount + it.ban + 4) {
                                    five = item
                                }
                                if(index == c - it.dayCount + it.ban + 5) {
                                    six = item
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
                        dataT.add(it)
                    }
                }
            }
            dataT.sortBy { it.dayCount }
            dataT.also {
                localList = it
                setState {
                    copy(infoList = it)
                }
            }
        }
    }

    private fun yin(two: SharesRecordActivity.ShareInfo): Boolean {
        return two.beginPrice > two.nowPrice
    }

    private fun yang(two: SharesRecordActivity.ShareInfo): Boolean {
        return two.nowPrice > two.beginPrice
    }

    fun deleteItem(item: BanShareInfo) {
        withState {
            val list = mutableListOf<BanShareInfo>()
            list.addAll(it.infoList)
            list.remove(item)
            setState {
                it.copy(infoList = list)
            }
        }
    }

    fun updateNetWork(baseFragment: BaseFragment) {
        withState {s ->
            val temp = mutableListOf<BanShareInfo>()
            temp.addAll(s.infoList)
            var count = 0
            temp.forEachIndexed { index, banShareInfo ->
                baseFragment.shiShiQuery(banShareInfo.code!!) {share ->
                    LogUtil.i(TAG, "share: ${share}")
                    count ++

                    val oneInfo = banShareInfo.copy()

                    val candleSize = oneInfo.candleEntryList?.size ?: 0
                    val lastShare = oneInfo.lastShareInfo

                    var liangBi = "0"
                    if(share.totalPrice > 0) {
                        liangBi = baoLiuXiaoShu(share.totalPrice / lastShare!!.totalPrice)
                    }

                    oneInfo.lastShareInfo = share

                    if(share.beginPrice == 0.00) {
                        oneInfo.candleEntryList?.add(CandleEntry(candleSize.toFloat(), share.nowPrice.toFloat(), share.nowPrice.toFloat(), share.nowPrice.toFloat(), share.nowPrice.toFloat()))
                    } else {
                        oneInfo.candleEntryList?.add(CandleEntry(candleSize.toFloat(), share.maxPrice.toFloat(), share.minPrice.toFloat(), share.beginPrice.toFloat(), share.nowPrice.toFloat()))
                    }

                    val adP= baoLiuXiaoShu(share.yesterdayPrice * (1 + (share.rangeBegin - 1)/100)) //建议挂单价格

                    oneInfo.moreInfo = "${share.rangeBegin},  ${share.rangeMin},  ${share.rangeMax},  $liangBi, $adP"
                    val p = share.totalPrice.toFloat() / 100000000
                    oneInfo.barEntryList?.add(BarEntry(candleSize.toFloat(), p))
                    oneInfo.colorsList?.add(Color.GRAY)
                    temp[index] = oneInfo

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
            val list = mutableListOf<BanShareInfo>()
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