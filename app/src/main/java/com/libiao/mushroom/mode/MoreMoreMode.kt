package com.libiao.mushroom.mode

import android.content.Context
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.room.report.ReportShareDatabase
import com.libiao.mushroom.room.report.ReportShareInfo
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i
import kotlin.math.abs
import kotlin.math.min

class MoreMoreMode : BaseMode() {

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - 18 - Constant.PRE

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


            val a = ten6.totalPrice > maxTemp * 1.2
            val b = ten7.totalPrice > maxTemp * 2
            val c = ten8.totalPrice > maxTemp * 2

            if(a && b && c) {
                i(TAG, "${ten8.brieflyInfo()}")
                mFitModeList.add(Pair(ten8.range, ten8))
                record(ten8)
            }
        }
    }

    private fun record(one: SharesRecordActivity.ShareInfo) {
        val info = ReportShareInfo()
        info.code = one.code
        info.time = one.time
        info.name = one.name
        info.updateTime = one.time
        info.ext5 = "21"
        ReportShareDatabase.getInstance()?.getReportShareDao()?.insert(info)
    }

    override fun shouldAnalysis(context: Context): Boolean {
        return true
    }

    override fun des(): String {
        return "连续放量"
    }
}