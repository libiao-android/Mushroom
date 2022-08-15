package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.room.ban.BanShareDatabase
import com.libiao.mushroom.room.ban.four.BanFourShareInfo
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i

class LianBan4Mode : BaseMode {

    companion object {
        const val KEY = "LianBan4Mode"
    }

    constructor(): super() {
    }
    constructor(showTime: Boolean): super(showTime) {
    }

    private val poolMap = HashMap<String, BanFourShareInfo>()

    init {
        val allShares = BanShareDatabase.getInstance()?.getBanFourShareDao()?.getAllShares()
        allShares?.forEach {
            //LogUtil.i(TAG, "getMineShares: ${it.code}")
            poolMap[it.code!!] = it
        }
        i(TAG, "init: ${poolMap.size}")
    }

    override fun analysis(day: Int, shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = day - 5
        if(mDeviationValue >=  0) {
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
                        if(it.dayCount > 30) {
                            BanShareDatabase.getInstance()?.getBanFourShareDao()?.delete(it.code!!)
                        } else {
                            i(TAG, "更新记录")
                            it.updateTime = five.time
                            it.dayCount = it.dayCount + 1
                            BanShareDatabase.getInstance()?.getBanFourShareDao()?.update(it)
                        }
                    }
                }
                return
            }

            if(!isChuang(one.code)
                && !zhangTing(one)
                && zhangTing(two)
                && zhangTing(three)
                && zhangTing(four)
                && zhangTing(five)
            ) {
                i(TAG, "${five.brieflyInfo()}")
                mFitModeList.add(Pair(five.range, five))

                val info = BanFourShareInfo()
                info.time = five.time
                info.code = five.code
                info.name = five.name
                info.dayCount = 5
                info.updateTime = five.time

                val id = BanShareDatabase.getInstance()?.getBanFourShareDao()?.insert(info)
                info.id = id?.toInt() ?: 0
                poolMap[one.code!!] = info
            }
        }
    }

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - Constant.PRE

        //analysis(mDeviationValue, shares)

        if(Constant.PRE == 0) {
            analysis(mDeviationValue, shares)
        } else {
            i(TAG, "只记录当天")
        }
    }

    override fun des(): String {
        return "四连板"
    }
}