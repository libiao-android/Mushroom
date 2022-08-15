package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.room.ban.BanShareDatabase
import com.libiao.mushroom.room.ban.five.BanFiveShareInfo
import com.libiao.mushroom.room.ban.four.BanFourShareInfo
import com.libiao.mushroom.room.ban.six.BanSixShareInfo
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i

class LianBan6Mode : BaseMode {

    companion object {
        const val KEY = "LianBan6Mode"
    }

    constructor(): super() {
    }
    constructor(showTime: Boolean): super(showTime) {
    }

    private val poolMap = HashMap<String, BanSixShareInfo>()

    init {
        val allShares = BanShareDatabase.getInstance()?.getBanSixShareDao()?.getAllShares()
        allShares?.forEach {
            //LogUtil.i(TAG, "getMineShares: ${it.code}")
            poolMap[it.code!!] = it
        }
        i(TAG, "init: ${poolMap.size}")
    }

    override fun analysis(day: Int, shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = day - 7
        if(mDeviationValue >=  0) {
            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]
            val four = shares[mDeviationValue + 3]
            val five = shares[mDeviationValue + 4]
            val six = shares[mDeviationValue + 5]
            val seven = shares[mDeviationValue + 6]

            if(poolMap.contains(one.code)) {
                val info = poolMap[one.code]
                info?.also {

                    if(info.updateTime == seven.time) {
                        i(TAG, "重复记录")
                    } else {
                        if(it.dayCount > 30) {
                            BanShareDatabase.getInstance()?.getBanSixShareDao()?.delete(it.code!!)
                        } else {
                            i(TAG, "更新记录")
                            it.updateTime = seven.time
                            it.dayCount = it.dayCount + 1
                            BanShareDatabase.getInstance()?.getBanSixShareDao()?.update(it)
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
                && zhangTing(six)
                && zhangTing(seven)
            ) {
                i(TAG, "${seven.brieflyInfo()}")
                mFitModeList.add(Pair(seven.range, seven))

                val info = BanSixShareInfo()
                info.time = seven.time
                info.code = seven.code
                info.name = seven.name
                info.dayCount = 7
                info.updateTime = seven.time

                val id = BanShareDatabase.getInstance()?.getBanSixShareDao()?.insert(info)
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
        return "六连板及以上"
    }
}