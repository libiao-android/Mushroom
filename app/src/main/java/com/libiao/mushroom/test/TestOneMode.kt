package com.libiao.mushroom.test

import android.content.Context
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.mode.BaseMode
import com.libiao.mushroom.room.TestOneShareDatabase
import com.libiao.mushroom.room.TestOneShareInfo
import com.libiao.mushroom.room.TestShareDatabase
import com.libiao.mushroom.room.TestShareInfo
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
        val mineShares = TestOneShareDatabase.getInstance()?.getMineShareDao()?.getMineShares()
        mineShares?.forEach {
            //LogUtil.i(TAG, "getMineShares: ${it.code}")
            poolMap[it.code!!] = it
        }
        LogUtil.i(TAG, "init: ${poolMap.size}")
    }


    override fun analysis(day: Int, shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = day - 2
        if(mDeviationValue >= 0) {

            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]
            if (two.code?.startsWith("sh688") == true || two.name!!.contains("ST")) return

            val a = zhangTing(one) && zhangTing(two).not() && two.range > 0 && two.nowPrice > two.beginPrice && two.rangeBegin >= 0.0

            if (a) {
                LogUtil.i(TAG, "${two.brieflyInfo()}")
                mFitModeList.add(Pair(two.range, two))

                val info = TestShareInfo()
                info.time = two.time
                info.code = two.code
                info.name = two.name
                info.dayCount = 20
                info.updateTime = two.time
                info.ext5 = "111"
                TestShareDatabase.getInstance()?.getTestShareDao()?.insert(info)
            }
        }
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