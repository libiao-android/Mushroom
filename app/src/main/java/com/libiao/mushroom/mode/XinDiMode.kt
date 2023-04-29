package com.libiao.mushroom.mode

import android.content.Context
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.room.TestShareDatabase
import com.libiao.mushroom.room.TestShareInfo
import com.libiao.mushroom.room.ban.BanShareDatabase
import com.libiao.mushroom.room.ban.one.BanOneShareInfo
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i

class XinDiMode : BaseMode {

    companion object {
        const val KEY = "XinDiMode"
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
        val x = 60 + Constant.PRE
        if(size >= v) {
            val prices = ArrayList<Pair<Double, Int>>()
            val liangs = ArrayList<Pair<Double, Int>>()
            for(i in size - v until day) {
                val one = shares[i]
                if(one.minPrice > 0) {
                    prices.add(Pair(one.minPrice, i))
                }
            }
            for(i in size - x until day) {
                val one = shares[i]
                liangs.add(Pair(one.totalPrice, i))
            }
            prices.sortBy { it.first }
            liangs.sortByDescending { it.first }
            val first  = prices[0]

            if(first.second == day - 1 || first.second == day - 2 || first.second == day - 3 || first.second == day - 4) {
                val one = shares[day - 2]
                val two = shares[day - 1]

                val liangFirst = liangs[0]
                val liangSecond = liangs[1]

                val a = liangFirst.first > liangSecond.first * 1.1


                if(one.range < 3 && (liangFirst.second == day - 1) && two.range > 5 && a && two.totalPrice > one.totalPrice * 2) {
                    i(TAG, "新低后放量：${two.brieflyInfo()}")
                    mFitModeList.add(Pair(two.range, two))

                    val info = TestShareInfo()
                    info.time = two.time
                    info.code = two.code
                    info.name = two.name
                    info.dayCount = 120
                    info.updateTime = two.time
                    info.startIndex = day - 120
                    info.ext5 = "4"
                    info.ext4 = baoLiuXiaoShu(liangFirst.first / liangSecond.first)
                    val id = TestShareDatabase.getInstance()?.getTestShareDao()?.insert(info)
                    info.id = id?.toInt() ?: 0
                    poolMap[two.code!!] = info
                }
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
        return "新低"
    }

    override fun shouldAnalysis(context: Context): Boolean {
        return true
    }
}