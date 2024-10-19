package com.libiao.mushroom.test

import android.content.Context
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.mode.BaseMode
import com.libiao.mushroom.room.ChuangDatabase
import com.libiao.mushroom.room.MineShareInfo
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil
import com.libiao.mushroom.utils.LogUtil.i

class ChuangTestOneMode : BaseMode {

    companion object {
        const val KEY = "MineMode"
    }

    constructor(): super() {
    }
    constructor(showTime: Boolean): super(showTime) {
    }

    private val poolMap = HashMap<String, MineShareInfo>()


    override fun analysis(day: Int, shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = day - 2
        if(mDeviationValue >= 20) {
            val one = shares[mDeviationValue]
            val two = shares[mDeviationValue + 1]
            if(one.rangeMax < 11 && two.rangeMax > 11 && isChuang(two.code) && two.name!!.contains("ST", true).not()) {
//                val info = MineShareInfo()
//                info.time = one.time
//                info.code = one.code
//                info.name = one.name
//                info.price = one.nowPrice
//                info.nowPrice = if (one.yesterdayPrice == 0.00) one.minPrice else one.yesterdayPrice
//                info.dayCount = 0
//                info.updateTime = one.time
//                info.maxPrice = one.maxPrice

                mFitModeList.add(Pair(two.range, two))
                i(TAG, "${two.brieflyInfo()}")
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