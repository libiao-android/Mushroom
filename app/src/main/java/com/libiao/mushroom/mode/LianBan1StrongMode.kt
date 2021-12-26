package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i

class LianBan1StrongMode : BaseMode {

    companion object {
        const val KEY = "LianBan1StrongMode"
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
                && !zhangTing(three)
                && !zhangTing(four)
            ) {
                if(three.minPrice > three.line_10
                    && four.minPrice > four.line_10
                    && five.minPrice > five.line_10
                    && six.minPrice > six.line_10
                ) {
                    if(!(three.totalPrice > four.totalPrice
                        && four.totalPrice > five.totalPrice
                        && five.totalPrice > six.totalPrice)
                    ) {

                        var minP = three.minPrice
                        if(four.minPrice < minP) minP = four.minPrice
                        if(five.minPrice < minP) minP = five.minPrice
                        if(six.minPrice < minP) minP = six.minPrice

                        val value = minP / two.nowPrice




                        var r = 0.0
                        if(mDeviationValue + 7 < size) {
                            val seven = shares[mDeviationValue + 6]
                            val eight = shares[mDeviationValue + 7]
                            r = seven.range - seven.rangeBegin + eight.range
                        }

                        i(TAG, "${six.brieflyInfo()}, $r, $value")
                        six.post1 = baoLiuXiaoShu(r)
                        six.post2 = baoLiuXiaoShu(value)
                        mFitModeList.add(Pair(r, six))
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
        return "一连板强势"
    }
}