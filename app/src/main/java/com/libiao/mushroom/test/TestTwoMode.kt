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
import kotlin.math.abs

class TestTwoMode : BaseMode {

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
        mDeviationValue = day - 3
        if(mDeviationValue >= 0) {
            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]

            if (isChuang(one.code)) {
                if (one.range < 19 && two.range > 19 && three.range > 19) {
                    LogUtil.i(TAG, "${three.brieflyInfo()}")
                    mFitModeList.add(Pair(three.range, three))

                    val info = TestShareInfo()
                    info.time = three.time
                    info.code = three.code
                    info.name = three.name
                    info.dayCount = 20
                    info.updateTime = three.time
                    info.ext5 = "8888"
                    TestShareDatabase.getInstance()?.getTestShareDao()?.insert(info)
                }
            }
        }
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