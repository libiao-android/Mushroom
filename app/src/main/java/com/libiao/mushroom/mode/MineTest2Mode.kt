package com.libiao.mushroom.mode

import android.content.Context
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.room.*
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil
import com.libiao.mushroom.utils.LogUtil.i

class MineTest2Mode : BaseMode {

    companion object {
        const val KEY = "MineTestMode"
    }

    constructor(): super() {
    }
    constructor(showTime: Boolean): super(showTime) {
    }

    private val poolMap = HashMap<String, MineShareInfo>()

    init {
        val mineShares = MineTest2ShareDatabase.getInstance()?.getMineShareDao()?.getMineShares()
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
                if(one.nowPrice < one.line_20) {
                    i(TAG, "delete: ${one.code}")
                    MineTest2ShareDatabase.getInstance()?.getMineShareDao()?.delete(one.code!!)
                    poolMap.remove(one.code)
                } else {
                    info?.also {
                        if(info.updateTime == one.time) {
                            i(TAG, "重复记录")
                        } else {
                            if(one.beginPrice > one.nowPrice && zero.nowPrice > zero.beginPrice && one.totalPrice > zero.totalPrice) {
                                it.youXuan = false
                            }
                            i(TAG, "更新记录")
                            it.updateTime = one.time
                            it.dayCount = it.dayCount + 1
                            if(one.totalPrice > it.label3!!.toDouble()) {
                                it.label3 = one.totalPrice.toString()
                                if(it.dayCount >= 3 && one.range > 0 && one.nowPrice > one.beginPrice && zero.range < 5 && one.totalPrice > zero.totalPrice * 1.8) {
                                //if(it.dayCount >= 3 && one.range > 0 && one.nowPrice > one.beginPrice && zero.range >= 3 && zero.range < 5 && one.totalPrice > zero.totalPrice * 1.8) {
                                    val info = TestShareInfo()
                                    info.code = one.code
                                    info.time = one.time
                                    info.name = one.name
                                    info.updateTime = one.time
                                    info.ext5 = "8"
                                    info.dayCount = it.dayCount
                                    TestShareDatabase.getInstance()?.getTestShareDao()?.insert(info)
                                }
                            }
                            if(it.dayCount < 3) {
                                if(one.maxPrice > it.maxPrice) {
                                    it.maxPrice = one.maxPrice
                                }
                            } else {
                                if(one.maxPrice > it.maxPrice * 1.01) {
                                    it.maxPrice = one.maxPrice
                                    if(it.label2 == null) {
                                        it.label2 = "新高"
                                        it.maxCount = 0
                                    }
                                    it.maxCount ++
                                }
                            }
                            it.nowPrice = one.nowPrice
                            it.delete = false
                            if(one.minPrice <= it.duanCengPrice) {
                                it.duanCeng = false
                            }
                            MineTest2ShareDatabase.getInstance()?.getMineShareDao()?.update(it)

                        }
                    }
                }
            } else {
                if(one.range > 7) {
                    val info = MineShareInfo()
                    info.time = one.time
                    info.code = one.code
                    info.name = one.name
                    info.price = one.nowPrice
                    info.nowPrice = one.nowPrice
                    info.updateTime = one.time
                    info.maxPrice = one.maxPrice
                    info.youXuan = true
                    info.duanCeng = false
                    info.delete = false
                    info.label3 = one.totalPrice.toString()
                    if(one.minPrice > zero.maxPrice) {
                        info.duanCeng = true
                        info.duanCengPrice = zero.maxPrice
                    }
                    val id = MineTest2ShareDatabase.getInstance()?.getMineShareDao()?.insert(info)
                    info.id = id?.toInt() ?: 0
                    poolMap.put(one.code!!, info)
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
        return "我的Test2"
    }
}