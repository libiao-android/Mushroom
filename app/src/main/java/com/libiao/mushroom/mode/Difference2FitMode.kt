package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i

class Difference2FitMode : BaseMode() {

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - 2 - Constant.PRE
        if(mDeviationValue >  0) {
            val zero = shares[mDeviationValue - 1]
            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]
            //val three = shares[mDeviationValue + 2]
            //val four = shares[mDeviationValue + 3]

            if(zhangTing(zero)) {
                if(one.beginPrice > one.nowPrice) {
                    if(zhangTing(two)) {
                        i(TAG, "${two.brieflyInfo()}")
                        mFitModeList.add(Pair(two.rangeBegin, two))
                    }
                }
            }
        }
    }

    override fun des(): String {
        return "分歧转一致"
    }
}