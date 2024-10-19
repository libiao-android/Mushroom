package com.libiao.mushroom.mode

import android.content.Context
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.room.ChuangDatabase
import com.libiao.mushroom.room.MineShareInfo
import com.libiao.mushroom.room.TestShareDatabase
import com.libiao.mushroom.room.TestShareInfo
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil
import com.libiao.mushroom.utils.LogUtil.i

class ChuangMode : BaseMode {

    companion object {
        const val KEY = "ChuangMode"
    }

    constructor(): super() {
    }
    constructor(showTime: Boolean): super(showTime) {
    }

    private val poolMap = HashMap<String, MineShareInfo>()

    init {
        val mineShares = ChuangDatabase.getInstance()?.getMineShareDao()?.getMineShares()
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
            if (isChuang(one.code).not() || one.name?.contains("ST", true) == true) return
            if(poolMap.contains(one.code)) {
                i(TAG, "contains: ${one.code}")
                val info = poolMap[one.code]
                if(info?.updateTime == one.time || info == null) {
                    i(TAG, "重复记录")
                    return
                }
                if (one.nowPrice < info.nowPrice || one.nowPrice < one.line_10) {
                    ChuangDatabase.getInstance()?.getMineShareDao()?.delete(one.code!!)
                    poolMap.remove(one.code)
                } else {
//                    if (one.nowPrice < one.line_10) {
//                        if (info.maxCount == 1) {
//                            ChuangDatabase.getInstance()?.getMineShareDao()?.delete(one.code!!)
//                            poolMap.remove(one.code)
//                        } else {
//                            info.maxCount += 1
//                        }
//                    } else {
//                        info.maxCount = 0
//                    }
                    info.updateTime = one.time
                    info.dayCount = info.dayCount + 1
                    ChuangDatabase.getInstance()?.getMineShareDao()?.update(info)

                    if (info.dayCount > 2) {
                        if (zero.nowPrice >= zero.beginPrice && one.nowPrice >= one.beginPrice) {
                            val xx = TestShareInfo()
                            xx.time = one.time
                            xx.code = one.code
                            xx.name = one.name
                            xx.dayCount = info.dayCount
                            xx.updateTime = one.time
                            xx.ext5 = "222"
                            TestShareDatabase.getInstance()?.getTestShareDao()?.insert(xx)
                        }
                    }
                }
            } else {
                
                if(one.rangeMax > 11) {
                    val info = MineShareInfo()
                    info.time = one.time
                    info.code = one.code
                    info.name = one.name
                    info.price = one.nowPrice
                    info.nowPrice = if (one.yesterdayPrice == 0.00) one.minPrice else one.yesterdayPrice
                    info.dayCount = 0
                    info.updateTime = one.time
                    info.maxPrice = one.maxPrice

                    val id = ChuangDatabase.getInstance()?.getMineShareDao()?.insert(info)
                    info.id = id?.toInt() ?: 0
                    poolMap.put(one.code!!, info)
                    mFitModeList.add(Pair(one.range, one))
                    i(TAG, "${one.brieflyInfo()}")
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

    override fun shouldAnalysis(context: Context): Boolean {
        return true
    }


    override fun des(): String {
        return "创业/科创大涨"
    }
}