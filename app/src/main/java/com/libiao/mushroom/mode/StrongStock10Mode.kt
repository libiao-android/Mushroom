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
        mDeviationValue = size - 1 - Constant.PRE
        if(mDeviationValue >  0) {

            val zero = shares[mDeviationValue - 1]
            val one = shares[mDeviationValue + 0]

            if (zhangTing(zero)) {
                if (one.beginPrice > one.nowPrice && one.minPrice > zero.nowPrice) {
                    i(TAG, "${one.brieflyInfo()}")
                    mFitModeList.add(Pair(one.range, one))
                }
            }
        }
    }

    override fun des(): String {
        return "10"
    }
}