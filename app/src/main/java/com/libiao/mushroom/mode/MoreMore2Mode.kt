package com.libiao.mushroom.mode

import android.content.Context
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.room.FangLiangShareDatabase
import com.libiao.mushroom.room.FangLiangShareInfo
import com.libiao.mushroom.room.MineShareDatabase
import com.libiao.mushroom.room.MineShareInfo
import com.libiao.mushroom.room.report.ReportShareDatabase
import com.libiao.mushroom.room.report.ReportShareInfo
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil
import com.libiao.mushroom.utils.LogUtil.i
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class MoreMore2Mode : BaseMode() {

    private val poolMap = HashMap<String, FangLiangShareInfo>()

    init {
        val mineShares = FangLiangShareDatabase.getInstance()?.getFangLiangShareDao()?.getFangLiangShares()
        mineShares?.forEach {
            //LogUtil.i(TAG, "getMineShares: ${it.code}")
            poolMap[it.code!!] = it
        }
        LogUtil.i(TAG, "init: ${poolMap.size}")
    }

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - 16 - Constant.PRE

        if(mDeviationValue >= 0) {
            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]
            val four = shares[mDeviationValue + 3]
            val five = shares[mDeviationValue + 4]
            val six = shares[mDeviationValue + 5]
            val seven = shares[mDeviationValue + 6]
            val eight = shares[mDeviationValue + 7]
            val nine = shares[mDeviationValue + 8]
            val ten = shares[mDeviationValue + 9]
            val ten1 = shares[mDeviationValue + 10]
            val ten2 = shares[mDeviationValue + 11]
            val ten3 = shares[mDeviationValue + 12]
            val ten4 = shares[mDeviationValue + 13]
            val ten5 = shares[mDeviationValue + 14]



            val ten6 = shares[mDeviationValue + 15]


            if(poolMap.contains(ten6.code)) {
                val info = poolMap[ten6.code] ?: return
                if(info.updateTime == ten6.time) {
                    i(TAG, "重复记录")
                    return
                }

                if (!zhangTing(ten6)) {
//                    if (ten6.totalPrice < 100000000 && !dieTing(ten6)) {
//                        i(TAG, "delete: ${ten6.code}")
//                        FangLiangShareDatabase.getInstance()?.getFangLiangShareDao()?.delete(ten6.code!!)
//                        poolMap.remove(ten6.code)
//                        return
//                    }
                    val a = ten6.totalPrice < 100000000
                    val b = ten6.totalPrice < 500000000 && info.maxPrice / ten6.totalPrice > 5.0
                    val c = ten6.totalPrice < info.preAvg * 3
                    if (a || b || c) {
                        if (info.delete == 1) {
                            i(TAG, "delete: ${ten6.code}")
                            FangLiangShareDatabase.getInstance()?.getFangLiangShareDao()?.delete(ten6.code!!)
                            poolMap.remove(ten6.code)
                            return
                        }
                        info.delete = 1
                    } else {
                        info.delete = 0
                    }
//                    if (ten8.totalPrice < 500000000 && info.maxPrice / ten8.totalPrice > 5.0 && !dieTing(ten8)) {
//                        i(TAG, "delete: ${ten8.code}")
//                        FangLiangShareDatabase.getInstance()?.getFangLiangShareDao()?.delete(ten8.code!!)
//                        poolMap.remove(ten8.code)
//                        return
//                    }
//                    if (ten8.totalPrice < info.minPrice) {
//                        info.minPrice = ten8.totalPrice
//                    }
//                    val beishu = ten8.totalPrice / info.maxTemp
//                    if (beishu < info.minBeiShu) {
//                        info.minBeiShu = beishu
//                    }
                } else {
                    info.delete = 0
                }
                if(ten6.totalPrice > info.maxPrice) {
                    info.maxPrice = ten6.totalPrice
                }
                info.updateTime = ten6.time
                info.dayCount = info.dayCount + 1
                FangLiangShareDatabase.getInstance()?.getFangLiangShareDao()?.update(info)
            } else {
//                val list = mutableListOf<Double>()
//                list.add(one.totalPrice)
//                list.add(two.totalPrice)
//                list.add(three.totalPrice)
//                list.add(four.totalPrice)
//                list.add(five.totalPrice)
//                list.add(six.totalPrice)
//                list.add(seven.totalPrice)
//                list.add(eight.totalPrice)
//                list.add(nine.totalPrice)
//                list.add(ten.totalPrice)
//                list.add(ten1.totalPrice)
//                list.add(ten2.totalPrice)
//                list.add(ten3.totalPrice)
//                list.add(ten4.totalPrice)
//                list.add(ten5.totalPrice)
//                list.sortDescending()

                val avg1 = (one.totalPrice + two.totalPrice + three.totalPrice + four.totalPrice + five.totalPrice + six.totalPrice
                + seven.totalPrice
                + eight.totalPrice
                + nine.totalPrice
                + ten.totalPrice
                + ten1.totalPrice
                + ten2.totalPrice
                + ten3.totalPrice
                + ten4.totalPrice
                + ten5.totalPrice) / 15


                val a = zhangTing(ten6) || ten6.totalPrice > avg1 * 3

               // i(TAG, "${ten6.name}, avg: ${avg1}, ${ten6.totalPrice}")

                if(a && ten5.totalPrice > 0) {
                    i(TAG, "${ten6.brieflyInfo()}")
                    mFitModeList.add(Pair(ten6.range, ten6))


                    record(ten6, avg1, ten5.nowPrice)
                }
            }
        }
    }

    private fun record(one: SharesRecordActivity.ShareInfo, avg: Double, beginP: Double) {
        val info = FangLiangShareInfo()
        info.code = one.code
        info.time = one.time
        info.name = one.name
        info.updateTime = one.time
        info.preAvg = avg
        info.dayCount = 1
        info.beginPrice = beginP
        info.maxPrice = one.totalPrice
        val id = FangLiangShareDatabase.getInstance()?.getFangLiangShareDao()?.insert(info)
        info.id = id?.toInt() ?: 0
        poolMap[one.code!!] = info
    }

    override fun shouldAnalysis(context: Context): Boolean {
        return true
    }

    override fun des(): String {
        return "连续放量2"
    }
}