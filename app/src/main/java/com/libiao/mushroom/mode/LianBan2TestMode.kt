package com.libiao.mushroom.mode

import android.content.Context
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.room.TestShareDatabase
import com.libiao.mushroom.room.TestShareInfo
import com.libiao.mushroom.room.ban.BanShareDatabase
import com.libiao.mushroom.room.ban.two.BanTwoShareInfo
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i

class LianBan2TestMode : BaseMode {

    companion object {
        const val KEY = "LianBan2TestMode"
    }

    constructor(): super() {
    }
    constructor(showTime: Boolean): super(showTime) {
    }

    private val poolMap = HashMap<String, BanTwoShareInfo>()

    init {
//        val allShares = BanShareDatabase.getInstance()?.getBanTwoShareDao()?.getAllShares()
//        allShares?.forEach {
//            //LogUtil.i(TAG, "getMineShares: ${it.code}")
//            poolMap[it.code!!] = it
//        }
        i(TAG, "init: ${poolMap.size}")
    }

    override fun analysis(day: Int, shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = day - 3
        if(mDeviationValue >=  0) {
            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]

//            if(poolMap.contains(one.code)) {
//                val info = poolMap[one.code]
//                info?.also {
//
//                    if(info.updateTime == three.time) {
//                        i(TAG, "重复记录")
//                    } else {
//                        if(it.dayCount > 30) {
//                            BanShareDatabase.getInstance()?.getBanTwoShareDao()?.delete(it.code!!)
//                        } else {
//                            i(TAG, "更新记录")
//                            it.updateTime = three.time
//                            it.dayCount = it.dayCount + 1
//                            BanShareDatabase.getInstance()?.getBanTwoShareDao()?.update(it)
//                        }
//                    }
//                }
//                return
//            }

            if(!isChuang(one.code)
                && !zhangTing(one)
                && zhangTing(two)
                && zhangTing(three)
            ) {
                i(TAG, "${three.brieflyInfo()}")
                mFitModeList.add(Pair(three.range, three))
                val info = TestShareInfo()
                info.code = three.code
                info.time = three.time
                info.name = three.name
                info.updateTime = three.time
                info.dayCount = 3
                info.ext5 = "11"
                TestShareDatabase.getInstance()?.getTestShareDao()?.insert(info)
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
        return "二连板Test"
    }
}