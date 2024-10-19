package com.libiao.mushroom.test

import android.content.Context
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.mode.BaseMode
import com.libiao.mushroom.room.TestOneShareDatabase
import com.libiao.mushroom.room.TestOneShareInfo
import com.libiao.mushroom.room.TestShareDatabase
import com.libiao.mushroom.room.TestShareInfo
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil

class TestTwoMode : BaseMode {

    constructor(): super() {
    }
    constructor(showTime: Boolean): super(showTime) {
    }

    init {

    }

    private val poolMap = HashMap<String, TestOneShareInfo>()

    init {

    }


    override fun analysis(day: Int, shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = day - 20
        if(mDeviationValue >= 0) {
            val last = shares[mDeviationValue + 19]

            if (last.code?.startsWith("sh688") == true || last.name!!.contains("ST")) return

            val last2 = shares[mDeviationValue + 18]
            val list = mutableListOf<SharesRecordActivity.ShareInfo>()
            val list2 = mutableListOf<SharesRecordActivity.ShareInfo>()
            for(i in 0 until 20) {
               // LogUtil.i(TAG, "$i")
                list.add(shares[mDeviationValue + i])
                list2.add(shares[mDeviationValue + i])
            }

            list.sortBy { it.maxPrice }

            val maxP = list[list.size - 1].maxPrice

            list.sortBy { it.minPrice }

            val minP = list[0].minPrice


            val maxRange = (maxP - minP) / minP * 100

            if (maxRange > 15 && maxRange < 30) {
                val avgP = (maxP - minP) / 8
                val gaoList = mutableListOf<Int>()
                val diList = mutableListOf<Int>()
                var a = false
                var b = false
                var c = false
                var d = false
                var minPIndex = 0
                list2.forEachIndexed{ index, info ->
                    if (info.minPrice == minP) {
                        minPIndex = index
                    }
                    if (info.minPrice <= minP + avgP) {
                        diList.add(index)

                    }
                    if (info.maxPrice >= minP + avgP * 7) {
                        gaoList.add(index)

                    }
                }
                if (diList.size >=2 && gaoList.size >= 2) {
                    val a1 = diList[0]
                    val a2 = diList[diList.size - 1]

                    val b1 = gaoList[0]
                    val b2 = gaoList[gaoList.size - 1]

                    if (b1 > a1 && a2 > b1 && b2 > a2) {
                        LogUtil.i(TAG, "${last.brieflyInfo()}")
                        mFitModeList.add(Pair(last.range, last))
                        val info = TestShareInfo()
                        info.time = last.time
                        info.code = last.code
                        info.name = last.name
                        info.dayCount = 20
                        info.updateTime = last.time
                        info.ext5 = "333"
                        TestShareDatabase.getInstance()?.getTestShareDao()?.insert(info)
                    }
                }


//                val e = last.minPrice <= minP + avgP && last.nowPrice >= last.beginPrice
//                val f = minPIndex < 10
//
//                if (a && b && c && d && e && f) {
//
//                }
            }

        }
    }

    private fun yinXian(two: SharesRecordActivity.ShareInfo): Boolean {
        return two.range < 0 && two.beginPrice > two.nowPrice
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