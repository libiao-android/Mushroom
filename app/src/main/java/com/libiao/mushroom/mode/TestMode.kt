package com.libiao.mushroom.mode

import android.content.Context
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.mine.fragment.Line20Fragment
import com.libiao.mushroom.room.TestShareDatabase
import com.libiao.mushroom.room.TestShareInfo
import com.libiao.mushroom.room.report.ReportShareDatabase
import com.libiao.mushroom.room.report.ReportShareInfo
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil
import com.libiao.mushroom.utils.LogUtil.i
import com.libiao.mushroom.utils.ShareParseUtil

class TestMode : BaseMode {

    constructor(): super() {
    }
    constructor(showTime: Boolean): super(showTime) {
    }

    private val poolMap = HashMap<String, TestShareInfo>()

    init {
        val fangLiangShares = TestShareDatabase.getInstance()?.getTestShareDao()?.getShares()
        fangLiangShares?.forEach {
            //LogUtil.i(TAG, "getMineShares: ${it.code}")
            poolMap[it.code!!] = it
        }
        i(TAG, "init: ${poolMap.size}")
    }


    override fun analysis(day: Int, shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = day - 3
        if(mDeviationValue > 1) {

            val zero = shares[mDeviationValue - 1]

            val one = shares[mDeviationValue + 0]
            if(one.beginPrice <= 0) return
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]

            val a = one.nowPrice > one.beginPrice
            val b = two.nowPrice > two.beginPrice
            val c = three.nowPrice > three.beginPrice
            val avg1 = (one.beginPrice + one.nowPrice) / 2
            val avg2 = (two.beginPrice + two.nowPrice) / 2
            val avg3 = (three.beginPrice + three.nowPrice) / 2
            val d = avg2 in avg1..avg3
            val e = (one.range + two.range + three.range) < 3
            val l = one.rangeMax - one.rangeMin < 3
            val m = two.rangeMax - two.rangeMin < 3
            val n = three.rangeMax - three.rangeMin < 3

            val f = three.totalPrice < two.totalPrice * 2

            val x = two.minPrice > one.minPrice
            val y = three.minPrice > two.minPrice

            val q = zero.range < 0

            if(a && b && c && d && e && f && l && m && n && x && y && q) {
                i(TAG, "${three.brieflyInfo()}")
                mFitModeList.add(Pair(three.range, three))

                val info = ReportShareInfo()
                info.code = three.code
                info.time = three.time
                info.name = three.name
                info.updateTime = three.time
                info.ext5 = "2"
                ReportShareDatabase.getInstance()?.getReportShareDao()?.insert(info)
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
        return "test"
    }

    private fun yin(two: SharesRecordActivity.ShareInfo): Boolean {
        return two.beginPrice > two.nowPrice
    }

    private fun yang(two: SharesRecordActivity.ShareInfo): Boolean {
        return two.nowPrice > two.beginPrice
    }

}