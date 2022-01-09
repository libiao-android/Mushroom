package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.room.MineShareDatabase
import com.libiao.mushroom.room.MineShareInfo
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil
import com.libiao.mushroom.utils.LogUtil.i
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.Charset

class MineMode : BaseMode {

    companion object {
        const val KEY = "MineMode"
    }

    constructor(): super() {
    }
    constructor(showTime: Boolean): super(showTime) {
    }

    private val poolMap = HashMap<String, MineShareInfo>()

    init {
        val mineShares = MineShareDatabase.getInstance()?.getMineShareDao()?.getMineShares()
        mineShares?.forEach {
            //LogUtil.i(TAG, "getMineShares: ${it.code}")
            poolMap[it.code!!] = it
        }
        LogUtil.i(TAG, "init: ${poolMap.size}")
    }

    override fun analysis(day: Int, shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = day - 2
        if(mDeviationValue >=  0) {
            val zero = shares[mDeviationValue]
            val one = shares[mDeviationValue + 1]
            if(poolMap.contains(one.code)) {
                i(TAG, "contains: ${one.code}")
                if(one.nowPrice < one.line_20) {
                    i(TAG, "delete: ${one.code}")
                    MineShareDatabase.getInstance()?.getMineShareDao()?.delete(one.code!!)
                    poolMap.remove(one.code)
                } else {
                    val info = poolMap[one.code]
                    info?.also {
                        if(info.updateTime == one.time) {
                            i(TAG, "重复记录")
                        } else {
                            i(TAG, "更新记录")
                            it.updateTime = one.time
                            it.dayCount = it.dayCount + 1
                            it.nowPrice = one.nowPrice
                            MineShareDatabase.getInstance()?.getMineShareDao()?.update(it)
                        }
                    }
                }
            } else {
                if(one.range >= 7 && one.nowPrice >= one.line_20) {
                    val info = MineShareInfo()
                    info.time = one.time
                    info.code = one.code
                    info.name = one.name
                    info.price = one.nowPrice
                    info.nowPrice = one.nowPrice
                    info.updateTime = one.time
                    val id = MineShareDatabase.getInstance()?.getMineShareDao()?.insert(info)
                    info.id = id?.toInt() ?: 0
                    poolMap.put(one.code!!, info)
                    i(TAG, "${one.brieflyInfo()}")
                    mFitModeList.add(Pair(one.range, one))
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
        return "我的"
    }
}