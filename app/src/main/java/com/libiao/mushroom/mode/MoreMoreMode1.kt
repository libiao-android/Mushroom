package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i

class MoreMoreMode1 : BaseMode {

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
            val four = shares[mDeviationValue + 3]
            val five = shares[mDeviationValue + 4]

            var maxT = one.totalPrice
            if(two.totalPrice > maxT) maxT = two.totalPrice
            if(three.totalPrice > maxT) maxT = three.totalPrice
            if(four.totalPrice > maxT) maxT = four.totalPrice
            if(five.totalPrice > maxT) maxT = five.totalPrice


            val ten1 = shares[mDeviationValue + 5]
            val ten2 = shares[mDeviationValue + 6]
            val ten3 = shares[mDeviationValue + 7]


            val preAvg = maxT

            var beiShu = 4.0
            if(preAvg > 2000000000) {
                beiShu = 2.0
            } else if(preAvg > 1000000000) {
                beiShu = 2.5
            } else if(preAvg > 500000000) {
                beiShu = 3.0
            } else if(preAvg > 100000000) {
                beiShu = 3.5
            }

            val moreAvg = preAvg * beiShu


            if(moreAvg > 0 && ten1.totalPrice > moreAvg && ten2.totalPrice > moreAvg && ten3.totalPrice > moreAvg) {

                if(ten3.totalPrice > 100000000) {
                    if(ten2.totalPrice > ten1.totalPrice * 0.75) {
                        if(ten3.totalPrice > ten2.totalPrice * 0.75) {
                            i(TAG, "${ten3.brieflyInfo()}, $preAvg")
                            mFitModeList.add(Pair(ten3.range, ten3))
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