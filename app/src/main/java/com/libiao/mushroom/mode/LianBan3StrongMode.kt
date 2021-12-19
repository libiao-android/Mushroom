package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i

class LianBan3StrongMode : BaseMode {

    companion object {
        const val KEY = "LianBan3StrongMode"
    }

    constructor(): super() {
    }
    constructor(showTime: Boolean): super(showTime) {
    }

    override fun analysis(day: Int, shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = day - 7
        if(mDeviationValue >=  0) {
            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]
            val four = shares[mDeviationValue + 3]
            val five = shares[mDeviationValue + 4]

            val six = shares[mDeviationValue + 5]
            val seven = shares[mDeviationValue + 6]
            if(!isChuang(one.code)
                && !zhangTing(one)
                && zhangTing(two)
                && zhangTing(three)
                && zhangTing(four)
                && !zhangTing(five)
            ) {
                val condition1 = five.nowPrice > five.beginPrice && five.range > 0
                        && seven.nowPrice > seven.beginPrice && seven.range > 0
                val condition2 = six.nowPrice > six.beginPrice && six.range > 0

                if(condition1 || condition2) {
                    i(TAG, seven.brieflyInfo())
                    mFitModeList.add(Pair(seven.range, seven))
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
        return "三连板强势"
    }
}