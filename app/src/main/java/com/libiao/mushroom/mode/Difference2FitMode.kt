package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i

class Difference2FitMode : BaseMode() {

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - 4 - Constant.PRE
        if(mDeviationValue >  0) {
            val zero = shares[mDeviationValue - 1]
            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]
            val four = shares[mDeviationValue + 3]

            if(zhangTing(zero)) {
                if(one.totalPrice > zero.totalPrice && one.totalPrice > 300000000 && one.beginPrice > one.nowPrice) {
                    if(zhangTing(two)) {
                        val fangliang = one.totalPrice / zero.totalPrice
                        val suoliang = two.totalPrice / one.totalPrice

                        if(fangliang > 1.2) {
                            mFitModeList.add(Pair(two.range, two))
                            i(TAG, "${two.brieflyInfo()}, ${three.range + four.range}, $fangliang, $suoliang")

                        }



                        if(two.totalPrice < one.totalPrice) {
                            if(one.maxPrice > zero.nowPrice) {

                            }
                        }
                    }
                }
            }
        }
    }

    private fun zhangTing(info: SharesRecordActivity.ShareInfo): Boolean {
        var maxRange = 1.1
        if(info.code?.startsWith("sz300") == true) {
            maxRange = 1.2
        }
        var zhangTingPrice = info.yesterdayPrice * maxRange
        zhangTingPrice = String.format("%.2f",zhangTingPrice).toDouble()
        return info.nowPrice >= zhangTingPrice
    }

    override fun des(): String {
        return "放量后十字星"
    }
}