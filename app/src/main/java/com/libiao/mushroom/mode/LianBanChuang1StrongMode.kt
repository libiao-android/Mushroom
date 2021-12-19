package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i

class LianBanChuang1StrongMode : BaseMode {

    companion object {
        const val KEY = "LianBanChuang1StrongMode"
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
            if(isChuang(one.code)
                && !zhangTing(one)
                && zhangTing(two)
                && !zhangTing(three)
            ) {
                if(three.nowPrice > three.beginPrice && three.range > 0) {
                    if(four.nowPrice < four.beginPrice || four.range < 0) {
                        if(three.totalPrice > four.totalPrice) {
                            if(five.nowPrice > five.beginPrice && five.range > 0) {
                                if(six.range > 0 && six.totalPrice < five.totalPrice) return
                                i(TAG, "${three.brieflyInfo()}")
                                mFitModeList.add(Pair(three.range, three))
                            }
                        }
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
        return "创业板一板强势"
    }
}