package com.libiao.mushroom.mode

import android.content.Context
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.room.TestShareDatabase
import com.libiao.mushroom.room.TestShareInfo
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i

class TestMode : BaseMode {

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
        mDeviationValue = day - 3
        if(mDeviationValue >  0) {

            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]

            if(poolMap.contains(one.code)) {
                val info = poolMap[one.code]
                info?.also {

                    if(it.updateTime == three.time) {
                        i(TAG, "重复记录")
                    } else {
                        if(three.nowPrice < it.beginPrice) {
                            i(TAG, "更新记录")
                            TestShareDatabase.getInstance()?.getTestShareDao()?.delete(three.code!!)
                        } else {
                            i(TAG, "更新记录")
                            it.updateTime = three.time
                            it.dayCount = it.dayCount + 1
                            TestShareDatabase.getInstance()?.getTestShareDao()?.update(it)
                        }
                    }
                }
                return
            }

            if(two.range > 7 || three.range > 7 || two.range + three.range > 7) {
                if(three.nowPrice > one.nowPrice) {
                    i(TAG, "${three.brieflyInfo()}")
                    mFitModeList.add(Pair(three.range, three))

                    val info = TestShareInfo()
                    info.time = three.time
                    info.code = three.code
                    info.name = three.name
                    info.dayCount = 3
                    info.updateTime = three.time
                    info.beginPrice = one.nowPrice

                    val id = TestShareDatabase.getInstance()?.getTestShareDao()?.insert(info)
                    info.id = id?.toInt() ?: 0
                    poolMap[one.code!!] = info
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
        return false
    }

    override fun des(): String {
        return "test"
    }
}