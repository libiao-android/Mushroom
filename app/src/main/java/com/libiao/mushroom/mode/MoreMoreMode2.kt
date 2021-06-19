package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i
import kotlin.math.abs
import kotlin.math.min

class MoreMoreMode2 : BaseMode() {

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

                        if (two.beginPrice < two.nowPrice && three.beginPrice < three.nowPrice && two.range > 0 && three.range > 0) { //连续两天阳线
                            if(two.totalPrice > one.totalPrice * 1.2 && (three.totalPrice > one.totalPrice * 0.9 || three.totalPrice > 1000000000)) {
                                if(three.totalCount < two.totalCount) {
                                    val a = (three.maxPrice - three.nowPrice) / (three.nowPrice - three.beginPrice)
                                    if(a < 5) {
                                        val beishu = one.totalPrice / zero.totalPrice
                                        if(beishu > 3.5 || one.totalPrice > 1000000000) {
                                            i(TAG, "${three.brieflyInfo()}, ${one.range}, ${two.range}, ${three.range}")
                                            if(mDeviationValue + 3 < size) {
                                                val post = shares[mDeviationValue + 3]
                                                three.postRange = post.range
                                            }
                                            mFitModeList.add(Pair(one.range + two.range + three.range, three))
                                        }
                                    }
                                }
                            }
                        }
                    }

                }

            }
        }
    }

    override fun des(): String {
        return "放量后连续两天收阳"
    }
}