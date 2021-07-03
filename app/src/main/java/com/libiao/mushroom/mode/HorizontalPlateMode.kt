package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i
import kotlin.math.max
import kotlin.math.min

class HorizontalPlateMode : BaseMode() {

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        //i(TAG, "$size")
        mDeviationValue = size - 5 - Constant.PRE
        if(mDeviationValue >= 0) {
            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]
            val four = shares[mDeviationValue + 3]
            val five = shares[mDeviationValue + 4]


            var middleOne = (one.beginPrice + one.nowPrice) / 2

            var middletwo = (two.beginPrice + two.nowPrice) / 2

            var middleThree = (three.beginPrice + three.nowPrice) / 2

            var middleFour = (four.beginPrice + four.nowPrice) / 2

            var middleFive = (five.beginPrice + five.nowPrice) / 2

            var totalPriceMin = min(one.totalPrice, two.totalPrice)
            totalPriceMin = min(totalPriceMin, three.totalPrice)
            totalPriceMin = min(totalPriceMin, four.totalPrice)
            totalPriceMin = min(totalPriceMin, five.totalPrice)
            //val incremental = (middleOne < middletwo && middletwo < middleThree) || (middletwo < middleThree && middleThree < middleFour)
            val incremental = false
            //val decreasing = (middleOne > middletwo && middletwo > middleThree) || (middletwo > middleThree && middleThree > middleFour)

            if(totalPriceMin > 100000000) {

                var middleMax = max(middleOne, middletwo)
                middleMax = max(middleMax, middleThree)
                middleMax = max(middleMax, middleFour)
                middleMax = max(middleMax, middleFive)

                var middleMin = min(middleOne, middletwo)
                middleMin = min(middleMin, middleThree)
                middleMin = min(middleMin, middleFour)
                middleMin = min(middleMin, middleFive)

                val a = (middleMax - middleMin) / middleMin

                //i(TAG, "${one.brieflyInfo()}, $middleMin, $middleMax, $a")

                if(a < getValue(five.nowPrice)) {
                    i(TAG, "震荡模式：$a, ${one.brieflyInfo()}, ${five.nowPrice}")
                    if(mDeviationValue + 5 < size) {
                        val six = shares[mDeviationValue + 5]
                        five.postRange = six.range
                    }
                    mFitModeList.add(Pair(a, five))
                }
            }
        }
    }

    private fun getValue(nowPrice: Double): Double {
        if(nowPrice > 150) return 0.0099
        if(nowPrice > 100) return 0.005
        if(nowPrice > 50) return 0.004
        return 0.002
    }

    override fun des(): String {
        return "平盘震荡"
    }

}