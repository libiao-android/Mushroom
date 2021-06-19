package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i

class MoreMoreMode5 : BaseMode() {

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - 5 - Constant.PRE

        if(mDeviationValue >  0) {

            val zero = shares[mDeviationValue - 1]
            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]
            val four = shares[mDeviationValue + 3]
            val five = shares[mDeviationValue + 4]

            if(more(zero, one)) {
                if(two.totalPrice > one.totalPrice * 1) { // 连续两天放量
                    if(two.nowPrice > (one.beginPrice + one.nowPrice)/2
                        && three.nowPrice > (one.beginPrice + one.nowPrice)/2
                        && two.maxPrice > one.nowPrice ) { //放量后价格不能低于放量前

                        if(two.range > 0 && three.range > 0 && four.range < 0 && five.range < 0) {
                            if(five.totalPrice < two.totalPrice * 0.8) {
                                if(five.totalPrice > zero.totalPrice * 3 || (five.totalPrice > zero.totalPrice * 2.5 && five.totalPrice > 500000000)) {
                                    i(TAG, "${three.brieflyInfo()}, ${one.range}, ${two.range}, ${three.range}, ${four.range}, ${five.range}")
                                    if(mDeviationValue + 5 < size) {
                                        val post = shares[mDeviationValue + 5]
                                        five.postRange = post.range
                                    }
                                    mFitModeList.add(Pair(one.range + two.range + three.range + four.range + five.range, five))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun des(): String {
        return "放量后阳阳阴阴"
    }
}