package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.room.Up50ShareDatabase
import com.libiao.mushroom.room.Up50ShareInfo
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i
import kotlin.math.max
import kotlin.math.min

class StrongStock50Mode : BaseMode {

    companion object {
        const val KEY = "StrongStock50Mode"
    }

    constructor(): super() {
    }
    constructor(showTime: Boolean): super(showTime) {
    }


    private val poolMap = HashMap<String, Up50ShareInfo>()

    init {
        val allShares = Up50ShareDatabase.getInstance()?.getUp50ShareDao()?.getShares()
        allShares?.forEach {
            //LogUtil.i(TAG, "getMineShares: ${it.code}")
            poolMap[it.code!!] = it
        }
        i(TAG, "init: ${poolMap.size}")
    }

    override fun analysis(day: Int, shares: ArrayList<SharesRecordActivity.ShareInfo>) {

        mDeviationValue = day - 10

        if(mDeviationValue >= 0) {
            val day1 = shares[mDeviationValue + 0]
            if(day1.name?.contains("ST") == true) return
            val day2 = shares[mDeviationValue + 1]
            val day3 = shares[mDeviationValue + 2]
            val day4 = shares[mDeviationValue + 3]
            val day5 = shares[mDeviationValue + 4]
            val day6 = shares[mDeviationValue + 5]
            val day7 = shares[mDeviationValue + 6]
            val day8 = shares[mDeviationValue + 7]
            val day9 = shares[mDeviationValue + 8]
            val day10 = shares[mDeviationValue + 9]

            if(poolMap.contains(day1.code)) {
                val info = poolMap[day1.code]
                info?.also {

                    if(info.updateTime == day10.time) {
                        i(TAG, "重复记录")
                    } else {
                        i(TAG, "更新记录")
                        it.updateTime = day10.time
                        it.dayCount = it.dayCount + 1
                        Up50ShareDatabase.getInstance()?.getUp50ShareDao()?.update(it)
                    }
                }
                return
            }

            if(day1.huanShouLv > 0) {
                var min = day1.beginPrice
                var max = day1.nowPrice

                var temp = day2
                min = min(min, temp.beginPrice)
                max = max(max, temp.nowPrice)

                temp = day3
                min = min(min, temp.beginPrice)
                max = max(max, temp.nowPrice)

                temp = day4
                min = min(min, temp.beginPrice)
                max = max(max, temp.nowPrice)

                temp = day5
                min = min(min, temp.beginPrice)
                max = max(max, temp.nowPrice)

                temp = day6
                min = min(min, temp.beginPrice)
                max = max(max, temp.nowPrice)

                temp = day7
                min = min(min, temp.beginPrice)
                max = max(max, temp.nowPrice)

                temp = day8
                min = min(min, temp.beginPrice)
                max = max(max, temp.nowPrice)

                temp = day9
                min = min(min, temp.beginPrice)
                max = max(max, temp.nowPrice)

                temp = day10
                min = min(min, temp.beginPrice)
                max = max(max, temp.nowPrice)

                if(day10.beginPrice > day1.beginPrice && min > 0) {
                    val range = (max - min) / min * 100
                    if(range >= 60) {
                        i(TAG, "${day10.brieflyInfo()}, $range")
                        mFitModeList.add(Pair(day10.range, day10))

                        val info = Up50ShareInfo()
                        info.time = day10.time
                        info.code = day10.code
                        info.name = day10.name
                        info.dayCount = 10
                        info.updateTime = day10.time

                        val id = Up50ShareDatabase.getInstance()?.getUp50ShareDao()?.insert(info)
                        info.id = id?.toInt() ?: 0
                        poolMap[day10.code!!] = info
                    }
                }
            }
        }
    }

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - Constant.PRE

        //analysis(mDeviationValue, shares)

        if(Constant.PRE == 0) {
            analysis(mDeviationValue, shares)
        } else {
            i(TAG, "只记录当天")
        }
    }

    override fun des(): String {
        return "50"
    }
}