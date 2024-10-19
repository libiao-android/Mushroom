package com.libiao.mushroom.mode

import android.content.Context
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.room.TestShareDatabase
import com.libiao.mushroom.room.TestShareInfo
import com.libiao.mushroom.utils.Constant
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class TestMode : BaseMode {

    constructor(): super() {
    }
    constructor(showTime: Boolean): super(showTime) {
    }

    init {

    }


    override fun analysis(day: Int, shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = day - 7
        if(mDeviationValue >= 0) {

            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]
            val four = shares[mDeviationValue + 3]
            val five = shares[mDeviationValue + 4]
            val six = shares[mDeviationValue + 5]
            val seven = shares[mDeviationValue + 6]

            val a = one.rangeMax < 9 && two.rangeMax < 9 && three.rangeMax < 9 && four.rangeMax < 9 && five.rangeMax < 9 && six.rangeMax >= 9
            val b = seven.range - seven.rangeBegin > -0.5
            val c = seven.maxPrice < six.maxPrice

            if(a && b && c) {
                mFitModeList.add(Pair(six.range, six))

                val info = TestShareInfo()
                info.time = six.time
                info.code = six.code
                info.name = six.name
                info.dayCount = 20
                info.updateTime = six.time
                TestShareDatabase.getInstance()?.getTestShareDao()?.insert(info)

            }
        }
    }

    private fun getTpBeiShu(tp: Double): Double {
        return 2.0
    }

    private fun getHuanShouLv(tp: Double): Int {
        if (tp > 2000000000) return 6
        if (tp > 1500000000) return 8
        return 9
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