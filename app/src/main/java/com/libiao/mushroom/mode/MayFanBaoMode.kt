package com.libiao.mushroom.mode

import android.content.Context
import android.util.Log
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.room.TestShareInfo
import com.libiao.mushroom.room.report.ReportShareDatabase
import com.libiao.mushroom.room.report.ReportShareInfo
import com.libiao.mushroom.room.test.TestShareDatabase2
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i

class MayFanBaoMode : BaseMode {

    constructor(): super() {
    }
    constructor(showTime: Boolean): super(showTime) {
    }

    private val poolMap = HashMap<String, TestShareInfo>()


    override fun analysis(day: Int, shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = day - 1
        if(mDeviationValue >  0) {

            val one = shares[mDeviationValue + 0]
            if(one.name?.contains("ST") == true) return




            if(one.range < -5) {
                Log.i(TAG, "${one.brieflyInfo()}")
                mFitModeList.add(Pair(one.range, one))

                val report = ReportShareInfo()
                report.code = one.code
                report.time = one.time
                report.name = one.name
                report.dayCount = 10
                report.ext5 = "7"
                ReportShareDatabase.getInstance()?.getReportShareDao()?.insert(report)
            }
        }
    }

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - Constant.PRE
        analysis(mDeviationValue, shares)
    }

    override fun shouldAnalysis(context: Context): Boolean {
        return false
    }

    override fun des(): String {
        return "可能反包"
    }

}