package com.libiao.mushroom.mode

import android.content.Context
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i

class UpLine5Mode : BaseMode() {

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - 7 - Constant.PRE

        if(mDeviationValue >  0) {
            val zero = shares[mDeviationValue - 1]
            if(upLine5(zero)) return
            val day1 = shares[mDeviationValue + 0]
            if(day1.name?.contains("ST") == true) return
            val day2 = shares[mDeviationValue + 1]
            val day3 = shares[mDeviationValue + 2]
            val day4 = shares[mDeviationValue + 3]
            val day5 = shares[mDeviationValue + 4]
            val day6 = shares[mDeviationValue + 5]
            val day7 = shares[mDeviationValue + 6]
            if(upLine5(day1)
                &&upLine5(day2)
                && upLine5(day3)
                && upLine5(day4)
                && upLine5(day5)
                && upLine5(day6)
                && upLine5(day7)
            ) {
                val r = (day7.nowPrice - day1.beginPrice) / day1.beginPrice * 100
                if(r > 15) {

                    i(TAG, "涨幅：$r, ${day7.brieflyInfo()}")
                    mFitModeList.add(
                        Pair(
                            r,
                            day7
                        )
                    )

                }

            }
        }


    }

    override fun shouldAnalysis(context: Context): Boolean {
        return true
    }

    private fun upLine5(info: SharesRecordActivity.ShareInfo): Boolean {
        return info.nowPrice >= info.line_5
    }

    override fun des(): String {
        return "5日线攀升"
    }
}