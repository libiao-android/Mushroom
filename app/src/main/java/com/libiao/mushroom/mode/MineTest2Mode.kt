package com.libiao.mushroom.mode

import android.content.Context
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.room.*
import com.libiao.mushroom.room.report.ReportShareInfo
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil
import com.libiao.mushroom.utils.LogUtil.i
import kotlin.math.min

class MineTest2Mode : BaseMode {

    companion object {
        const val KEY = "MineTestMode"
    }

    constructor(): super() {
    }
    constructor(showTime: Boolean): super(showTime) {
    }

    private val poolMap = HashMap<String, MineShareInfo>()
    private val poolMap2 = HashMap<String, ReportShareInfo>()

    init {
        val mineShares = MineTest2ShareDatabase.getInstance()?.getMineShareDao()?.getMineShares()
        val mineShares2 = XinGaoShareDatabase.getInstance()?.getReportShareDao()?.getShares()
        mineShares?.forEach {
            //LogUtil.i(TAG, "getMineShares: ${it.code}")
            poolMap[it.code!!] = it
        }
        LogUtil.i(TAG, "init: ${poolMap.size}")


        mineShares2?.forEach {
            //LogUtil.i(TAG, "getMineShares: ${it.code}")
            poolMap2[it.code!!] = it
        }
        LogUtil.i(TAG, "init2: ${poolMap2.size}")
    }

    override fun analysis(day: Int, shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = day - 2
        if(mDeviationValue >= 20) {
            val zero = shares[mDeviationValue]
            val one = shares[mDeviationValue + 1]
            if(poolMap.contains(one.code)) {
               // i(TAG, "contains: ${one.code}")
                val info = poolMap[one.code] ?: return
                if(info?.updateTime == one.time) {
                    i(TAG, "重复记录")
                    return
                }
                val beginP = info?.label1?.toDouble() ?: 0.00
                val maxP = info?.label2?.toDouble() ?: 0.00
                val minP = info?.label3?.toDouble() ?: 0.00
                val maxMP = info?.label4?.toDouble() ?: 0.00

                val hong = info?.label5?.toInt() ?: 0
                val lv = info?.label6?.toInt() ?: 0


                val maxR = (maxP - beginP) / beginP * 100

                info.updateTime = one.time
                info.dayCount = info.dayCount + 1
                if (one.range > 0) {
                    info?.label5 = (hong + 1).toString()
                    if(one.nowPrice > maxP) {
                        info?.label2 = one.nowPrice.toString()
                        // 创新高
                        //if (maxR > 15 && lv > 1 && one.maxPrice > maxMP && info.youXuan.not() && info.maxCount == 0) {

                            //info.youXuan = true
                            //info.maxCount = 1
                            //recordXinGao(info)
                        //}
                    }
                } else {
                    info.youXuan = false
                    info?.label6 = (lv + 1).toString()
                    if (one.nowPrice < minP) {
                        info?.label3 = one.nowPrice.toString()
                    }
                    val minR = (one.nowPrice - beginP) / beginP * 100
                    if (maxR - minR > min(10.00, maxR * 0.3)) {
                        i(TAG, "delete: ${one.code}")
                        MineTest2ShareDatabase.getInstance()?.getMineShareDao()?.delete(one.code!!)
                        poolMap.remove(one.code)

                      //  XinGaoShareDatabase.getInstance()?.getMineShareDao()?.delete(one.code!!)
                      //  poolMap2.remove(one.code)

                        return
                    }
                }
                if (one.maxPrice > maxMP) {
                    info?.label4 = one.maxPrice.toString()
                }
                MineTest2ShareDatabase.getInstance()?.getMineShareDao()?.update(info!!)
            } else {
                if(zero.range < 0 && one.range > 0 && one.yesterdayPrice > 0) {
                    val info = MineShareInfo()
                    info.time = one.time
                    info.code = one.code
                    info.name = one.name
                    info.updateTime = one.time
                    info.dayCount = 1

                    info.label1 = one.yesterdayPrice.toString() // 开始价格
                    info.label2 = one.nowPrice.toString() // 最高收盘价格
                    info.label3 = one.nowPrice.toString() // 最低收盘价格
                    info.label4 = one.maxPrice.toString() // 最高价格

                    info.label5 = "1" // hong
                    info.label6 = "0" // 绿


                    val id = MineTest2ShareDatabase.getInstance()?.getMineShareDao()?.insert(info)
                    info.id = id?.toInt() ?: 0
                    poolMap.put(one.code!!, info)

                   // mFitModeList.add(Pair(one.range, one))
                   // i(TAG, "${one.brieflyInfo()}")
                   // i(TAG, "${one.brieflyInfo()}")
                }
            }
        }
    }

    private fun recordXinGao(one: MineShareInfo?) {
        val info = ReportShareInfo()
        info.time = one?.updateTime
        info.code = one?.code
        info.name = one?.name
        info.dayCount = one?.dayCount ?: 0

        info.label1 = one?.label1 // 开始价格
        info.label2 = one?.label2.toString() // 最高收盘价格
        info.label3 = one?.label3.toString() // 最低收盘价格
        info.label4 = one?.label4.toString() // 最高价格

        info.label5 = one?.label5 // hong
        info.label6 = one?.label6 // 绿


        val id = XinGaoShareDatabase.getInstance()?.getReportShareDao()?.insert(info)
        info.id = id?.toInt() ?: 0
        // poolMap2.put(one?.code!!, info)
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
        return "我的Test2"
    }
}