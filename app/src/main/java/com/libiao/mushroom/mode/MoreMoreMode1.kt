package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i

class MoreMoreMode1 : BaseMode {

    constructor(): super() {
    }
    constructor(showTime: Boolean): super(showTime) {
    }

    override fun analysis(day: Int, shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = day - 3
        if(mDeviationValue >  0) {

            val zero = shares[mDeviationValue - 1]
            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]

            if(more(zero, one)) {
                if(two.totalPrice > one.totalPrice * 1) { // 连续两天放量
                    if(two.nowPrice > (one.beginPrice + one.nowPrice)/2
                        && three.nowPrice > (one.beginPrice + one.nowPrice) * 0.51
                        && two.maxPrice > one.nowPrice ) { //放量后价格不能低于放量前

                        if (two.beginPrice > two.nowPrice && three.beginPrice > three.nowPrice) { //连续两天收阴线
                            if (three.totalPrice > zero.totalPrice * 2 && three.totalPrice > 130000000 && three.totalPrice > one.totalPrice * 0.7) { //连续收阴量能不能太低
                                if (three.totalPrice > zero.totalPrice * 3 || three.totalPrice * 2 > two.totalPrice) {
                                    if(three.nowPrice > one.nowPrice) { //高位
                                        if(three.totalPrice < (one.totalPrice + two.totalPrice)/2) { //缩量
                                            if(three.maxPrice > two.maxPrice) { //创新高
                                                return
                                            }
                                        }
                                    }

                                    if(three.maxPrice < two.nowPrice) { //跳空低开低走
                                        return
                                    }

                                    val shangYinXianTwo = two.maxPrice - two.beginPrice > (two.beginPrice - two.nowPrice) * 2
                                    val shangYinXianThree = three.maxPrice - three.beginPrice > (three.beginPrice - three.nowPrice) * 2
                                    if(shangYinXianTwo && shangYinXianThree) {
                                        val m = three.beginPrice - three.nowPrice > (three.nowPrice - three.minPrice)
                                        if(m) { return }
                                    }
                                    i(TAG, "${three.brieflyInfo()}")


                                    if(mDeviationValue + 3 < size) {
                                        val post = shares[mDeviationValue + 3]
                                        three.postRange = post.range
                                        three.post1 = "${post.range}/${post.rangeBegin}/${post.rangeMin}/${post.rangeMax}"
                                    }
                                    if(mDeviationValue + 4 < size) {
                                        val post = shares[mDeviationValue + 4]
                                        three.post2 = "${post.range}/${post.rangeBegin}/${post.rangeMin}/${post.rangeMax}"
                                    }
                                    mFitModeList.add(
                                        Pair(
                                            three.range,
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

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - Constant.PRE

        analysis(mDeviationValue, shares)

    }

    override fun des(): String {
        return "放量后连续两天收阴"
    }
}