package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i
import kotlin.math.abs
import kotlin.math.min

class MoreMoreMode4 : BaseMode() {

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - 4 - Constant.PRE

        if(mDeviationValue >  0) {

            val zero = shares[mDeviationValue - 1]
            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]
            val four = shares[mDeviationValue + 3]

            if(more(zero, one)) {
                if(two.totalPrice > one.totalPrice * 1) { // 连续两天放量
                    if(two.nowPrice > (one.beginPrice + one.nowPrice)/2
                        && three.nowPrice > (one.beginPrice + one.nowPrice)/2
                        && two.maxPrice > one.nowPrice ) { //放量后价格不能低于放量前
                        if(three.totalPrice > two.totalPrice && four.totalPrice > three.totalPrice) { //连续三天放量
                            i(TAG, "${three.brieflyInfo()}, ${one.range}, ${two.range}, ${three.range}, ${four.range}")
                            if(mDeviationValue + 4 < size) {
                                val post = shares[mDeviationValue + 4]
                                four.postRange = post.range
                            }
                            mFitModeList.add(Pair(one.range + two.range + three.range + four.range, four))
                        }

                    }

                }
            }
        }
    }

    override fun des(): String {
        return "连续三天放量"
    }
}