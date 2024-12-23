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

class TestThreeMode : BaseMode {

    constructor(): super() {
    }
    constructor(showTime: Boolean): super(showTime) {
    }

    init {

    }

    private val poolMap = HashMap<String, TestOneShareInfo>()



    override fun analysis(day: Int, shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = day - 3
        if(mDeviationValue >= 0) {

            val zero = shares[mDeviationValue + 0]
            val one = shares[mDeviationValue + 1]
            if (isKeChuang(one.code) || one.name!!.contains("ST")) return
            val two = shares[mDeviationValue + 2]

            val a = zero.totalPrice < 5000000000
            val b = one.totalPrice < 5000000000
            val c = two.totalPrice > 5000000000 && two.huanShouLv > 10

            if (a && b && c) {
                LogUtil.i(TAG, "${two.brieflyInfo()}")
                mFitModeList.add(Pair(two.range, two))


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
        return "test3"
    }

}