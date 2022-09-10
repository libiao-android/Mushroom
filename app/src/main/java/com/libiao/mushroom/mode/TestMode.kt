package com.libiao.mushroom.mode

import android.content.Context
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.room.TestShareDatabase
import com.libiao.mushroom.room.TestShareInfo
import com.libiao.mushroom.utils.Constant
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
        mDeviationValue = day - 5
        if(mDeviationValue >  0) {

            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]
            val four = shares[mDeviationValue + 3]
            val five = shares[mDeviationValue + 4]

            if(!isChuang(one.code) && !zhangTing(one) && zhangTing(two) && !zhangTing(three)) {

                if(yin(three) && yang(four) && yang(five) && !zhangTing(four) && !zhangTing(five)) {
                    if(four.maxPrice < five.maxPrice) {
                        i(TAG, "${five.brieflyInfo()}")
                        mFitModeList.add(Pair(five.range, five))

                        val info = TestShareInfo()
                        info.time = two.time
                        info.code = two.code
                        info.name = two.name
                        info.dayCount = 3
                        info.updateTime = two.time
                        info.startIndex = mDeviationValue + 1

                        val id = TestShareDatabase.getInstance()?.getTestShareDao()?.insert(info)
                        info.id = id?.toInt() ?: 0
                        poolMap[two.code!!] = info
                    }
                }
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

    private fun yin(two: SharesRecordActivity.ShareInfo): Boolean {
        return two.beginPrice > two.nowPrice
    }

    private fun yang(two: SharesRecordActivity.ShareInfo): Boolean {
        return two.nowPrice > two.beginPrice
    }

}