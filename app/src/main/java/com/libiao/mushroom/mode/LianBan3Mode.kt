package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.room.ban.BanShareDatabase
import com.libiao.mushroom.room.ban.three.BanThreeShareInfo
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i

class LianBan3Mode : BaseMode {

    companion object {
        const val KEY = "LianBan3Mode"
    }

    constructor(): super() {
    }
    constructor(showTime: Boolean): super(showTime) {
    }

    private val poolMap = HashMap<String, BanThreeShareInfo>()

    init {
        val allShares = BanShareDatabase.getInstance()?.getBanThreeShareDao()?.getAllShares()
        allShares?.forEach {
            //LogUtil.i(TAG, "getMineShares: ${it.code}")
            poolMap[it.code!!] = it
        }
        i(TAG, "init: ${poolMap.size}")
    }

    override fun analysis(day: Int, shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = day - 4
        if(mDeviationValue >=  0) {
            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]
            val four = shares[mDeviationValue + 3]

            if(poolMap.contains(one.code)) {
                val info = poolMap[one.code]
                info?.also {

                    if(info.updateTime == four.time) {
                        i(TAG, "重复记录")
                    } else {
                        if(it.dayCount > 30) {
                            BanShareDatabase.getInstance()?.getBanThreeShareDao()?.delete(it.code!!)
                        } else {
                            i(TAG, "更新记录")
                            it.updateTime = four.time
                            it.dayCount = it.dayCount + 1
                            BanShareDatabase.getInstance()?.getBanThreeShareDao()?.update(it)
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
            ) {
                i(TAG, "${four.brieflyInfo()}")
                mFitModeList.add(Pair(four.range, four))

                val info = BanThreeShareInfo()
                info.time = four.time
                info.code = four.code
                info.name = four.name
                info.dayCount = 4
                info.updateTime = four.time

                val id = BanShareDatabase.getInstance()?.getBanThreeShareDao()?.insert(info)
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
        return "三连板"
    }
}