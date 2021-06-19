package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i
import kotlin.math.abs
import kotlin.math.min

class MoreMoreMode : BaseMode() {

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - 3 - Constant.PRE

        for(mDeviationValue in 1 .. size - 5) {

            if(mDeviationValue >  0) {

                val zero = shares[mDeviationValue - 1]
                val one = shares[mDeviationValue + 0]
                val two = shares[mDeviationValue + 1]
                val three = shares[mDeviationValue + 2]
                val four = shares[mDeviationValue + 3]
                val five = shares[mDeviationValue + 4]
                //val six = shares[mDeviationValue + 5]

                if(more(zero, one)) {


                    if(two.totalPrice > one.totalPrice * 1) { // 连续两天放量
                        if(two.nowPrice > (one.beginPrice + one.nowPrice)/2
                            && three.nowPrice > (one.beginPrice + one.nowPrice)/2
                            && two.maxPrice > one.nowPrice ) { //放量后价格不能低于放量前

                            if(two.beginPrice > two.nowPrice && three.beginPrice > three.nowPrice) { //连续两天收阴线
                                if(three.totalPrice > zero.totalPrice * 2 && three.totalPrice > 130000000 && three.totalPrice > one.totalPrice * 0.7) { //连续收阴量能不能太低
                                    if(three.totalPrice > zero.totalPrice * 3 || three.totalPrice * 2 > two.totalPrice) {
                                        i(TAG, "${three.brieflyInfo()}, ${one.range}, ${two.range}, ${three.range}, ${four.range}, ${five.range}")
                                        mFitModeList.add(Pair(three.range, three))
                                    }
                                }

                            }


                            if (two.beginPrice < two.nowPrice && three.beginPrice < three.nowPrice && two.range > 0 && three.range > 0) { //连续两天阳线
                                if(two.totalPrice > one.totalPrice * 1.2 && (three.totalPrice > one.totalPrice * 0.9 || three.totalPrice > 1000000000)) {
                                    if(three.totalCount < two.totalCount) {
                                        val a = (three.maxPrice - three.nowPrice) / (three.nowPrice - three.beginPrice)
                                        if(a < 5) {
                                            //if(isChuang(three.code))
                                            val beishu = one.totalPrice / zero.totalPrice
                                            if(beishu > 3.5 || one.totalPrice > 1000000000) {
                                                i(TAG, "${three.brieflyInfo()}, ${one.range}, ${two.range}, ${three.range}, ${four.range}, ${five.range}")
                                                mFitModeList.add(Pair(three.range, three))
                                            }
                                        }
                                    }
                                }
                            }

                            if (two.range > 0 && two.nowPrice > two.beginPrice && two.range > 5) { //阳阴阴
                                if(three.nowPrice < three.beginPrice && four.nowPrice < four.beginPrice && three.range < 0 && four.range < 0) {
                                    if(three.totalPrice > one.totalPrice * 0.7 && four.totalPrice > one.totalPrice * 0.7) {
                                        if(four.nowPrice > (one.beginPrice + one.nowPrice)/2) {
                                            i(TAG, "${three.brieflyInfo()}, ${one.range}, ${two.range}, ${three.range}, ${four.range}, ${five.range}")
                                            mFitModeList.add(Pair(three.range, three))
                                        }
                                    }

                                }
                            }

                            if(three.totalPrice > two.totalPrice && four.totalPrice > three.totalPrice) { //连续三天放量
                                i(TAG, "${three.brieflyInfo()}, ${one.range}, ${two.range}, ${three.range}, ${four.range}, ${five.range}")
                                mFitModeList.add(Pair(three.range, three))
                            }

                            if(two.range > 0 && three.range > 0 && four.range < 0 && five.range < 0) {
                                if(five.totalPrice < two.totalPrice * 0.8) {
                                    if(five.totalPrice > zero.totalPrice * 3 || (five.totalPrice > zero.totalPrice * 2.5 && five.totalPrice > 500000000)) {
                                        i(TAG, "${three.brieflyInfo()}, ${one.range}, ${two.range}, ${three.range}, ${four.range}, ${five.range}")
                                        mFitModeList.add(Pair(three.range, three))
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
        return "连续放量"
    }
}