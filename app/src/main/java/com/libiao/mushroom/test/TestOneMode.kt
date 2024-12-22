package com.libiao.mushroom.test

import android.content.Context
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.mode.BaseMode
import com.libiao.mushroom.room.TestOneShareDatabase
import com.libiao.mushroom.room.TestOneShareInfo
import com.libiao.mushroom.room.TestShareDatabase
import com.libiao.mushroom.room.TestShareInfo
import com.libiao.mushroom.room.report.ReportShareDatabase
import com.libiao.mushroom.room.report.ReportShareInfo
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil
import java.lang.Double.max

class TestOneMode : BaseMode {

    constructor(): super() {
    }
    constructor(showTime: Boolean): super(showTime) {
    }

    init {

    }

    private val poolMap = HashMap<String, TestOneShareInfo>()

    init {

    }


    override fun analysis(day: Int, shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = day - 4
        if(mDeviationValue >= 0) {
            val one = shares[mDeviationValue + 0]
            if (isKeChuang(one.code) || one.name!!.contains("ST")) return
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]
            val four = shares[mDeviationValue + 3]

            val a = zhangTing(one).not()
            val b = zhangTing(two)
            val c = three.range > 0 && three.nowPrice >= three.beginPrice && three.rangeBegin >= 0.0
            //val c = zhangTing(three) && three.rangeBegin >= 0.0
            //val d = four.maxPrice > three.maxPrice && four.rangeBegin <= 2 && four.range < 0 && four.rangeMax < 4
            val d = four.rangeBegin <= 2 && four.maxPrice < three.maxPrice * 1.04 && four.nowPrice < three.maxPrice
            if (a && b && c && d) {
                if (mDeviationValue + 5 < size) {
                    val five = shares[mDeviationValue + 4]
                    val six = shares[mDeviationValue + 5]
                    val zhang = five.range - five.rangeBegin + six.range
                    four.post1 = baoLiuXiaoShu(zhang)
                }
                LogUtil.i(TAG, "${four.brieflyInfo()}")
                mFitModeList.add(Pair(four.range, four))
                record(four)
            }
        }
    }

    private fun record(one: SharesRecordActivity.ShareInfo) {
        val info = ReportShareInfo()
        info.code = one.code
        info.time = one.time
        info.name = one.name
        info.updateTime = one.time
        info.ext5 = "6666"
        ReportShareDatabase.getInstance()?.getReportShareDao()?.insert(info)
    }
    private fun fanBao(
        one: SharesRecordActivity.ShareInfo,
        two: SharesRecordActivity.ShareInfo
    ): Boolean {
        if (two.nowPrice > two.beginPrice) {
            if (two.range > 5) return true
            if (two.nowPrice > kotlin.math.max(one.beginPrice, one.nowPrice)) return true
        }
        return false
    }

    private fun yinXian(two: SharesRecordActivity.ShareInfo): Boolean {
        return two.range < 0 && two.beginPrice > two.nowPrice
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
        return "test"
    }

}