package com.libiao.mushroom.mode

import android.content.Context
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.room.TestShareInfo
import com.libiao.mushroom.room.test.TestShareDatabase2
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i

class TestMode2 : BaseMode {

    constructor(): super() {
    }
    constructor(showTime: Boolean): super(showTime) {
    }

    private val poolMap = HashMap<String, TestShareInfo>()

    init {
//        val fangLiangShares = TestShareDatabase2.getInstance()?.getTestShareDao()?.getShares()
//        fangLiangShares?.forEach {
//            //LogUtil.i(TAG, "getMineShares: ${it.code}")
//            poolMap[it.code!!] = it
//        }
//        i(TAG, "init: ${poolMap.size}")
    }


    override fun analysis(day: Int, shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = day - 3
        if(mDeviationValue >  0) {

            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]

            val a = one.range < 0 && two.range < 0 && three.range < 0
            val b = three.range - three.rangeMin > 3

            if(a && b) {
                i(TAG, "${three.brieflyInfo()}")
                mFitModeList.add(Pair(three.range, three))
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
        return "test2"
    }

    private fun yin(two: SharesRecordActivity.ShareInfo): Boolean {
        return two.beginPrice > two.nowPrice
    }

    private fun yang(two: SharesRecordActivity.ShareInfo): Boolean {
        return two.nowPrice > two.beginPrice && two.range > 0
    }

}