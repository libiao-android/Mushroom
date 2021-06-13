package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.LogUtil.i

class LessMildMode : BaseMode() {

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - 3
        if(mDeviationValue >= 0) {
            val today = shares[mDeviationValue + 2]
            val pre1 = shares[mDeviationValue + 1]
            val pre2 = shares[mDeviationValue]

            if(today.nowPrice > 4 && today.totalPrice > 150000000) {
                if(pre2.nowPrice > pre2.beginPrice && pre2.range > 0 && (pre2.range < 2 || pre2.maxPrice - pre2.nowPrice < pre2.nowPrice - pre2.beginPrice)) {
                    val a = pre1.totalPrice / pre2.totalPrice
                    val b = today.totalPrice / pre1.totalPrice
                    if(a < 0.8 && a > 0.7) {
                        if(b < 0.8 && b > 0.7) {
                            if(today.zongShiZhi > 30 && today.zongShiZhi > 100) {
                                i(TAG, "两天缩量：${today.brieflyInfo()}, ${today.range}, ${pre1.range}")
                                mFitModeList.add(Pair(today.totalPrice, today))
                            }
                        }
                    }
                }
            }
        }
    }

    override fun des(): String {
        return "连续缩量"
    }
}