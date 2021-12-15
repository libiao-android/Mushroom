package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.bean.SharesInfo
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i
import kotlin.math.abs
import kotlin.math.min

class YiZiBanMode : BaseMode() {

    companion object {
        const val KEY = "YiZiBanMode"
    }

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - 1 - Constant.PRE
        if(mDeviationValue >  0) {

            val zero = shares[mDeviationValue - 1]
            val one = shares[mDeviationValue + 0]

            if(!yiZi(zero) && yiZi(one)) {
                i(TAG, "${one.brieflyInfo()}")
                mFitModeList.add(Pair(one.range, one))
            }

        }
    }

    private fun yiZi(info: SharesRecordActivity.ShareInfo): Boolean {
        return zhangTing(info) && info.minPrice == info.maxPrice
    }

    override fun des(): String {
        return "一字板"
    }
}