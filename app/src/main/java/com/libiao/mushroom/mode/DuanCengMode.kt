package com.libiao.mushroom.mode

import android.content.Context
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.room.TestShareDatabase
import com.libiao.mushroom.room.TestShareInfo
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i

class DuanCengMode : BaseMode {

    constructor(): super() {
    }
    constructor(showTime: Boolean): super(showTime) {
    }

    private val poolMap = HashMap<String, TestShareInfo>()

    init {
        val fangLiangShares = TestShareDatabase.getInstance()?.getTestShareDao()?.getShares()
        fangLiangShares?.forEach {
            //LogUtil.i(TAG, "getMineShares: ${it.code}")
            poolMap[it.code!!] = it
        }
        i(TAG, "init: ${poolMap.size}")
    }



    override fun analysis(day: Int, shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = day - 5
        if(mDeviationValue >  0) {

            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]
            val four = shares[mDeviationValue + 3]
            val five = shares[mDeviationValue + 4]


            if(poolMap.contains(one.code)) {
                val info = poolMap[one.code]
                info?.also {

                    if(info.updateTime == five.time) {
                        i(TAG, "重复记录")
                    } else {
                        i(TAG, "更新记录")
                        it.updateTime = five.time
                        it.dayCount = it.dayCount + 1
                        TestShareDatabase.getInstance()?.getTestShareDao()?.update(it)
                    }
                }
                return
            }

            if(two.range > 0 && two.nowPrice > two.beginPrice) {
                if(three.range > 0 && three.nowPrice > three.beginPrice) {
                    if(two.totalPrice > one.totalPrice * 1.5 && three.totalPrice > one.totalPrice * 1.5 && two.totalPrice > three.totalPrice) {
                        if(four.beginPrice > four.nowPrice && five.beginPrice > five.nowPrice) {
                            if(four.totalPrice * 1.5 < three.totalPrice) {
                                if(four.totalPrice > five.totalPrice) {
                                    if(!zhangTing(one) && !zhangTing(two) && !zhangTing(three)) {
                                        i(TAG, "${five.brieflyInfo()}")
                                        mFitModeList.add(Pair(five.range, three))


                                        val info = TestShareInfo()
                                        info.time = five.time
                                        info.code = five.code
                                        info.name = five.name
                                        info.dayCount = 5
                                        info.updateTime = five.time

                                        val id = TestShareDatabase.getInstance()?.getTestShareDao()?.insert(info)
                                        info.id = id?.toInt() ?: 0
                                        poolMap[one.code!!] = info
                                    }
                                }
                            }
                        }
                    }
                }
            }


        }
    }

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - Constant.PRE
        analysis(mDeviationValue, shares)
    }

    override fun shouldAnalysis(context: Context): Boolean {
        return true
    }

    override fun des(): String {
        return "断层"
    }
}