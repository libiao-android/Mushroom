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
        mDeviationValue = day - 2
        if(mDeviationValue >= 0) {


            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]

            val a = one.nowPrice > one.beginPrice
            val b = two.nowPrice > two.beginPrice
            val c = two.range > one.range
            val d = two.totalPrice / one.totalPrice > 1.1 && two.totalPrice > 200000000
            val e = two.range > 2

            if(a && b && c && d && e) {
                i(TAG, "${two.brieflyInfo()}")
                mFitModeList.add(Pair(two.range, two))

                val info = TestShareInfo()
                info.code = two.code
                info.time = two.time
                info.name = two.name
                info.updateTime = two.time
                info.ext5 = "5"
                TestShareDatabase.getInstance()?.getTestShareDao()?.insert(info)
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

}