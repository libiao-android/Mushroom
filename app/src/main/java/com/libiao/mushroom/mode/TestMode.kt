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
        val size = shares.size
        mDeviationValue = day - 2
        if(mDeviationValue >  0) {

            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]


            if(poolMap.contains(two.code)) {
                val info = poolMap[two.code]
                info?.also {

                    if(info.updateTime == two.time) {
                        i(TAG, "重复记录")
                    } else {
                        i(TAG, "更新记录")
                        it.updateTime = two.time
                        it.dayCount = it.dayCount + 1
                        TestShareDatabase.getInstance()?.getTestShareDao()?.update(it)
                    }
                }
                return
            }

            if(one.totalPrice > 1000000000 && one.huanShouLv > 5.0 &&
                two.totalPrice > 1000000000 && two.huanShouLv > 5.0) {
                i(TAG, "${two.brieflyInfo()}")
                mFitModeList.add(Pair(two.range, two))


                val info = TestShareInfo()
                info.time = two.time
                info.code = two.code
                info.name = two.name
                info.dayCount = 2
                info.updateTime = two.time

                val id = TestShareDatabase.getInstance()?.getTestShareDao()?.insert(info)
                info.id = id?.toInt() ?: 0
                poolMap[two.code!!] = info
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
        return "test"
    }
}