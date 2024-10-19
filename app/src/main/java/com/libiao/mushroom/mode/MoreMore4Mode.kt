package com.libiao.mushroom.mode

import android.content.Context
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.room.FangLiangShareDatabase3
import com.libiao.mushroom.room.FangLiangShareInfo
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil
import com.libiao.mushroom.utils.LogUtil.i

class MoreMore4Mode : BaseMode {

    companion object {
        const val KEY = "MoreMore4Mode"
    }

    constructor(): super() {
    }
    constructor(showTime: Boolean): super(showTime) {
    }

    private val poolMap = HashMap<String, FangLiangShareInfo>()

    init {
        val mineShares = FangLiangShareDatabase3.getInstance()?.getFangLiangShareDao()?.getFangLiangShares()
        mineShares?.forEach {
            //LogUtil.i(TAG, "getMineShares: ${it.code}")
            poolMap[it.code!!] = it
        }
        LogUtil.i(TAG, "init: ${poolMap.size}")
    }

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - 11 - Constant.PRE

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


            if(poolMap.contains(ten1.code)) {
                val info = poolMap[ten1.code] ?: return
                if(info.updateTime == ten1.time) {
                    i(TAG, "重复记录")
                    return
                }

                if (!zhangTing(ten1)) {
                    val a = ten1.totalPrice < 500000000
                    if (a) {
                        if (info.delete == 1) {
                            i(TAG, "delete: ${ten1.code}")
                            FangLiangShareDatabase3.getInstance()?.getFangLiangShareDao()?.delete(ten1.code!!)
                            poolMap.remove(ten1.code)
                            return
                        }
                        info.delete = 1
                    } else {
                        info.delete = 0
                    }
                } else {
                    info.delete = 0
                }
                if(ten1.totalPrice > info.maxPrice) {
                    info.maxPrice = ten1.totalPrice
                }
                info.updateTime = ten1.time
                info.dayCount = info.dayCount + 1
                FangLiangShareDatabase3.getInstance()?.getFangLiangShareDao()?.update(info)
            } else {
                val list = mutableListOf<Double>()
                list.add(one.totalPrice)
                list.add(two.totalPrice)
                list.add(three.totalPrice)
                list.add(four.totalPrice)
                list.add(five.totalPrice)
                list.add(six.totalPrice)
                list.add(seven.totalPrice)
                list.add(eight.totalPrice)
                list.add(nine.totalPrice)
                list.add(ten.totalPrice)
                list.sortDescending()


                val a = ten1.totalPrice > list[0]
                val b = ten1.totalPrice > 3000000000 && ten1.huanShouLv > 5
                if(a && b) {
                    i(TAG, "${ten1.brieflyInfo()}")
                    mFitModeList.add(Pair(ten1.range, ten1))
                    //record(ten1, list[0], ten1.nowPrice)
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
        val id = FangLiangShareDatabase3.getInstance()?.getFangLiangShareDao()?.insert(info)
        info.id = id?.toInt() ?: 0
        poolMap[one.code!!] = info
    }

    override fun shouldAnalysis(context: Context): Boolean {
        return true
    }

    override fun des(): String {
        return "放大量"
    }
}
//失败的量能
//金字塔型量能603803