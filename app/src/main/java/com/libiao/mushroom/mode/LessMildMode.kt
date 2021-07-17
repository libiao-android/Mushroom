package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i
import kotlin.math.max

class LessMildMode : BaseMode() {

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - 4 - Constant.PRE
        if(mDeviationValue > 5) {

            val zero5 = shares[mDeviationValue - 5]
            val zero4 = shares[mDeviationValue - 4]
            val zero3 = shares[mDeviationValue - 3]
            val zero2 = shares[mDeviationValue - 2]
            val zero = shares[mDeviationValue - 1]
            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]
            val four = shares[mDeviationValue + 3]

            if(two.totalPrice == 0.00) return
            if(zero.totalPrice * 1.5 > one.totalPrice && two.totalPrice * 2 < one.totalPrice) {
                if(one.range > 0 && one.totalPrice > 100000000 && two.range < 4) {
                    val avg = (zero5.totalPrice + zero4.totalPrice) / 2
                    if(two.totalPrice < avg) {

                    }
                    if(one.range > 3 && zero.range < 3 && two.range > -3) {
                        if(one.nowPrice > max(zero.beginPrice, zero.nowPrice)) {
                            if(two.maxPrice < one.maxPrice) {
                                val r = three.range + four.range
                                i(TAG, "${two.brieflyInfo()}, ${r}")
                                mFitModeList.add(Pair(r, two))
                            }
                        }
                    }

                }
            }
        }
    }

    override fun des(): String {
        return "极度缩量"
    }

}
//2021-7-13-2, 恒力石化, sh600346, 1803.42, 10.32

//2021-06-25, 马应龙, sh600993, 0.0, 11.0