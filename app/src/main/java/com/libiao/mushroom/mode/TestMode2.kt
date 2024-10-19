package com.libiao.mushroom.mode

import android.content.Context
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.room.TestShareDatabase
import com.libiao.mushroom.room.TestShareInfo
import com.libiao.mushroom.room.test.TestShareDatabase2
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil
import com.libiao.mushroom.utils.LogUtil.i
import kotlin.math.max

class TestMode2 : BaseMode {

    constructor(): super() {
    }
    constructor(showTime: Boolean): super(showTime) {
    }

    private val poolMap = HashMap<String, TestShareInfo>()

    init {
//        val fangLiangShares = TestShareDatabase2.getInstance()?.getTestShareDao()?.getShares()
//        fangLiangShares?.forEach {
//            //LogUtil.i(TAG, "getMineShares: ${it.code}")
//            poolMap[it.code!!] = it
//        }
//        i(TAG, "init: ${poolMap.size}")
    }


    override fun analysis(day: Int, shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = day - 10
        if(mDeviationValue >  0) {

            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]
            val four = shares[mDeviationValue + 3]
            val five = shares[mDeviationValue + 4]
            val six = shares[mDeviationValue + 5]
            val seven = shares[mDeviationValue + 6]
            val eight = shares[mDeviationValue + 7]
            val nine = shares[mDeviationValue + 8]
            val ten = shares[mDeviationValue + 9]

            if (ten.range < 0 || nine.range < 0) return

            val list = mutableListOf<SharesRecordActivity.ShareInfo>()
            val list2 = mutableListOf<SharesRecordActivity.ShareInfo>()
            list.add(one)
            list.add(two)
            list.add(three)
            list.add(four)
            list.add(five)
            list.add(six)
            list.add(seven)
            list.add(eight)
            list.add(nine)
            list.add(ten)

            list2.add(one)
            list2.add(two)
            list2.add(three)
            list2.add(four)
            list2.add(five)
            list2.add(six)
            list2.add(seven)
            list2.add(eight)
            list2.add(nine)
            list2.add(ten)


            list.sortByDescending { it.maxPrice }

            if (list[0].time == ten.time && list[1].time != nine.time && list[1].time != eight.time) {
                val firstH = 9
                var secondH = 0
                var min = 0

                list2.forEachIndexed { index, shareInfo ->
                    if (shareInfo.time == list[9].time) {
                        min = index
                    }
                    if (shareInfo.time == list[1].time) {
                        secondH = index
                    }
                }

                if (secondH > min) {
                    val list3 = mutableListOf<SharesRecordActivity.ShareInfo>()
                    for(m in secondH until list2.size) {
                        list3.add(list2[m])
                    }

                    list3.sortBy { it.maxPrice }

                    var secondMin = 0


                    list2.forEachIndexed { a, shareInfo ->
                        if (shareInfo.time == list3[0].time) {
                            secondMin = a
                            i(TAG, "secondMin: $secondMin, index: $a")
                        }
                    }

                    var range = 0.00
                    for( i in secondH + 1 .. secondMin) {
                        range += list2[i].range
                        if (list2[i].range > 0) return
                    }

                    for( i in secondMin + 1 .. firstH) {
                        if (list2[i].range < 0) return
                    }

                    if (range > -5) return


                    var realMinIndex = 0
                    var rang1 = 0.00

                    for( i in secondH downTo  0) {
                        if(list2[i].range < 0) break
                        realMinIndex = i
                        rang1 += list2[i].range

                    }

                    if (secondH - realMinIndex >= 1 && rang1 > 10) {
                        val time = secondMin - secondH

                        ten.post1 = baoLiuXiaoShu(range)
                        ten.post2 = baoLiuXiaoShu(time.toDouble())
                        mFitModeList.add(Pair(ten.range, ten))

                        i(TAG, "${ten.name}, min: $min, secondH: $secondH, secondMin: $secondMin, time: $time, range: $range")
                    }
                }
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
        return "test2"
    }

    private fun yin(two: SharesRecordActivity.ShareInfo): Boolean {
        return two.beginPrice > two.nowPrice
    }

    private fun yang(two: SharesRecordActivity.ShareInfo): Boolean {
        return two.nowPrice > two.beginPrice && two.range > 0
    }

}