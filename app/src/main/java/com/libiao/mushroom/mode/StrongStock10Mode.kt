package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i

class StrongStock10Mode : BaseMode() {

    companion object {
        const val KEY = "StrongStock10Mode"
    }

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - 2 - Constant.PRE
        if(mDeviationValue >  0) {

            val zero = shares[mDeviationValue - 1]
            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]

            var biLi = 0.985
            if(isChuang(zero.code)) {
                biLi = 0.975
            }

            if(zhangTing(zero)) {
                if(one.range < 5 || (!zhangTing(one) && two.beginPrice < two.nowPrice)) {
                    if(one.minPrice > zero.nowPrice * biLi && two.minPrice > zero.nowPrice * biLi) {
                        if(two.totalPrice > zero.totalPrice) {
                            i(TAG, "${two.brieflyInfo()}")
                            mFitModeList.add(Pair(two.range, two))
                        }
                    }
                }
            }
        }
    }

    override fun des(): String {
        return "10"
    }
}