package com.libiao.mushroom.mode

import android.content.Context
import android.os.Environment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.libiao.mushroom.MushRoomApplication
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.room.*
import com.libiao.mushroom.room.report.ReportShareDatabase
import com.libiao.mushroom.room.report.ReportShareInfo
import com.libiao.mushroom.thread.ThreadPoolUtil
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.FileUtil
import com.libiao.mushroom.utils.LogUtil
import com.libiao.mushroom.utils.LogUtil.i
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.max
import kotlin.math.min

class MineTestMode : BaseMode {

    companion object {
        const val KEY = "MineTestMode"
    }

    constructor(): super() {
    }
    constructor(showTime: Boolean): super(showTime) {
    }

    private val poolMap = HashMap<String, MineShareInfo>()

    init {
        val mineShares = MineTestShareDatabase.getInstance()?.getMineShareDao()?.getMineShares()
        mineShares?.forEach {
            //LogUtil.i(TAG, "getMineShares: ${it.code}")
            poolMap[it.code!!] = it
        }
        LogUtil.i(TAG, "init: ${poolMap.size}")
    }

    override fun analysis(day: Int, shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = day - 2
        if(mDeviationValue >= 20) {
            val zero = shares[mDeviationValue]
            val one = shares[mDeviationValue + 1]
            if(poolMap.contains(one.code)) {
                i(TAG, "contains: ${one.code}")
                val info = poolMap[one.code]
                if(info?.updateTime == one.time) {
                    i(TAG, "重复记录")
                    return
                }
                val a = one.beginPrice > one.nowPrice && one.totalPrice > zero.totalPrice
                val b = one.nowPrice < one.line_20 || one.totalPrice == 0.00
                if (a || b) {
                    MineTestShareDatabase.getInstance()?.getMineShareDao()?.delete(one.code!!)
                    poolMap.remove(one.code)
                } else {
                    info?.also {
                        it.updateTime = one.time
                        it.dayCount = it.dayCount + 1
                        it.nowPrice = one.nowPrice

                        MineTestShareDatabase.getInstance()?.getMineShareDao()?.update(it)

                        if (it.dayCount > 5) {
                            if (one.totalPrice >= it.maxPrice && one.nowPrice > one.beginPrice && it.ext1 == "1") {
                               // report(one, it.dayCount)
                            }
                        }

                        if (one.totalPrice < it.maxPrice) {
                            it.maxPrice = one.totalPrice
                            if (one.nowPrice > one.beginPrice) {
                                it.ext1 = "1"
                            } else {
                                it.ext1 = "0"
                            }
                        } else {
                            it.ext1 = "0"
                        }
                    }

                }
            } else {
                if(one.range >= 8 || zero.range + one.range > 8) {
                    val info = MineShareInfo()
                    info.time = one.time
                    info.code = one.code
                    info.name = one.name
                    info.price = one.nowPrice
                    info.nowPrice = one.nowPrice
                    info.updateTime = one.time
                    info.maxPrice = one.totalPrice
                    val id = MineTestShareDatabase.getInstance()?.getMineShareDao()?.insert(info)
                    info.id = id?.toInt() ?: 0
                    poolMap.put(one.code!!, info)
                    i(TAG, "${one.brieflyInfo()}")
                }
            }
        }
    }

    private fun report(one: SharesRecordActivity.ShareInfo, dayCount: Int) {
        val info = ReportShareInfo()
        info.code = one.code
        info.time = one.time
        info.name = one.name
        info.dayCount = dayCount
        mFitModeList.add(Pair(one.range, one))
        ReportShareDatabase.getInstance()?.getReportShareDao()?.insert(info)
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

    override fun shouldAnalysis(context: Context): Boolean {
        return true
    }

    override fun des(): String {
        return "我的Test"
    }
}