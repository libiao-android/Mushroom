package com.libiao.mushroom.mine.test

import android.graphics.Color
import android.os.Environment
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

    var isMorePrice = false
    var isLessPrice = false

    var xingao = false
    var xingao2 = false
    var fangliang = false

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

    fun fetchInfo(month: Int) {
        if(xingao2) {
            fetchXinGao2Info()
            return
        }
        withState {

            if(month == 6) {
                TestShareDatabase.getInstance()?.getTestShareDao()?.deleteTest("5")
            }

            val data = TestShareDatabase.getInstance()?.getTestShareDao()?.getShares()

            LogUtil.i(TAG, "fetchInfo: ${data?.size}")
            val dataT = ArrayList<TestShareInfo>()

            data?.forEach {
                if(it.dayCount >= 0 && isFit(month, it.time!!) && isfitCheck(it.ext5)) {
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

                        val jianGe = it.ext4

                        if(it.candleEntryList == null) {

                            var blackIndex = 120
                            var a = updateIndex - 120
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
                            var secondPrice = 0.00 // 之前最高价
                            var secondIndex = 0
                            var secondItem:  SharesRecordActivity.ShareInfo? = null

                            var minPrice = 9999.99 // 之前最低价
                            var minIndex = 0

                            var secondTotal = 0.00 // 之前最大量能
                            var secondTotalIndex = 0

                            var moreXinGao = false // 新高后新高
                            var moreXinGaoIndex = -1
                            var moreXinGaoItem: SharesRecordActivity.ShareInfo? = null

                            var moreLiangXinGao = false // 量能新高后新高
                            var moreLiangXinGaoIndex = -1
                            var moreLiangXinGaoItem: SharesRecordActivity.ShareInfo? = null


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

                                if(index in 1..119) {
                                    if(item.maxPrice > secondPrice) {
                                        secondPrice = item.maxPrice
                                        secondItem = item
                                        secondIndex = index
                                    }

                                    if(item.minPrice < minPrice) {
                                        minPrice = item.minPrice
                                        minIndex = index
                                    }
                                    if(item.totalPrice > secondTotal) {
                                        secondTotal = item.totalPrice
                                        secondTotalIndex = index
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

                                if(index == 119) {
                                    itemBlack2 = item
                                }


                                if(index > 120) {
                                    if(item.maxPrice > itemBlack!!.maxPrice && !moreXinGao && item.nowPrice > item.beginPrice) {
                                        moreXinGao = true
                                        moreXinGaoIndex = index
                                        moreXinGaoItem = item
                                    }

                                    if(item.totalPrice > itemBlack!!.totalPrice && !moreLiangXinGao && item.nowPrice > item.beginPrice) {
                                        moreLiangXinGao = true
                                        moreLiangXinGaoIndex = index
                                        moreLiangXinGaoItem = item
                                    }
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
                                } else if((index == moreXinGaoIndex && xingao) || (index == moreLiangXinGaoIndex && fangliang)){
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

                            if(test) {
                                //val aa = "${itemBlack!!.range}, ${huanSuanYi(itemBlack!!.totalPrice)}"

//                                if(itemBlackPost != null && itemBlackPost!!.rangeBegin > 3 && !ShareParseUtil.zhangTing(itemBlack!!)) {
//                                    it.moreInfo = "${it.label1}, ${itemBlackPost!!.rangeBegin}"
//                                    dataT.add(it)
//                                }

//                                if(itemBlackPost != null && itemBlackPost!!.nowPrice < itemBlackPost!!.beginPrice
//                                    && itemBlackPost2 != null &&  itemBlackPost2!!.beginPrice < itemBlackPost2!!.nowPrice
//                                    && itemBlackPost!!.totalPrice < itemBlack!!.totalPrice) {
//                                    it.moreInfo = "${it.label1}, ${itemBlackPost!!.range}, ${itemBlackPost2!!.range}"
//                                    dataT.add(it)
//                                }

//                                if(itemBlack != null && itemBlackPre != null) {
//                                    if(itemBlack!!.maxPrice > itemBlackPre!!.maxPrice) {
//                                        val temp = max(itemBlack!!.rangeBegin, itemBlack!!.range)
//                                        if(itemBlack!!.rangeMax - temp > (temp - itemBlack!!.rangeMin) * 2) {
//                                            it.moreInfo = "${it.label1}, ${itemBlack?.range}, ${itemBlackPost?.range}, ${itemBlackPost2?.range}"
//                                            dataT.add(it)
//                                        }
//
//                                    }
//                                }
                                val a = itemBlackPre2?.range ?: 0.00
                                val b = itemBlackPre?.range ?: 0.00
                                val c = itemBlack?.range ?: 0.00
                                it.moreInfo = "${itemBlackPre2?.range}, ${itemBlackPre?.range}, ${itemBlack?.range}, ${itemBlackPost?.range}, ${itemBlackPost2?.range}"
                                dataT.add(it)
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

                            if(fangliang) {
                                val aa = "${itemBlack!!.range}, ${itemBlack!!.rangeBegin},  ${itemBlack!!.rangeMin},  ${itemBlack!!.rangeMax},  ${huanSuanYi(itemBlack!!.totalPrice)}"
                                val bb = "${baoLiuXiaoShu(itemBlack!!.totalPrice / secondTotal)}"
                                if(isMorePrice) {
                                    if(itemBlackPost != null) {
                                        if(itemBlackPost!!.totalPrice > itemBlack!!.totalPrice && itemBlackPost!!.maxPrice > itemBlack!!.maxPrice) {
                                            if(itemBlackPost!!.range > 0 && itemBlackPost!!.nowPrice > itemBlackPost!!.beginPrice) {
                                                val cc = "${itemBlackPost!!.range}, ${itemBlackPost!!.rangeBegin},  ${itemBlackPost!!.rangeMin},  ${itemBlackPost!!.rangeMax},  ${huanSuanYi(itemBlackPost!!.totalPrice)}"
                                                it.moreInfo = "$aa\n$cc\n$bb"

                                                if(itemBlackPost2 != null && itemBlackPost3 != null) {
                                                    val d = itemBlackPost2!!.range - itemBlackPost2!!.rangeBegin
                                                    val e = itemBlackPost3!!.range
                                                    val f = d + e
                                                    it.moreInfo = "$aa\n$cc\n$bb,    ${baoLiuXiaoShu(d)}, ${baoLiuXiaoShu(e)}, ${baoLiuXiaoShu(f)}"
                                                }

                                                dataT.add(it)
                                            }

                                        }
                                    }
                                } else {
//                                    if(moreLiangXinGao) {
//
//                                    }
                                    it.moreInfo = "$aa\n$bb"
                                    dataT.add(it)
                                }
                            }

                            if (xingao) {
                                if (itemBlack != null && itemBlack!!.range > 0 && itemBlack!!.nowPrice > itemBlack!!.beginPrice) {

                                    val a =
                                        "${itemBlack!!.range}, ${itemBlack!!.rangeBegin},  ${itemBlack!!.rangeMin},  ${itemBlack!!.rangeMax},  ${huanSuanYi(
                                            itemBlack!!.totalPrice
                                        )}"
                                    val b =
                                        "$secondIndex, ${baoLiuXiaoShu((itemBlack!!.maxPrice - secondItem!!.maxPrice) / secondItem!!.maxPrice * 100)}"

                                    val c = "$secondTotalIndex, ${baoLiuXiaoShu((itemBlack!!.totalPrice) / secondTotal)}"
                                    var d = ""
                                    if(itemBlackPost != null) {
                                        d = "${itemBlackPost!!.range}, ${itemBlackPost!!.rangeBegin},  ${itemBlackPost!!.rangeMin},  ${itemBlackPost!!.rangeMax}"
                                    }
                                    it.moreInfo = "$a\n$b   |   $c\n$d"

                                    val isZhaBan = itemBlack!!.range < itemBlack!!.rangeMax && ShareParseUtil.maxZhangTing(itemBlack!!)

                                    val diDianMid = secondIndex < minIndex
                                    val liangNengJianGe = secondTotalIndex < 100

                                    val shangYinXian = itemBlack!!.rangeMax - itemBlack!!.range > itemBlack!!.range - itemBlack!!.rangeBegin

                                    val zhangTing = ShareParseUtil.zhangTing(itemBlack!!) //涨停会带来很多打板客，这些都是短线资金，带来的量能放大，不是逻辑驱动

                                    val zhangTingPre = ShareParseUtil.zhangTing(itemBlackPre!!)

                                    val jiaGeJianGe = jianGe?.toInt() ?: 0 > 20

                                    if(maxPrice) {
                                        if (itemBlack!!.totalPrice > secondTotal && itemBlackPost?.totalPrice ?: 0.00 > itemBlack!!.totalPrice) {
                                            if(
                                                !isZhaBan
                                                && !shangYinXian
                                              //  && !diDianMid
                                              //  && !zhangTingPre
                                             //   && !zhangTing
                                                //&& liangNengJianGe
                                                //&& jiaGeJianGe
                                                && itemBlack!!.totalPrice > 300000000
                                            ) {
                                                dataT.add(it)
                                            } else {
//                                                if (moreXinGaoIndex > 122
//                                                    //&& moreXinGaoItem!!.totalPrice > itemBlack!!.totalPrice
//                                                    //&& itemBlack!!.totalPrice > 100000000 && moreXinGaoIndex < 130
//                                                ) {
//                                                    it.moreInfo = "$a\n$b\n$c\n新高"
//                                                    dataT.add(it)
//                                                }
                                            }
                                        }
                                    } else if(xinGaoAgain) {
                                        if(moreXinGao && moreXinGaoIndex > 125) {
                                            dataT.add(it)
                                        }
                                    } else {
                                        dataT.add(it)
                                    }
                                }
                            }
                        }
                    }
                }

            }
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

    private fun fetchXinGao2Info() {
        withState {

            val data = TestShareDatabase2.getInstance()?.getTestShareDao()?.getShares()

            LogUtil.i(TAG, "fetchInfo: ${data?.size}")
            val dataT = ArrayList<TestShareInfo>()

            data?.forEach {
                if(it.dayCount > 0) {
                    val f = File(file_2023, it.code)
                    if(f.exists()) {
                        val stream = FileInputStream(f)
                        val reader = BufferedReader(InputStreamReader(stream, Charset.defaultCharset()))
                        val lines = reader.readLines()

                        if(it.candleEntryList == null) {

                            var a = lines.size - 120 - it.dayCount
                            if(a < 0) a = 0
                            var b = lines.size
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
                            var secondPrice = 0.00 // 之前最高价
                            var secondIndex = 0
                            var secondItem:  SharesRecordActivity.ShareInfo? = null

                            var minPrice = 9999.99 // 之前最低价
                            var minIndex = 0

                            var secondTotal = 0.00 // 之前最大量能
                            var secondTotalIndex = 0
                            var maxLiang = false
                            var maxLiangIndex = -1

                            var moreXinGao = false // 新高后新高
                            var moreXinGaoIndex = -1
                            var moreXinGaoItem: SharesRecordActivity.ShareInfo? = null

                            var moreLiangXinGao = false // 量能新高后新高
                            var moreLiangXinGaoIndex = -1
                            var moreLiangXinGaoItem: SharesRecordActivity.ShareInfo? = null


                            var zuiDaLiang = 0.00
                            var zuiDaLiangCount = 0
                            var zuiDaLiangIndex = -1


                            var shuang = false


                            records.forEachIndexed { index, s ->
                                //if(index == 0) return@forEachIndexed
                                val item = SharesRecordActivity.ShareInfo(s)

                                if(dayCheck) {
                                    if(index > 60) {
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

                                if(index in 1..119) {
                                    if(item.maxPrice > secondPrice) {
                                        secondPrice = item.maxPrice
                                        secondItem = item
                                        secondIndex = index
                                    }

                                    if(item.minPrice < minPrice) {
                                        minPrice = item.minPrice
                                        minIndex = index
                                    }
                                    if(item.totalPrice > secondTotal) {
                                        secondTotal = item.totalPrice
                                        secondTotalIndex = index
                                    }
                                }

                                var line5 = item.line_5
                                if(line5 == 0.00) { line5 = item.beginPrice }

                                var line10 = item.line_10
                                if(line10 == 0.00) { line10 = item.beginPrice }

                                var line20 = item.line_20
                                if(line20 == 0.00) { line20 = item.beginPrice }

                                if(dayCheck) {
                                    if(index > 60) {
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
                                if(dayCheck) {
                                    if(index > 60) {
                                        barEntrys.add(BarEntry(index.toFloat(), p))
                                    }
                                } else {
                                    barEntrys.add(BarEntry(index.toFloat(), p))
                                }

                                if(index == 119) {
                                    itemBlack2 = item
                                }


                                if(index > 120) {
                                    if(!maxLiang && item.totalPrice > itemBlack!!.totalPrice) {
                                        maxLiang = true
                                        maxLiangIndex = index
                                    }

                                    if(item.totalPrice > zuiDaLiang) {
                                        zuiDaLiang = item.totalPrice
                                        zuiDaLiangCount++
                                        if(index - zuiDaLiangIndex == 1) {
                                            shuang = true
                                        }
                                        zuiDaLiangIndex = index
                                    }
                                }

                                if(index == 119) {
                                    itemBlackPre = item
                                }

                                if(index == 121) {
                                    itemBlackPost = item
                                }

                                if(index == 120) {
                                    colorEntrys.add(Color.BLACK)
                                    itemBlack = item
                                    zuiDaLiang = item.totalPrice
                                } else if((index == maxLiangIndex && index != 120) || zuiDaLiangIndex == index){
                                    if(index == 121) {
                                        shuang = true
                                    }
                                    colorEntrys.add(Color.GRAY)
                                } else {
                                    val green = "#28FF28"
                                    val red = "#FF0000"

                                    if(dayCheck) {
                                        if(index > 60) {
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

                            val aa =
                                "${itemBlack!!.range}, ${itemBlack!!.rangeBegin},  ${itemBlack!!.rangeMin},  ${itemBlack!!.rangeMax},  ${huanSuanYi(itemBlack!!.totalPrice)}"

                            it.zuiDaLiangCount = zuiDaLiangCount
                            val bb = "${it.dayCount}, ${it.maxCount}, ${zuiDaLiangCount}"
                            it.moreInfo = "${aa}\n${bb}"

                            val isHexin = it.lastShareInfo?.totalPrice ?: 0.00 > zuiDaLiang / 3
                            if(!shuang) return@forEach
                            if(gao2LiangChecked) {
                                if(itemBlack!!.totalPrice > gao2Liang * 100000000.00) {
                                    if(gao2XinLiang) {
                                        if(maxLiang) {
                                            if(heXin) {
                                                if(isHexin) {
                                                    dataT.add(it)
                                                }
                                            } else {
                                                dataT.add(it)
                                            }
                                        }
                                    } else {
                                        if(heXin) {
                                            if(isHexin) {
                                                dataT.add(it)
                                            }
                                        } else {
                                            dataT.add(it)
                                        }
                                    }
                                }
                            } else {
                                if(gao2XinLiang) {
                                    if(maxLiang) {
                                        if(heXin) {
                                            if(isHexin) {
                                                dataT.add(it)
                                            }
                                        } else {
                                            dataT.add(it)
                                        }
                                    }
                                } else {
                                    if(heXin) {
                                        if(isHexin) {
                                            dataT.add(it)
                                        }
                                    } else {
                                        dataT.add(it)
                                    }
                                }
                            }

                        }
                    }
                }
            }

            if(zhengXu) {
                dataT.sortBy { getPaiXuType(it) }
            } else {
                dataT.sortByDescending { getPaiXuType(it) }
            }

            oneDayCount2(dataT)

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
        if(test && ext5 == "5") return true
        if(xingao && ext5 == "1") return true
        if(fangliang && ext5 == "2") return true
        if(xinDi && ext5 == "4") return true
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

    fun setXinGaoChecked(checked: Boolean) {
        xingao = checked
        if(xingao) {
            test = false
            xinDi = false
            xingao2 = false
            fangliang = false
        }
    }

    fun setFangLiangChecked(checked: Boolean) {
        fangliang = checked
        if(fangliang) {
            test = false
            xinDi = false
            xingao2 = false
            xingao = false
        }
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
        if(xinDi) {
            test = false
            fangliang = false
            xingao2 = false
            xingao = false
        }
    }

    fun setWenLiang(checked: Boolean) {
        test = checked
        if(test) {
            xinDi = false
            fangliang = false
            xingao2 = false
            xingao = false
        }
    }
}