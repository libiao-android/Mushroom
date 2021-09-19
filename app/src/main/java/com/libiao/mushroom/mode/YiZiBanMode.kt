package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i
import kotlin.math.abs
import kotlin.math.min

class YiZiBanMode : BaseMode() {

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - 1 - Constant.PRE
        if(mDeviationValue >  0) {

            val zero = shares[mDeviationValue - 1]
            val one = shares[mDeviationValue + 0]

            if(!zhangTing(zero) && zhangTing(one) && one.minPrice == one.maxPrice) {
                i(TAG, "${one.brieflyInfo()}")
                mFitModeList.add(Pair(one.range, one))
            }

        }
    }

    override fun des(): String {
        return "一字板"
    }
}