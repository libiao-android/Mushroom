package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i

class StrongStockMode2 : BaseMode() {

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - 2 - Constant.PRE
        if(mDeviationValue >  0) {

            val zero = shares[mDeviationValue - 1]
            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]

            var biLi = 0.99
            if(isChuang(zero.code)) {
                biLi = 0.98
            }

            if(zhangTing(zero)) {
                if(one.range < 5 || (!zhangTing(one) && two.beginPrice < two.nowPrice)) {
                    if(one.minPrice > zero.nowPrice * biLi && two.minPrice > zero.nowPrice * biLi) {
                        i(TAG, "${two.brieflyInfo()}")
                        mFitModeList.add(Pair(two.range, two))
                    }
                }
            }
        }
    }

    override fun des(): String {
        return "涨停后依然强势"
    }
}