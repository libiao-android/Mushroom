package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.room.ban.BanShareDatabase
import com.libiao.mushroom.room.ban.twochuang.BanTwoChuangShareInfo
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i

class LianBanChuang2Mode : BaseMode {

    companion object {
        const val KEY = "LianBanChuang2Mode"
    }

    constructor(): super() {
    }
    constructor(showTime: Boolean): super(showTime) {
    }

    private val poolMap = HashMap<String, BanTwoChuangShareInfo>()

    init {
        val allShares = BanShareDatabase.getInstance()?.getBanTwoChuangShareDao()?.getAllShares()
        allShares?.forEach {
            //LogUtil.i(TAG, "getMineShares: ${it.code}")
            poolMap[it.code!!] = it
        }
        i(TAG, "init: ${poolMap.size}")
    }

    override fun analysis(day: Int, shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = day - 3
        if(mDeviationValue >=  0) {
            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]

            if(poolMap.contains(one.code)) {
                val info = poolMap[one.code]
                info?.also {

                    if(info.updateTime == three.time) {
                        i(TAG, "重复记录")
                    } else {
                        if(it.dayCount > 30) {
                            BanShareDatabase.getInstance()?.getBanTwoChuangShareDao()?.delete(it.code!!)
                        } else {
                            i(TAG, "更新记录")
                            it.updateTime = three.time
                            it.dayCount = it.dayCount + 1
                            BanShareDatabase.getInstance()?.getBanTwoChuangShareDao()?.update(it)
                        }
                    }
                }
                return
            }

            if(isChuang(one.code)
                && !zhangTing(one)
                && zhangTing(two)
                && zhangTing(three)
            ) {
                i(TAG, "${three.brieflyInfo()}")
                mFitModeList.add(Pair(three.range, three))

                val info = BanTwoChuangShareInfo()
                info.time = three.time
                info.code = three.code
                info.name = three.name
                info.dayCount = 3
                info.updateTime = three.time

                val id = BanShareDatabase.getInstance()?.getBanTwoChuangShareDao()?.insert(info)
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
        return "创业板二连板"
    }
}