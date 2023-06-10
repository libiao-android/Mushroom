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

class FanBaoAgainMode : BaseMode {

    constructor(): super() {
    }
    constructor(showTime: Boolean): super(showTime) {
    }

    private val poolMap = HashMap<String, TestShareInfo>()

    init {
        val fangLiangShares = TestShareDatabase2.getInstance()?.getTestShareDao()?.getShares()
        fangLiangShares?.forEach {
            //LogUtil.i(TAG, "getMineShares: ${it.code}")
            poolMap[it.code!!] = it
        }
        i(TAG, "init: ${poolMap.size}")
    }


    override fun analysis(day: Int, shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = day - 4
        if(mDeviationValue >  0) {

            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]
            val four = shares[mDeviationValue + 3]
            if(one.range < -3 && two.maxPrice > one.maxPrice && three.range < -3 && four.maxPrice > three.maxPrice) {
                Log.i(TAG, "${four.brieflyInfo()}")
                mFitModeList.add(Pair(four.range, four))

                val report = ReportShareInfo()
                report.code = four.code
                report.time = four.time
                report.name = four.name
                report.dayCount = 10
                report.ext5 = "3"
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
        return true
    }

    override fun des(): String {
        return "再反包"
    }

}