package com.libiao.mushroom.mode

import android.util.Log
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i
import kotlin.math.abs
import kotlin.math.min

class LeaderMode : BaseMode() {

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - 4
        if(mDeviationValue >  0) {
            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]
            val four = shares[mDeviationValue + 3]
            val range = one.range + two.range + three.range + four.range
            if(range >= 39) {
                i(TAG, "$range, ${four.brieflyInfo()}")
                mFitModeList.add(Pair(range, four))
            }
        }
    }

    override fun des(): String {
        return "龙头"
    }
}