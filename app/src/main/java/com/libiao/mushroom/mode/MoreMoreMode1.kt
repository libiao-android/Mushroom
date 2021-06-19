package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i
import kotlin.math.abs
import kotlin.math.min

class MoreMoreMode1 : BaseMode() {

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - 3 - Constant.PRE
        if(mDeviationValue >  0) {

            val zero = shares[mDeviationValue - 1]
            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]

            if(more(zero, one)) {
                if(two.totalPrice > one.totalPrice * 1) { // 连续两天放量
                    if(two.nowPrice > (one.beginPrice + one.nowPrice)/2
                        && three.nowPrice > (one.beginPrice + one.nowPrice)/2
                        && two.maxPrice > one.nowPrice ) { //放量后价格不能低于放量前

                        if (two.beginPrice > two.nowPrice && three.beginPrice > three.nowPrice) { //连续两天收阴线
                            if (three.totalPrice > zero.totalPrice * 2 && three.totalPrice > 130000000 && three.totalPrice > one.totalPrice * 0.7) { //连续收阴量能不能太低
                                if (three.totalPrice > zero.totalPrice * 3 || three.totalPrice * 2 > two.totalPrice) {
                                    i(
                                        TAG,
                                        "${three.brieflyInfo()}, ${one.range}, ${two.range}, ${three.range}"
                                    )
                                    if(mDeviationValue + 3 < size) {
                                        val post = shares[mDeviationValue + 3]
                                        three.postRange = post.range
                                    }
                                    mFitModeList.add(
                                        Pair(
                                            one.range + two.range + three.range,
                                            three
                                        )
                                    )
                                }
                            }

                        }

                    }

                }

            }
        }
    }

    override fun des(): String {
        return "放量后连续两天收阴"
    }
}