package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i

class MoreMoreMode4 : BaseMode {

    constructor(): super() {
    }
    constructor(showTime: Boolean): super(showTime) {
    }

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - 2 - Constant.PRE

        if(mDeviationValue > 1) {

            var fuYi = shares[mDeviationValue - 2]
            val zero = shares[mDeviationValue - 1]
            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]


            if(fuYi.rangeMin == fuYi.rangeMax) {
                if(zero.rangeMin == zero.rangeMax) {
                    return
                }
            }
            var beiShu = 4.0
            val avgMorePrice = (one.totalPrice + two.totalPrice) / 2
            if(avgMorePrice < 500000000) return
            if(avgMorePrice > 2000000000) {
                beiShu = 2.0
            } else if(avgMorePrice > 1000000000) {
                beiShu = 2.5
            } else if(avgMorePrice > 500000000) {
                beiShu = 3.0
            } else if(avgMorePrice > 100000000) {
                beiShu = 3.5
            }

            val avgZeroPrice = zero.totalPrice
            if(one.totalPrice > avgZeroPrice * (beiShu - 0.5) && avgMorePrice > avgZeroPrice * beiShu) {
                if(one.range > 0 && two.range > 0) {

                    if(two.nowPrice >= two.beginPrice && two.rangeMax - two.range < 3.5) {
                        if(one.range + two.range > 9.0) {
                            var twoFit = false
                            if(two.code?.startsWith("sz300") == true) {
                                if(two.range > 19) {
                                    twoFit = true
                                }
                            } else {
                                if(two.range > 9) {
                                    twoFit = true
                                }
                            }
                            if(two.totalPrice > one.totalPrice * 0.9) {
                                twoFit = true
                            }

                            if(twoFit) {
                                val a = avgMorePrice / avgZeroPrice
                                val zengLiang = (avgMorePrice - avgZeroPrice) / 100000000
                                if(zengLiang > 15) {

                                    i(TAG, "$mDeviationValue, $size")
                                    if(mDeviationValue + 4 <= size) {
                                        val three = shares[mDeviationValue + 2]
                                        val four = shares[mDeviationValue + 3]
                                        two.postRange = String.format("%.2f",three.range + four.range).toDouble()
                                    } else if(mDeviationValue + 3 <= size) {
                                        val three = shares[mDeviationValue + 2]
                                        two.postRange = String.format("%.2f",three.range).toDouble()
                                    }

                                    i(TAG, "${two.brieflyInfo()}, ${one.range}, ${two.range}, $zengLiang, $a")
                                    mFitModeList.add(Pair(zengLiang, two))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun des(): String {
        return "连续两天放量"
    }
}

//2021.02
//两天未涨停
//600570 量能没有明显放大，竞价买赚钱
//300033 第一天涨17个点，量能明显放大，缩量回调3日后突破
//000951 亏钱
//300003 高开涨，后一天跌，量能维持
//002385 亏钱


//第一天涨停
//002493 量能没有明显放大，缩量回调3日后突破
//002191 第二天较长上引线，亏钱
//002249 第三天缩量回调，后面突破
//600315 赚钱
//000963 高开放量涨停
//000878 高开放量涨停
//000021 缩量冲高回落，亏钱
//002157 缩量跌，亏
//603000 缩量跌，平


//第一天阴线
//002360 第二天涨停，前三天涨停，巨亏
//003035 第二天涨停，前n天涨停，巨亏
//300896 第二天涨停，前无涨停，低开继续放量突破
//600711 第二天没有涨停，第三天高开放量涨停被砸
//601899 第二天没有涨停，前一天一字涨停，巨亏

//第二天涨停
//600908 回调后突破
//000538 放量突破
//600366 回调3日后有新高



//两天涨停
//002230 回调3日后突破
//601225 第三天放量亏钱
//002124 缩量冲高回落亏钱
//002221 缩量冲高回落亏钱
//600010 回调后突破

//第二天量能低于第一天
//000977 平
//000069 平
//600316 平

