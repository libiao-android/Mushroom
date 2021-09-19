package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i

class Chuang20Mode : BaseMode() {

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - 1 - Constant.PRE
        if(mDeviationValue >= 0) {

            val one = shares[mDeviationValue + 0]

            if(isChuang(one.code) && zhangTing(one)) {
                i(TAG, "${one.brieflyInfo()}")
                mFitModeList.add(Pair(one.range, one))
            }
        }
    }

    override fun des(): String {
        return "创业板涨停"
    }

}