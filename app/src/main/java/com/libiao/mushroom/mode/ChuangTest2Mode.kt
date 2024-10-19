package com.libiao.mushroom.mode

import android.content.Context
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.room.ChuangDatabase
import com.libiao.mushroom.room.MineShareInfo
import com.libiao.mushroom.room.report.ReportShareDatabase
import com.libiao.mushroom.room.report.ReportShareInfo
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil
import com.libiao.mushroom.utils.LogUtil.i

class ChuangTest2Mode : BaseMode {

    companion object {
        const val KEY = "MineMode"
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
        mDeviationValue = day - 7
        if(mDeviationValue >= 20) {
            val one = shares[mDeviationValue]
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]
            val four = shares[mDeviationValue + 3]
            val five = shares[mDeviationValue + 4]
            val six = shares[mDeviationValue + 5]
            val seven = shares[mDeviationValue + 6]

          //  val a = seven.range - seven.rangeBegin > -0.5
            val zhangFu = 11
            val b = one.rangeMax > zhangFu || two.rangeMax > zhangFu || three.rangeMax > zhangFu || four.rangeMax > zhangFu || five.rangeMax > zhangFu
            val c = seven.maxPrice < six.maxPrice && seven.range - seven.rangeBegin > -0.5


            if(six.rangeMax > zhangFu && six.rangeMax <= 15 && b.not() && c) {
                mFitModeList.add(Pair(six.range, six))
                i(TAG, "${six.brieflyInfo()}")

//                val info = ReportShareInfo()
//                info.code = six.code
//                info.time = six.time
//                info.name = six.name
//                ReportShareDatabase.getInstance()?.getReportShareDao()?.insert(info)
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