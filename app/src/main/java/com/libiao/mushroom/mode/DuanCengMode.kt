package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i

class DuanCengMode : BaseMode {

    constructor(): super() {
    }
    constructor(showTime: Boolean): super(showTime) {
    }

    override fun analysis(day: Int, shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = day - 3
        if(mDeviationValue >=  0) {

            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]
            if(!zhangTing(one) && zhangTing(two)) {
                val duanCeng = two.minPrice > one.maxPrice
                if(duanCeng) {
                    if(three.totalPrice > two.totalPrice && three.nowPrice > two.minPrice) {
                        i(TAG, "${two.brieflyInfo()}, ${three.range}")
                        mFitModeList.add(Pair(two.rangeBegin, two))
                    }
                }
            }
        }
    }

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - Constant.PRE
        analysis(mDeviationValue, shares)
    }

    override fun des(): String {
        return "断层"
    }
}