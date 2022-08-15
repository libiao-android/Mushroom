package com.libiao.mushroom.mode

import android.content.Context
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.bean.SharesInfo
import com.libiao.mushroom.room.TestShareDatabase
import com.libiao.mushroom.room.TestShareInfo
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i
import kotlin.math.abs
import kotlin.math.min

class FiveRedMode : BaseMode() {

    companion object {
        const val KEY = "FiveRedMode"
    }

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - 5 - Constant.PRE
        if(mDeviationValue >  0) {

            val zero = shares[mDeviationValue - 1]
            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]
            val four = shares[mDeviationValue + 3]
            val five = shares[mDeviationValue + 4]

            if(zero.beginPrice > zero.nowPrice
                && one.nowPrice >= one.beginPrice
                && two.nowPrice >= two.beginPrice
                && three.nowPrice >= three.beginPrice
                && four.nowPrice >= four.beginPrice
                && five.nowPrice >= five.beginPrice
                && one.range < 5
                && two.range < 5
                && three.range < 5
                && four.range < 5
                && five.range < 5
                && one.totalPrice > 100000000
                && two.totalPrice > 100000000
                && three.totalPrice > 100000000
                && four.totalPrice > 100000000
                && five.totalPrice > 100000000
            ) {
                val oneM = (one.beginPrice + one.nowPrice) / 2
                val twoM = (two.beginPrice + two.nowPrice) / 2
                val threeM = (three.beginPrice + three.nowPrice) / 2
                val fourM = (four.beginPrice + four.nowPrice) / 2
                val fiveM = (five.beginPrice + five.nowPrice) / 2
                if(one.range > 0
                    && two.range > 0
                    && three.range > 0
                    && four.range > 0
                    && five.range > 0
                ) {
                    i(TAG, "${five.brieflyInfo()}")
                    mFitModeList.add(Pair(five.range, five))

                    val info = TestShareInfo()
                    info.time = five.time
                    info.code = five.code
                    info.name = five.name
                    info.dayCount = mDeviationValue + 4
                    info.ext1 = (one.range + two.range + three.range + four.range + five.range).toString()
                    info.ext2 = five.liuTongShiZhi.toString()
                    TestShareDatabase.getInstance()?.getTestShareDao()?.insert(info)
                }
            }
        }
    }

    override fun shouldAnalysis(context: Context): Boolean {
        return false
    }

    override fun des(): String {
        return "5连阳"
    }
}