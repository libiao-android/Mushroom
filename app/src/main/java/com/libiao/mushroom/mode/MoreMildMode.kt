package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i

class MoreMildMode : BaseMode() {

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - 3 - Constant.PRE
        if(mDeviationValue >  0) {

            val zero = shares[mDeviationValue - 1]
            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]
            if(one.totalCount == 0) return
            if(two.totalCount == 0) return
            if(three.totalCount == 0) return
            if(zero.nowPrice >= zero.beginPrice) return
            if(three.totalPrice < 100000000) return
            if(three.maxPrice - three.nowPrice > three.nowPrice - three.beginPrice) return

            val a = one.totalCount / two.totalCount.toDouble()
            val b = two.totalCount / three.totalCount.toDouble()

            if(a > 0.7 && a < 0.85) {
                if(b > 0.7 && b < 0.85) {
                    if(
                        (one.nowPrice > one.beginPrice )
                        && (two.nowPrice > two.beginPrice )
                        && (three.nowPrice > three.beginPrice)
                    ) {
                        var beginIndex = mDeviationValue - 10
                        if(beginIndex < 0) beginIndex = 0
                        var maxCount = 0
                        for( i in beginIndex .. mDeviationValue) {
                            val count = shares[i].totalCount
                            if(count > maxCount) maxCount = count
                        }
                        val c = three.totalCount / maxCount.toDouble()
                        //Log.i(Constant.TAG, "${three.totalCount}, $maxCount")
                        i(TAG, "$a, $b, $c, ${three.brieflyInfo()}")
                        if(mDeviationValue + 3 < size) {
                            val post = shares[mDeviationValue + 3]
                            three.postRange = post.range
                        }
                        mFitModeList.add(Pair(a + b, three))
                    }

                }
            }
        }
    }

    override fun des(): String {
        return "温和放量"
    }
}