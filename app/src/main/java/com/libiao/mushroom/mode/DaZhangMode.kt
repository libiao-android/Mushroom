package com.libiao.mushroom.mode

import android.content.Context
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.room.TestShareDatabase
import com.libiao.mushroom.room.TestShareInfo
import com.libiao.mushroom.room.ban.BanShareDatabase
import com.libiao.mushroom.room.ban.one.BanOneShareInfo
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i

class DaZhangMode : BaseMode() {

    companion object {
        const val KEY = "DaZhangMode"
    }

    override fun analysis(day: Int, shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = day - 1
        if(mDeviationValue >=  0) {
            val one = shares[mDeviationValue + 0]

            if(one.range >= 9) {
                i(TAG, "${one.brieflyInfo()}")
                mFitModeList.add(Pair(one.range, one))
                val info = TestShareInfo()
                info.code = one.code
                info.time = one.time
                info.name = one.name
                info.updateTime = one.time
                info.ext5 = "12"
                TestShareDatabase.getInstance()?.getTestShareDao()?.insert(info)
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
        return "大涨"
    }
}