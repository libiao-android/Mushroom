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
import kotlin.math.abs

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
        if(mDeviationValue >= 0) {


            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]

            if(one.totalPrice <= 0 || two.totalPrice <= 0) return

            val a = two.totalPrice > one.totalPrice && one.totalPrice > 30000000
            val b = three.totalPrice > two.totalPrice
           // val c = one.nowPrice > one.line_20
           // val d = two.nowPrice > two.line_20
           // val e = three.nowPrice > three.line_20
            val f = (two.totalPrice - one.totalPrice) / one.totalPrice
            val g = (three.totalPrice - two.totalPrice) / two.totalPrice
            val h = abs(f - g) < 0.99 && f > 0.2 && g > 0.2
            val j = one.nowPrice > one.beginPrice && two.nowPrice > two.beginPrice && three.nowPrice > three.beginPrice
            val k = three.range > two.range && two.range > one.range


            if(a && b && h && j && k) {
                i(TAG, "${three.brieflyInfo()}")
                mFitModeList.add(Pair(three.range, three))

                val info = TestShareInfo()
                info.code = three.code
                info.time = three.time
                info.name = three.name
                info.updateTime = three.time
                info.ext5 = "5"
                info.label1 = "${baoLiuXiaoShu(f)}, ${baoLiuXiaoShu(g)}"
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
        return true
    }

    override fun des(): String {
        return "test"
    }

}