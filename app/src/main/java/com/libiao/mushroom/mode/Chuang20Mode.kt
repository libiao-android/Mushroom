package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i

class Chuang20Mode : BaseMode() {

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - 3 - Constant.PRE
        if(mDeviationValue >= 0) {

            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]

            if(one.range > 19) {
                if(two.beginPrice > two.nowPrice && three.beginPrice < three.nowPrice) {
                    i(TAG, "${three.brieflyInfo()}")
                    mFitModeList.add(Pair(three.range, three))
                }
            }
        }
    }

    override fun des(): String {
        return "创业板涨停后强势"
    }

}