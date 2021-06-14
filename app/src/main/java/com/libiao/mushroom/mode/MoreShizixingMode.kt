package com.libiao.mushroom.mode

import android.util.Log
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i
import kotlin.math.abs
import kotlin.math.min

class MoreShizixingMode : BaseMode() {

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - 2 - Constant.PRE
        if(mDeviationValue >  0) {
            val zero = shares[mDeviationValue - 1]
            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]

            if(more(zero, one)) {
                if(isShiZiXing(two.beginPrice, two.nowPrice)) {
                    val a = abs(two.beginPrice - two.nowPrice) / min(two.beginPrice, two.nowPrice)

                    if(mDeviationValue + 2 < size) {
                        val post = shares[mDeviationValue + 2]
                        two.postRange = post.range
                    }
                    mFitModeList.add(Pair(a, two))

                    i(TAG, "$a, ${two.brieflyInfo()}, ${mFitModeList.size}")
                }
            }
        }
    }

    override fun des(): String {
        return "放量后十字星"
    }
}