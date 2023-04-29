package com.libiao.mushroom.mode

import android.content.Context
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.room.TestShareInfo
import com.libiao.mushroom.room.test.TestShareDatabase2
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i

class XinGao2Mode : BaseMode {

    companion object {
        const val KEY = "XinGao2Mode"
    }

    constructor(): super() {
    }
    constructor(showTime: Boolean): super(showTime) {
    }

    private val poolMap = HashMap<String, TestShareInfo>()

    init {
        val fangLiangShares = TestShareDatabase2.getInstance()?.getTestShareDao()?.getShares()
        fangLiangShares?.forEach {
            //LogUtil.i(TAG, "getMineShares: ${it.code}")
            poolMap[it.code!!] = it
        }
        i(TAG, "init: ${poolMap.size}")
    }

    override fun analysis(day: Int, shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        val v = 10 + Constant.PRE

        if(size >= v) {
            val today = shares[day - 1]
            if(poolMap.contains(today.code)) {
                i(TAG, "contains: ${today.code}")
                val record = poolMap[today.code]
                if(record?.updateTime == today.time) {
                    i(TAG, "重复记录")
                    return
                }

                if(today.maxPrice > record!!.maxPrice) {
                    record.maxPrice = today.maxPrice
                    record.maxCount++
                    record.result = 0
                } else {
                    record.result++

                }
                if(record.result == 20) {
                    i(TAG, "删除记录")
                    TestShareDatabase2.getInstance()?.getTestShareDao()?.delete(today.code!!)
                    poolMap.remove(today.code)
                } else {
                    record.dayCount++
                    record.updateTime = today.time
                    TestShareDatabase2.getInstance()?.getTestShareDao()?.update(record)
                }
                return
            }

            val prices = ArrayList<Pair<Double, Int>>()
            val nowPrices = ArrayList<Pair<Double, Int>>()
            //val liang = ArrayList<Pair<Double, Int>>()

            var start = size - v - 110
            if(start < 0) start = 0


            for(i in start until day) {
                val one = shares[i]
                prices.add(Pair(one.maxPrice, i))
                nowPrices.add(Pair(one.nowPrice, i))
                //liang.add(Pair(one.totalPrice, i))
            }

            prices.sortByDescending { it.first }
            nowPrices.sortByDescending { it.first }
            //liang.sortByDescending { it.first }

            val first  = prices[0]
            val second  = prices[1]

            val firstNow  = nowPrices[0]
            val secondNow  = nowPrices[1]

            //val firstLiang = liang[0]

            if(first.second == day - 1 && firstNow.second == day - 1) { //站稳新高
                val two = shares[day - 1]
                i(TAG, "新高：${two.brieflyInfo()}")
                mFitModeList.add(Pair(two.range, two))

                val info = TestShareInfo()
                info.time = two.time
                info.code = two.code
                info.name = two.name
                info.dayCount = 1
                info.updateTime = two.time
                info.maxPrice = two.maxPrice
                info.maxCount = 1
                info.startIndex = start
                info.ext5 = "3"

                val id = TestShareDatabase2.getInstance()?.getTestShareDao()?.insert(info)
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
        return "新高2"
    }

    override fun shouldAnalysis(context: Context): Boolean {
        return true
    }
}