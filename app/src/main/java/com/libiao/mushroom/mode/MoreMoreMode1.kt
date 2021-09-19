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
        mDeviationValue = day - 13
        if(mDeviationValue >  0) {

            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]
            val four = shares[mDeviationValue + 3]
            val five = shares[mDeviationValue + 4]
            val six = shares[mDeviationValue + 5]
            val seven = shares[mDeviationValue + 6]
            val eight = shares[mDeviationValue + 7]
            val nine = shares[mDeviationValue + 8]
            val ten = shares[mDeviationValue + 9]

            var maxT = one.totalPrice
            if(two.totalPrice > maxT) maxT = two.totalPrice
            if(three.totalPrice > maxT) maxT = three.totalPrice
            if(four.totalPrice > maxT) maxT = four.totalPrice
            if(five.totalPrice > maxT) maxT = five.totalPrice
            if(six.totalPrice > maxT) maxT = six.totalPrice
            if(seven.totalPrice > maxT) maxT = seven.totalPrice
            if(eight.totalPrice > maxT) maxT = eight.totalPrice
            if(nine.totalPrice > maxT) maxT = nine.totalPrice
            if(ten.totalPrice > maxT) maxT = ten.totalPrice


            val ten1 = shares[mDeviationValue + 10]
            val ten2 = shares[mDeviationValue + 11]
            val ten3 = shares[mDeviationValue + 12]

            var preAvg = (one.totalPrice + two.totalPrice + three.totalPrice
                    + four.totalPrice + five.totalPrice + six.totalPrice + seven.totalPrice
                    + eight.totalPrice + nine.totalPrice + ten.totalPrice) / 10

            preAvg = maxT

            val moreAvg = preAvg * 3



            if(ten1.totalPrice > moreAvg && ten2.totalPrice > moreAvg && ten3.totalPrice > moreAvg) {

                var max = ten1.totalPrice
                if(ten2.totalPrice > max) max = ten2.totalPrice
                if(ten3.totalPrice > max) max = ten3.totalPrice

                var min = ten1.totalPrice
                if(ten2.totalPrice < min) min = ten2.totalPrice
                if(ten3.totalPrice < min) min = ten3.totalPrice

                if(max < min * 2) {

                }

                if(ten3.nowPrice > ten1.nowPrice) {
                    i(TAG, "${ten3.brieflyInfo()}, $preAvg")
                    mFitModeList.add(Pair(ten3.range, ten3))
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
        return "放量"
    }
}