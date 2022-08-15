package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.room.ban.BanShareDatabase
import com.libiao.mushroom.room.ban.one.BanOneShareInfo
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i

class LianBan1Mode : BaseMode {

    companion object {
        const val KEY = "LianBan1Mode"
    }

    constructor(): super() {
    }
    constructor(showTime: Boolean): super(showTime) {
    }

    private val poolMap = HashMap<String, BanOneShareInfo>()

    init {
        val allShares = BanShareDatabase.getInstance()?.getBanOneShareDao()?.getAllShares()
        allShares?.forEach {
            //LogUtil.i(TAG, "getMineShares: ${it.code}")
            poolMap[it.code!!] = it
        }
        i(TAG, "init: ${poolMap.size}")
    }


    override fun analysis(day: Int, shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = day - 2
        if(mDeviationValue >=  0) {
            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]

            if(poolMap.contains(one.code)) {
                val info = poolMap[one.code]
                info?.also {

                    if(info.updateTime == two.time) {
                        i(TAG, "重复记录")
                    } else {
                        if(it.dayCount > 30) {
                            BanShareDatabase.getInstance()?.getBanOneShareDao()?.delete(it.code!!)
                        } else {
                            i(TAG, "更新记录")
                            it.updateTime = two.time
                            it.dayCount = it.dayCount + 1
                            BanShareDatabase.getInstance()?.getBanOneShareDao()?.update(it)
                        }
                    }
                }
                return
            }

            if(!isChuang(one.code)
                && !zhangTing(one)
                && zhangTing(two)
            ) {
                i(TAG, "${two.brieflyInfo()}")
                mFitModeList.add(Pair(two.range, two))

                val info = BanOneShareInfo()
                info.time = two.time
                info.code = two.code
                info.name = two.name
                info.dayCount = 2
                info.updateTime = two.time

                val id = BanShareDatabase.getInstance()?.getBanOneShareDao()?.insert(info)
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
        return "一板"
    }
}