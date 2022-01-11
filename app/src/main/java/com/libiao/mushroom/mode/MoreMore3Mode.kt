package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil
import com.libiao.mushroom.utils.LogUtil.i

class MoreMore3Mode : BaseMode {

    companion object {
        const val KEY = "MoreMore3Mode"
    }

    constructor(): super() {
    }
    constructor(showTime: Boolean): super(showTime) {
    }

    override fun analysis(day: Int, shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = day - 8
        if(mDeviationValue >  0) {

            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]

            val ten1 = shares[mDeviationValue + 3]
            val ten2 = shares[mDeviationValue + 4]
            val ten3 = shares[mDeviationValue + 5]
            val ten4 = shares[mDeviationValue + 6]
            val ten5 = shares[mDeviationValue + 7]

            if(one.code == "sz000558") {
                LogUtil.i(TAG, "${one.code}")
            }
            var preAvg = (one.totalPrice + two.totalPrice + three.totalPrice) / 3
            if(zhangTing(three)) {
                preAvg = (one.totalPrice + two.totalPrice) / 2
            }

            var beiShu = 3.0
            if(preAvg > 1000000000) {
                beiShu = 2.0
            } else if(preAvg > 500000000) {
                beiShu = 2.5
            } else if(preAvg > 100000000) {
                beiShu = 3.0
            }

            val moreAvg = preAvg * beiShu

            if(moreAvg > 0
                && (ten1.totalPrice > moreAvg)
                && (ten2.totalPrice > moreAvg)
                && ten3.totalPrice > moreAvg
                && ten4.totalPrice > moreAvg
                && ten5.totalPrice > moreAvg
            ) {

                if(ten5.totalPrice > 100000000) {
                    if(!(ten3.totalPrice > ten4.totalPrice && ten4.totalPrice > ten5.totalPrice)) {
                        if(ten5.nowPrice > ten1.beginPrice) {
                            i(TAG, "${ten3.brieflyInfo()}, $preAvg")

                            var max = ten1.totalPrice
                            var min = ten1.totalPrice
                            if(zhangTing(ten1)) min = ten2.totalPrice
                            if(ten2.totalPrice > max) max = ten2.totalPrice
                            if(ten3.totalPrice > max) max = ten3.totalPrice
                            if(ten4.totalPrice > max) max = ten4.totalPrice
                            if(ten5.totalPrice > max) max = ten5.totalPrice

                            if(ten2.totalPrice < min) min = ten2.totalPrice
                            if(ten3.totalPrice < min) min = ten3.totalPrice
                            if(ten4.totalPrice < min) min = ten4.totalPrice
                            if(ten5.totalPrice < min) min = ten5.totalPrice

                            val liangBi = max / min

                            val avg1 = (ten1.totalPrice + ten2.totalPrice + ten3.totalPrice
                                    + ten4.totalPrice + ten5.totalPrice) / 5
                            val avg2 = one.totalPrice
                            ten5.post1 = String.format("%.1f",avg1 / avg2)
                            //ten5.post2 = String.format("%.1f",(ten1.range + ten2.range + ten3.range + ten4.range + ten5.range))
                            ten5.post2 = String.format("%.1f",liangBi)
                            mFitModeList.add(Pair(ten5.range, ten5))
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
        return "连续三天放量"
    }
}