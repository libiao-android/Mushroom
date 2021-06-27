package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i
import java.lang.StringBuilder
import kotlin.math.abs
import kotlin.math.min

class UpLineMode : BaseMode() {

    val map = HashMap<String, ArrayList<SharesRecordActivity.ShareInfo>>()

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - 3 - Constant.PRE

        var lianxu = false
        var key = ""

        for(mDeviationValue in 9 .. size - 22) {
            if(mDeviationValue >  0) {

                val day1 = shares[mDeviationValue + 0]
                //if(day1.time?.startsWith("2020") == true) continue
                if(day1.name?.contains("ST") == true) continue
                val day2 = shares[mDeviationValue + 1]
                val day3 = shares[mDeviationValue + 2]
                val day4 = shares[mDeviationValue + 3]
                val day5 = shares[mDeviationValue + 4]
                val day6 = shares[mDeviationValue + 5]
                val day7 = shares[mDeviationValue + 6]
                val day8 = shares[mDeviationValue + 7]
                val day9 = shares[mDeviationValue + 8]
                val day10 = shares[mDeviationValue + 9]
                val day11 = shares[mDeviationValue + 10]
                val day12 = shares[mDeviationValue + 11]
                val day13 = shares[mDeviationValue + 12]
                val day14 = shares[mDeviationValue + 13]
                val day15 = shares[mDeviationValue + 14]
                val day16 = shares[mDeviationValue + 15]
                val day17 = shares[mDeviationValue + 16]
                val day18 = shares[mDeviationValue + 17]
                val day19 = shares[mDeviationValue + 18]
                val day20 = shares[mDeviationValue + 19]
                val day21 = shares[mDeviationValue + 20]
                val day22 = shares[mDeviationValue + 21]
//                val day23 = shares[mDeviationValue + 22]
//                val day24 = shares[mDeviationValue + 23]
//                val day25 = shares[mDeviationValue + 24]
//                val day26 = shares[mDeviationValue + 25]
//                val day27 = shares[mDeviationValue + 26]
//                val day28 = shares[mDeviationValue + 27]
//                val day29 = shares[mDeviationValue + 28]
//                val day30 = shares[mDeviationValue + 29]
//                val day31 = shares[mDeviationValue + 30]
//                val day32 = shares[mDeviationValue + 31]
//                val day33 = shares[mDeviationValue + 32]
//                val day34 = shares[mDeviationValue + 33]
//                val day35 = shares[mDeviationValue + 34]
                if(upLine10(day1)
                    && upLine10(day2)
                    && upLine10(day3)
                    && upLine10(day4)
                    && upLine10(day5)
                    && upLine10(day6)
                    && upLine10(day7)
                    && upLine10(day8)
                    && upLine10(day9)
                    && upLine10(day10)
                    && upLine10(day11)
                    && upLine10(day12)
                    && upLine10(day13)
                    && upLine10(day14)
                    && upLine10(day15)
                    && upLine10(day16)
                    && upLine10(day17)
                    && upLine10(day18)
                    && upLine10(day19)
                    && upLine10(day20)
                    && upLine10(day21)
                    && upLine10(day22)
//                    && upLine10(day23)
//                    && upLine10(day24)
//                    && upLine10(day25)
//                    && upLine10(day26)
//                    && upLine10(day27)
//                    && upLine10(day28)
//                    && upLine10(day29)
//                    && upLine10(day30)
//                    && upLine10(day31)
//                    && upLine10(day32)
//                    && upLine10(day33)
//                    && upLine10(day34)
//                    && upLine10(day35)
                ) {
                    if(lianxu) {
                        val list = map.get(key)
                        list?.add(day22)
                    } else {
                        val r = (day22.nowPrice - day1.beginPrice) / day1.beginPrice * 100
                        if(r > 40 && r < 60) {

                            var yin = shangYinXian(day1)
                            yin += shangYinXian(day2)
                            yin += shangYinXian(day3)
                            yin += shangYinXian(day4)
                            yin += shangYinXian(day5)
                            yin += shangYinXian(day6)
                            yin += shangYinXian(day7)
                            yin += shangYinXian(day8)
                            yin += shangYinXian(day9)
                            yin += shangYinXian(day10)
                            yin += shangYinXian(day11)
                            yin += shangYinXian(day12)
                            yin += shangYinXian(day13)
                            yin += shangYinXian(day14)
                            yin += shangYinXian(day15)
                            yin += shangYinXian(day16)
                            yin += shangYinXian(day17)
                            yin += shangYinXian(day18)
                            yin += shangYinXian(day19)
                            yin += shangYinXian(day20)
                            yin += shangYinXian(day21)
                            yin += shangYinXian(day22)

                            if(yin / r < 0.5) {
                                //第一次
                                val list = ArrayList<SharesRecordActivity.ShareInfo>()
                                list.add(day22)
                                key = day22.brieflyInfo()
                                map.put(key, list)
                                val pian = (day22.nowPrice - day22.line_10) / day22.line_10 * 100
                                i(TAG, "涨幅：$r, ${day22.brieflyInfo()}, $yin, ${yin/r}, $pian")
                                mFitModeList.add(
                                    Pair(
                                        r,
                                        day22
                                    )
                                )
                            }
                        }

                    }
                    lianxu = true
                    //i(TAG, "${day35.brieflyInfo()}")
                    //mFitModeList.add(Pair(mFitModeList.size.toDouble(), day22))

                } else {
                    if(lianxu) {
                        //断了，卖出
                        val list = map.get(key)
                        list?.add(day22)
                    }
                    lianxu = false
                }
            }
        }

    }

    private fun shangYinXian(day: SharesRecordActivity.ShareInfo): Double {
        var end = 0.00
        if(day.beginPrice > day.nowPrice) {
            end = day.rangeBegin
        } else {
            end = day.range
        }
        //i(TAG, "${day.brieflyInfo()}, ${day.rangeMax - end}")
        return day.rangeMax - end
    }

    private fun upLine10(info: SharesRecordActivity.ShareInfo): Boolean {
        return info.nowPrice >= info.line_10
    }

    override fun des(): String {
        return "10日线攀升"
    }

    override fun content(): String {

//        val size = mFitModeList.size
//        i(TAG, "mFitModeList: $size")
//        mFitModeList.sortByDescending { it.first }
//
//        val sb = StringBuilder("")
//
//        for(i in 0 until size) {
//            sb.append("${mFitModeList[i].second?.code}" +
//                    ",  ${mFitModeList[i].second?.name}" +
//                    ",  ${String.format("%.2f",mFitModeList[i].first)}" +
//                    //",  ${mFitModeList[i].second?.postRange}" +
//                    ",  ${mFitModeList[i].second?.time}" +
//                    // ",  ${mFitModeList[i].second?.zongShiZhi}" +
//                    " \n")
//        }
//
//        return sb.toString()




        i(TAG, "${map.size}")
        val dayMap = HashMap<Int, ArrayList<String>>()
        map.forEach {
            //i(TAG, "${it.key}: ${it.value.size}")
            var v = dayMap.get(it.value.size)
            if(v == null) {
                v = ArrayList<String>()
                v.add(it.key)
                dayMap.put(it.value.size, v)
            } else {
                v.add(it.key)
            }
        }
        dayMap.forEach{
            i(TAG, "${it.key}: ${it.value.size}")
            i(TAG, "${it.value}")
        }
        return ""
    }
}