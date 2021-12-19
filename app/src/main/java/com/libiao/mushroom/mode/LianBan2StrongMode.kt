package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i

class LianBan2StrongMode : BaseMode {

    companion object {
        const val KEY = "LianBan2StrongMode"
    }

    constructor(): super() {
    }
    constructor(showTime: Boolean): super(showTime) {
    }

    override fun analysis(day: Int, shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = day - 6
        if(mDeviationValue >=  0) {
            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]
            val four = shares[mDeviationValue + 3]

            val five = shares[mDeviationValue + 4]
            val six = shares[mDeviationValue + 5]
            if(!isChuang(one.code)
                && !zhangTing(one)
                && zhangTing(two)
                && zhangTing(three)
                && !zhangTing(four)
            ) {
                if(four.nowPrice > four.beginPrice && four.range > 0) {
                    if(five.beginPrice > five.nowPrice && five.totalPrice > four.totalPrice) return
                    if(six.nowPrice > six.beginPrice && six.range > 0) {
                        i(TAG, "${six.brieflyInfo()}")
                        mFitModeList.add(Pair(six.range, six))
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
        return "二连板强势"
    }
}