package com.libiao.mushroom.mode

import android.content.Context
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.room.TestShareDatabase
import com.libiao.mushroom.room.TestShareInfo
import com.libiao.mushroom.room.ban.BanShareDatabase
import com.libiao.mushroom.room.ban.one.BanOneShareInfo
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i

class XinGaoMode : BaseMode {

    companion object {
        const val KEY = "XinGaoMode"
    }

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
        val v = 120 + Constant.PRE
        if(size >= v) {
            val prices = ArrayList<Pair<Double, Int>>()
            for(i in size - v until day) {
                val one = shares[i]
                prices.add(Pair(one.maxPrice, i))
            }
            prices.sortByDescending { it.first }
            val first  = prices[0]
            val second  = prices[1]
            if(first.second == day - 1 && first.second - second.second > 30) {
                val two = shares[day - 1]
                i(TAG, "新高：${two.brieflyInfo()}")
                mFitModeList.add(Pair(two.range, two))

                val info = TestShareInfo()
                info.time = two.time
                info.code = two.code
                info.name = two.name
                info.dayCount = 120
                info.updateTime = two.time
                info.startIndex = day - 120

                val id = TestShareDatabase.getInstance()?.getTestShareDao()?.insert(info)
                info.id = id?.toInt() ?: 0
                poolMap[two.code!!] = info
            }
        }
    }

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - Constant.PRE

        analysis(mDeviationValue, shares)

//        if(Constant.PRE == 0) {
//            analysis(mDeviationValue, shares)
//        } else {
//            i(TAG, "只记录当天")
//        }
    }

    override fun des(): String {
        return "新高"
    }

    override fun shouldAnalysis(context: Context): Boolean {
        return true
    }
}