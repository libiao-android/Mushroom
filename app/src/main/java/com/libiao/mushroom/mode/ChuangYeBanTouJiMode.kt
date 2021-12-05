package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i

class ChuangYeBanTouJiMode : BaseMode {

    companion object {
        const val KEY = "ChuangYeBanTouJiMode"
    }

    constructor(): super() {
    }
    constructor(showTime: Boolean): super(showTime) {
    }

    override fun analysis(day: Int, shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = day - 1
        if(mDeviationValue >=  0) {
            val one = shares[mDeviationValue + 0]

            if (one.range > 15) {
                i(TAG, "${one.brieflyInfo()}")
                mFitModeList.add(Pair(one.range, one))
            }
        }
    }

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - Constant.PRE
        analysis(mDeviationValue, shares)
    }

    override fun des(): String {
        return "创业板投机"
    }
}