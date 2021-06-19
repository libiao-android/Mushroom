package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i

class MoreMoreMode3 : BaseMode() {

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
                        if (two.range > 0 && two.nowPrice > two.beginPrice && two.range > 5) { //阳阴阴
                            if(three.nowPrice < three.beginPrice && four.nowPrice < four.beginPrice && three.range < 0 && four.range < 0) {
                                if(three.totalPrice > one.totalPrice * 0.7 && four.totalPrice > one.totalPrice * 0.7) {
                                    if(four.nowPrice > (one.beginPrice + one.nowPrice)/2) {
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

            }
        }
    }

    override fun des(): String {
        return "放量后阳阴阴"
    }
}