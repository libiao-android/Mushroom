package com.libiao.mushroom.mode

import android.content.Context
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.room.TestShareDatabase
import com.libiao.mushroom.room.TestShareInfo
import com.libiao.mushroom.room.ban.BanShareDatabase
import com.libiao.mushroom.room.ban.one.BanOneShareInfo
import com.libiao.mushroom.room.report.ReportShareDatabase
import com.libiao.mushroom.room.report.ReportShareInfo
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i

class XinGaoMode : BaseMode {

    companion object {
        const val KEY = "XinGaoMode"
    }

    constructor(): super() {
    }
    constructor(showTime: Boolean): super(showTime) {
    }

    private val poolMap = HashMap<String, TestShareInfo>()

    override fun analysis(day: Int, shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        val v = 120 + Constant.PRE
        if(size >= v) {
            val prices = ArrayList<Pair<Double, Int>>()
            for(i in size - v until day) {
                val one = shares[i]
                prices.add(Pair(one.maxPrice, i))
            }
            prices.sortByDescending { it.first }
            val first  = prices[0]
            val second  = prices[1]
            val two = shares[day - 1]
            if(first.second == day - 1 && two.range > 0 && two.nowPrice > two.beginPrice) {

                i(TAG, "新高：${two.brieflyInfo()}")
                mFitModeList.add(Pair(two.range, two))
                record(two)
            }
        }
    }

    private fun record(one: SharesRecordActivity.ShareInfo) {
        val info = ReportShareInfo()
        info.code = one.code
        info.time = one.time
        info.name = one.name
        info.updateTime = one.time
        info.ext5 = "22"
        ReportShareDatabase.getInstance()?.getReportShareDao()?.insert(info)
    }

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - Constant.PRE

        analysis(mDeviationValue, shares)

//        if(Constant.PRE == 0) {
//            analysis(mDeviationValue, shares)
//        } else {
//            i(TAG, "只记录当天")
//        }
    }

    override fun des(): String {
        return "新高"
    }

    override fun shouldAnalysis(context: Context): Boolean {
        return true
    }
}