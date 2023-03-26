package com.libiao.mushroom.mode

import android.content.Context
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.room.report.ReportShareDatabase
import com.libiao.mushroom.room.report.ReportShareInfo
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i

class SuoLiangMode : BaseMode() {

    companion object {
        const val KEY = "SuoLiangMode"
    }

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - 2 - Constant.PRE
        if(mDeviationValue >  0) {

            val zero = shares[mDeviationValue - 1]
            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]


            if(one.nowPrice > one.beginPrice && one.range > 3 && one.totalPrice > zero.totalPrice * 2 && one.totalPrice > 100000000
                && two.range > -3 && two.range < 0 && one.totalPrice > two.totalPrice * 2.4
            ) {
                i(TAG, "${two.brieflyInfo()}")
                mFitModeList.add(Pair(two.range, two))

                val info = ReportShareInfo()
                info.code = two.code
                info.time = two.time
                info.name = two.name
                info.updateTime = two.time
                info.ext5 = "3"
                ReportShareDatabase.getInstance()?.getReportShareDao()?.insert(info)
            }
        }
    }

    override fun des(): String {
        return "缩量"
    }

    override fun shouldAnalysis(context: Context): Boolean {
        return false
    }
}