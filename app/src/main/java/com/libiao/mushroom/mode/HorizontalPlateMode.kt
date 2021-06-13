package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.LogUtil.i
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class HorizontalPlateMode : BaseMode() {

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - 4
        if(mDeviationValue >  0) {
            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]
            val four = shares[mDeviationValue + 3]


            var middleOne = (one.beginPrice + one.nowPrice) / 2

            var middletwo = (two.beginPrice + two.nowPrice) / 2

            var middleThree = (three.beginPrice + three.nowPrice) / 2

            var middleFour = (four.beginPrice + four.nowPrice) / 2

            if(four.nowPrice > 30) {
                middleOne = String.format("%.1f",middleOne).toDouble()
                middletwo = String.format("%.1f",middletwo).toDouble()
                middleThree = String.format("%.1f",middleThree).toDouble()
                middleFour = String.format("%.1f",middleFour).toDouble()
            }
            var totalPriceMin = min(one.totalPrice, two.totalPrice)
            totalPriceMin = min(totalPriceMin, three.totalPrice)
            totalPriceMin = min(totalPriceMin, four.totalPrice)
            //val incremental = (middleOne < middletwo && middletwo < middleThree) || (middletwo < middleThree && middleThree < middleFour)
            val incremental = false
            val decreasing = (middleOne > middletwo && middletwo > middleThree) || (middletwo > middleThree && middleThree > middleFour)

            if(totalPriceMin > 100000000 && four.nowPrice > 10 && !incremental && !decreasing) {

                var middleMax = max(middleOne, middletwo)
                middleMax = max(middleMax, middleThree)
                middleMax = max(middleMax, middleFour)

                var middleMin = min(middleOne, middletwo)
                middleMin = min(middleMin, middleThree)
                middleMin = min(middleMin, middleFour)

                if((middleMax - middleMin) / middleMin < 0.05) {
                    if(abs(one.range) < 1 && abs(two.range) < 1 && abs(three.range) < 1 && abs(four.range) < 1) {
                        i(TAG, "震荡模式：${shares.last().brieflyInfo()}")
                        mFitModeList.add(Pair(four.zongShiZhi, four))
                    }
                }
            }
        }
    }

    override fun des(): String {
        return "平盘震荡"
    }

}