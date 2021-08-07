package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i
import kotlin.math.max

class ShangYinXian2FitMode : BaseMode() {

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - 4 - Constant.PRE
        if(mDeviationValue >=  0) {
            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]
            val four = shares[mDeviationValue + 3]

            val p = max(one.beginPrice, one.nowPrice)
            if(one.range > 0 && (one.maxPrice - p) / p * 100 > 5) {
                //if(two.range >5) {
                    i(TAG, "${two.brieflyInfo()}, ${three.range + four.range}")
                    mFitModeList.add(Pair(two.rangeBegin, two))
                //}
            }
        }
    }

    override fun des(): String {
        return "长上引线转一致"
    }
}