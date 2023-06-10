package com.libiao.mushroom.report

import android.graphics.Color
import android.os.Environment
import com.airbnb.mvrx.MavericksViewModel
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.mine.fragment.BaseFragment
import com.libiao.mushroom.room.report.ReportShareDatabase
import com.libiao.mushroom.room.report.ReportShareInfo
import com.libiao.mushroom.thread.ThreadPoolUtil
import com.libiao.mushroom.utils.LogUtil
import com.libiao.mushroom.utils.ShareParseUtil
import com.libiao.mushroom.utils.baoLiuXiaoShu
import okhttp3.*
import java.io.*
import java.nio.charset.Charset

class ReportViewModel(initial: ReportState): MavericksViewModel<ReportState>(initial) {

    companion object {
        private const val TAG = "ReportViewModel"
    }

    private val file_2023 = File(Environment.getExternalStorageDirectory(), "A_SharesInfo/2023")

    var localList = mutableListOf<ReportShareInfo>()

    var maxLiang: Boolean = false
    var threeYang: Boolean = false

    var fanbao: Boolean = false

    private val client = OkHttpClient()


    fun fetchInfo(month: Int) {
        if(month == 6) {
            //ReportShareDatabase.getInstance()?.getReportShareDao()?.deleteByExt("3")
        }
        withState {
            val dataOrign =
                if(fanbao) {
                    ReportShareDatabase.getInstance()?.getReportShareDao()?.getSharesTest("3")
                } else {
                    ReportShareDatabase.getInstance()?.getReportShareDao()?.getSharesTest("2")
                }
            val data = dataOrign?.filter {
                true
            }
            LogUtil.i(TAG, "fetchInfo: ${data?.size}")
            val dataT = ArrayList<ReportShareInfo>()
            val dataTime = ArrayList<ReportShareInfo>()

            var time = ""
            var rangeCount = 0.00
            data?.forEach {
                if(isFit(month, it.time!!)) {
                    if(it.time == time) {

                    } else {
                        time = it.time!!
                        if(dataTime.size > 0) {
                            dataTime[0].ext1 = "${dataTime.size}"
                        }
                        dataT.addAll(dataTime)
                        dataTime.clear()
                    }
                    val f = File(file_2023, it.code)
                    if(f.exists()) {
                        val stream = FileInputStream(f)
                        val reader = BufferedReader(InputStreamReader(stream, Charset.defaultCharset()))
                        val lines = reader.readLines()
                        var startIndex = 0
                        var recordIndex = 0

                        lines.forEachIndexed{ind, line ->
                            if(it.time != null && line.startsWith(it.time!!)) {
                                recordIndex = ind
                                return@forEachIndexed
                            }
                        }
                        startIndex = recordIndex - it.dayCount

                        //LogUtil.i(TAG, "aaa: $aaa")
                        if(it.candleEntryList == null) {

                            var a: Int = startIndex - 1

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
                            var post1: SharesRecordActivity.ShareInfo? = null
                            var post2: SharesRecordActivity.ShareInfo? = null


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

                                if(index == it.dayCount) {
                                    pre = item
                                }
                                if(index == it.dayCount + 1) {
                                    colorEntrys.add(Color.BLACK)
                                    current = item
                                }else {
                                    if(index == it.dayCount + 2) {
                                        post1 = item
                                    }
                                    if(index == it.dayCount + 3) {
                                        post2 = item
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
                                    it.moreInfo3 = "${item.rangeBegin}, ${item.rangeMin}, ${item.rangeMax}, ${item.range}"
                                }
                            }
                            it.candleEntryList = candleEntrys
                            it.barEntryList = barEntrys
                            it.colorsList = colorEntrys
                            it.values_5 = values_5
                            it.values_10 = values_10
                            it.values_20 = values_20


                            val liangBi = baoLiuXiaoShu(current!!.totalPrice / pre!!.totalPrice)

                            it.moreInfo = "${current!!.rangeBegin}, ${current!!.rangeMin}, ${current!!.rangeMax}, ${current!!.range}, $liangBi"

                            if(fanbao) {
                                dataTime.add(it)
                            } else {
                                if(maxLiang) {
                                    dataTime.add(it)
                                } else {
                                    if(threeYang) {
                                        val max = Math.max(pre!!.beginPrice, pre!!.nowPrice)
                                        if(current!!.nowPrice < max) {
                                            dataTime.add(it)
                                        }
                                    } else {
                                        if(post1 != null) {
                                            if(post1!!.nowPrice > current!!.nowPrice && post1!!.maxPrice > current!!.maxPrice) {
                                                dataTime.add(it)
                                                it.moreInfo2 = "${post1!!.range}"
                                            } else {
                                                if(post2 != null) {
                                                    if(post2!!.nowPrice > current!!.nowPrice && post2!!.maxPrice > current!!.maxPrice) {
                                                        dataTime.add(it)
                                                        it.moreInfo2 = "${post1!!.range}, ${post2!!.range}"
                                                    }
                                                }
                                            }
                                        } else {
                                            dataTime.add(it)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
            LogUtil.i(TAG, "rangeCount: $rangeCount")
            //dataTime.sortByDescending { it.yinXianLength }
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

    private fun isOnlyDelete(it: ReportShareInfo): Boolean {
        if(maxLiang) {
            return it.delete == 1
        }
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

    fun setOnlySeeDelete(checked: Boolean) {
        maxLiang = checked
    }

    fun setConditionOne(checked: Boolean) {
        threeYang = checked
    }

    fun online() {
        withState {s ->
            val temp = mutableListOf<ReportShareInfo>()
            temp.addAll(s.infoList)
            var count = 0
            temp.forEachIndexed { index, banShareInfo ->
                shiShiQuery(banShareInfo.code!!) {share ->
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

    fun shiShiQuery(code: String, result: (info: SharesRecordActivity.ShareInfo) -> Unit) {
        //http://qt.gtimg.cn/q=s_sz000858
        val request = Request.Builder()
            .url("https://qt.gtimg.cn/q=$code")
            .build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                LogUtil.i(BaseFragment.TAG, "onFailure: ${e}, $code")
            }

            override fun onResponse(call: Call?, response: Response?) {
                val value = response?.body()?.string()
                //Log.i("libiao_A", "response: ${value}")
                try {
                    if (value != null) {
                        //LogUtil.i(TAG, "response: ${value}")
                        val strs = value.split("~")
                        if(strs.size > 45) {
                            val share = ShareParseUtil.parseFromNetwork(code, strs)
                            LogUtil.i(BaseFragment.TAG, "share: $share")
                            result(share)
                        }
                    } else {
                        LogUtil.i(
                            BaseFragment.TAG,
                            "exception value: $value, $code"
                        )
                    }
                } catch (e: Exception) {
                    LogUtil.i(
                        BaseFragment.TAG,
                        "parseSharesInfo exception: ${e.message}, $code, $value"
                    )
                } finally {
                    ThreadPoolUtil.executeUI(Runnable {
                        //shiShiRefresh()
                    })
                }
            }
        })
    }

    fun setFanbaoAgain(checked: Boolean) {
        fanbao = checked
    }
}