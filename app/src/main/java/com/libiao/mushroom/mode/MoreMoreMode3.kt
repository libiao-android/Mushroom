package com.libiao.mushroom.mode

import android.content.Context
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i

class MoreMoreMode3 : BaseMode() {

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - 21 - Constant.PRE

        if(mDeviationValue >= 0) {
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
            val ten1 = shares[mDeviationValue + 10]
            val ten2 = shares[mDeviationValue + 11]
            val ten3 = shares[mDeviationValue + 12]
            val ten4 = shares[mDeviationValue + 13]
            val ten5 = shares[mDeviationValue + 14]
            val ten6 = shares[mDeviationValue + 15]
            val ten7 = shares[mDeviationValue + 16]
            val ten8 = shares[mDeviationValue + 17]
            val ten9 = shares[mDeviationValue + 18]
            val ten10 = shares[mDeviationValue + 19]

            val ten11 = shares[mDeviationValue + 20]
            val ten12 = shares[mDeviationValue + 21]
            val ten13 = shares[mDeviationValue + 22]
            val ten14 = shares[mDeviationValue + 23]
            val ten15 = shares[mDeviationValue + 24]
            val ten16 = shares[mDeviationValue + 25]

            var maxTemp = one.totalPrice
            if(two.totalPrice > maxTemp) maxTemp = two.totalPrice
            if(three.totalPrice > maxTemp) maxTemp = three.totalPrice
            if(four.totalPrice > maxTemp) maxTemp = four.totalPrice
            if(five.totalPrice > maxTemp) maxTemp = five.totalPrice
            if(six.totalPrice > maxTemp) maxTemp = six.totalPrice
            if(seven.totalPrice > maxTemp) maxTemp = seven.totalPrice
            if(eight.totalPrice > maxTemp) maxTemp = eight.totalPrice
            if(nine.totalPrice > maxTemp) maxTemp = nine.totalPrice
            if(ten.totalPrice > maxTemp) maxTemp = ten.totalPrice
            if(ten1.totalPrice > maxTemp) maxTemp = ten1.totalPrice
            if(ten2.totalPrice > maxTemp) maxTemp = ten2.totalPrice
            if(ten3.totalPrice > maxTemp) maxTemp = ten3.totalPrice
            if(ten4.totalPrice > maxTemp) maxTemp = ten4.totalPrice
            if(ten5.totalPrice > maxTemp) maxTemp = ten5.totalPrice
            if(ten6.totalPrice > maxTemp) maxTemp = ten6.totalPrice
            if(ten7.totalPrice > maxTemp) maxTemp = ten7.totalPrice
            if(ten8.totalPrice > maxTemp) maxTemp = ten8.totalPrice
            if(ten9.totalPrice > maxTemp) maxTemp = ten9.totalPrice
            if(ten10.totalPrice > maxTemp) maxTemp = ten10.totalPrice

            val a = ten11.beginPrice < ten11.line_10 && ten11.nowPrice > ten11.line_10
            if (!a) return

            val redList = mutableListOf<SharesRecordActivity.ShareInfo>()
            val greenList = mutableListOf<SharesRecordActivity.ShareInfo>()
            if(ten11.nowPrice > ten11.beginPrice) redList.add(ten11)
            if(ten11.nowPrice < ten11.beginPrice) greenList.add(ten11)

            if(ten12.nowPrice > ten12.beginPrice) redList.add(ten12)
            if(ten12.nowPrice < ten12.beginPrice) greenList.add(ten12)

            if(ten13.nowPrice > ten13.beginPrice) redList.add(ten13)
            if(ten13.nowPrice < ten13.beginPrice) greenList.add(ten13)

            if(ten14.nowPrice > ten14.beginPrice) redList.add(ten14)
            if(ten14.nowPrice < ten14.beginPrice) greenList.add(ten14)

            if(ten15.nowPrice > ten15.beginPrice) redList.add(ten15)
            if(ten15.nowPrice < ten15.beginPrice) greenList.add(ten15)

            if(ten16.nowPrice > ten16.beginPrice) redList.add(ten16)
            if(ten16.nowPrice < ten16.beginPrice) greenList.add(ten16)

            if (redList.size >= greenList.size && greenList.size > 0) {
                redList.sortByDescending { it.totalPrice }
                val redMax = redList[0]
                greenList.sortByDescending { it.totalPrice }
                val greenMax = greenList[0]
                if (redMax.totalPrice > greenMax.totalPrice * 2) {
                    i(TAG, "${ten16.brieflyInfo()}")
                    mFitModeList.add(Pair(ten16.range, ten16))
                }
            }


//            val a = ten11.totalPrice > maxTemp * 5 && ten11.totalPrice > 200000000 && ten11.range > 8
//            val b = ten12.totalPrice > maxTemp * 2
//            val c = ten13.totalPrice > maxTemp * 2
//            val d = ten14.totalPrice > maxTemp * 2
//            val e = ten15.totalPrice > maxTemp * 2
//            val f = ten16.totalPrice > maxTemp * 2

//            if(a) {
//                i(TAG, "${ten11.brieflyInfo()}")
//                mFitModeList.add(Pair(ten11.range, ten11))
//            }
        }
    }

    override fun shouldAnalysis(context: Context): Boolean {
        return true
    }

    override fun des(): String {
        return "放量后阳阴阴"
    }
}