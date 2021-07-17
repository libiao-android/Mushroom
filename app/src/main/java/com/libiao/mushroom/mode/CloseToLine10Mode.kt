package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil

class CloseToLine10Mode : BaseMode() {

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - 20 - Constant.PRE

        if(mDeviationValue >= 0) {
            val day1 = shares[mDeviationValue + 0]
            if(day1.name?.contains("ST") == true) return
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
            if(day1.beginPrice > 0) {
                var r = (day20.nowPrice - day1.beginPrice) / day1.beginPrice * 100
//                var r = 0.00
//                r += day1.range
//                r += day2.range
//                r += day3.range
//                r += day4.range
//                r += day5.range
//                r += day6.range
//                r += day7.range
//                r += day8.range
//                r += day9.range
//                r += day10.range
//                r += day11.range
//                r += day12.range
//                r += day13.range
//                r += day14.range
//                r += day15.range
//                r += day16.range
//                r += day17.range
//                r += day18.range
//                r += day19.range
//                r += day20.range

                if(r > 50) {
                    if(upLine10(day11)
                        && upLine10(day12)
                        && upLine10(day13)
                        && upLine10(day14)
                        && upLine10(day15)
                        && upLine10(day16)
                        && upLine10(day17)
                        && upLine10(day18)
                        && upLine10(day19)
                        && upLine10(day20)
                    ) {
                        val closeToR = (day20.nowPrice - day20.line_10) / day20.line_10 * 100
                        if(closeToR < 10) {
                            LogUtil.i(TAG, "${day20.brieflyInfo()}, ${r}, $closeToR")
                            mFitModeList.add(Pair(closeToR, day20))
                        }
                    }
                }
            }
        }

    }

    private fun upLine10(info: SharesRecordActivity.ShareInfo): Boolean {
        return info.nowPrice >= info.line_10
    }

    override fun des(): String {
        return "靠近10日线"
    }
}