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
        mDeviationValue = size - 12 - Constant.PRE

        if(mDeviationValue >= 10) {
            val one = shares[mDeviationValue + 0]
            if (!isChuang(one.code) && !isKeChuang(one.code)) return
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

            var maxTemp = one.totalPrice
            if(two.totalPrice > maxTemp) maxTemp = two.totalPrice
            if(three.totalPrice > maxTemp) maxTemp = three.totalPrice
            if(four.totalPrice > maxTemp) maxTemp = four.totalPrice
            if(five.totalPrice > maxTemp) maxTemp = five.totalPrice
            if(six.totalPrice > maxTemp) maxTemp = six.totalPrice
            if(seven.totalPrice > maxTemp) maxTemp = seven.totalPrice
            if(eight.totalPrice > maxTemp) maxTemp = eight.totalPrice

            val a = nine.totalPrice > maxTemp
            val b = nine.totalPrice > eight.totalPrice * 2
            val c = nine.range > 0 && nine.nowPrice >= nine.beginPrice && nine.rangeMax > 7
            val d = ten.totalPrice > nine.totalPrice
            val e = ten.range > 0 && ten.nowPrice >= ten.beginPrice
            val f = ten.maxPrice > nine.maxPrice

            val g = ten.rangeMax - ten.range < ten.range



            if(a && b && c && d && e && f && g) {
                i(TAG, "${ten.brieflyInfo()}")
                ten.post1 = (ten1.range - ten1.rangeBegin + ten2.range).toString()
                mFitModeList.add(Pair(ten.range + nine.range, ten))

            }
        }
    }

    private fun record(one: SharesRecordActivity.ShareInfo) {
        val info = ReportShareInfo()
        info.code = one.code
        info.time = one.time
        info.name = one.name
        info.updateTime = one.time
        info.ext5 = "3"
        ReportShareDatabase.getInstance()?.getReportShareDao()?.insert(info)
    }

    private fun yang(info: SharesRecordActivity.ShareInfo): Boolean {
        return info.range > 0 && info.nowPrice >= info.beginPrice
    }

    override fun shouldAnalysis(context: Context): Boolean {
        return true
    }

    override fun des(): String {
        return "连续放量"
    }
}