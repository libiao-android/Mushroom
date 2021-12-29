package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.room.MineShareDatabase
import com.libiao.mushroom.room.MineShareInfo
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil
import com.libiao.mushroom.utils.LogUtil.i
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.Charset

class MineMode : BaseMode {

    companion object {
        const val KEY = "MineMode"
    }

    constructor(): super() {
    }
    constructor(showTime: Boolean): super(showTime) {
    }

    private val poolSet = HashSet<String?>()

    init {
        val mineShares = MineShareDatabase.getInstance()?.getMineShareDao()?.getMineShares()
        mineShares?.forEach {
            LogUtil.i(TAG, "getMineShares: ${it.code}")
            poolSet.add(it.code)
        }
    }

    override fun analysis(day: Int, shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = day - 3
        if(mDeviationValue >=  0) {
            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]
            if(poolSet.contains(one.code)) {
                i(TAG, "contains: ${one.code}")
                if(three.nowPrice < three.line_10) {
                    i(TAG, "delete: ${one.code}")
                    MineShareDatabase.getInstance()?.getMineShareDao()?.delete(one.code!!)
                }
            } else {
                if(zhangTing(one)
                    && two.minPrice > two.line_10
                    && three.minPrice > three.line_10
                ) {
                    poolSet.add(one.code!!)
                    val info = MineShareInfo()
                    info.time = one.time
                    info.code = one.code
                    info.name = one.name
                    info.price = one.nowPrice
                    MineShareDatabase.getInstance()?.getMineShareDao()?.insert(info)
                    i(TAG, "${three.brieflyInfo()}")
                    mFitModeList.add(Pair(three.range, three))
                }
            }

        }
    }

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - Constant.PRE
        if(Constant.PRE == 0) {
            analysis(mDeviationValue, shares)
        } else {
            i(TAG, "只记录当天")
        }
    }

    override fun des(): String {
        return "我的"
    }
}