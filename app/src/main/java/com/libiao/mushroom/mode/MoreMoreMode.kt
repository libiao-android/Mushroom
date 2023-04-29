package com.libiao.mushroom.mode

import android.content.Context
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.room.report.ReportShareDatabase
import com.libiao.mushroom.room.report.ReportShareInfo
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i
import kotlin.math.abs
import kotlin.math.min

class MoreMoreMode : BaseMode() {

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - 7 - Constant.PRE

        if(mDeviationValue >  0) {
            val zero = shares[mDeviationValue - 1]
            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]
            val four = shares[mDeviationValue + 3]
            val five = shares[mDeviationValue + 4]
            val six = shares[mDeviationValue + 5]
            val seven = shares[mDeviationValue + 6]
            var a = 0
            val list = ArrayList<SharesRecordActivity.ShareInfo>()

            list.add(one)
            list.add(two)
            list.add(three)
            list.add(four)
            list.add(five)
            list.add(six)
            list.add(seven)

            val indexList = ArrayList<Int>()
            val pList = ArrayList<Double>()

            if(yang(seven)) {
                indexList.add(7)
                pList.add(seven.totalPrice)
            } else {
                return
            }
            if(yang(six)) {
                indexList.add(6)
                pList.add(six.totalPrice)
            }
            if(yang(five)) {
                indexList.add(5)
                pList.add(five.totalPrice)
            }
            if(pList.size == 3) return

            if(yang(four)) {
                indexList.add(4)
                pList.add(four.totalPrice)

                if(pList.size == 3) {
                    val x = indexList[0] - indexList[1] > 1 && indexList[1] - indexList[2] > 1
                    val y = pList[0] > pList[1] * 1.1 && pList[1] > pList[2] * 1.1
                    val m = four.totalPrice > three.totalPrice
                    val n = four.totalPrice > five.totalPrice

                    val q = (list[indexList[0]-1].beginPrice + list[indexList[0]-1].nowPrice) / 2
                    val w = (list[indexList[1]-1].beginPrice + list[indexList[1]-1].nowPrice) / 2
                    val e = (list[indexList[2]-1].beginPrice + list[indexList[2]-1].nowPrice) / 2
                    val z = q > w && w > e

                    if(x && y && m && n && z) {
                        i(TAG, "${four.brieflyInfo()}")
                        mFitModeList.add(Pair(seven.range, seven))
                        record(four)
                    }
                    return
                }
            }



            if(yang(three)) {
                indexList.add(3)
                pList.add(three.totalPrice)

                if(pList.size == 3) {

                    val x = indexList[0] - indexList[1] > 1 && indexList[1] - indexList[2] > 1
                    val y = pList[0] > pList[1] * 1.1 && pList[1] > pList[2] * 1.1
                    val m = three.totalPrice > two.totalPrice

                    val q = (list[indexList[0]-1].beginPrice + list[indexList[0]-1].nowPrice) / 2
                    val w = (list[indexList[1]-1].beginPrice + list[indexList[1]-1].nowPrice) / 2
                    val e = (list[indexList[2]-1].beginPrice + list[indexList[2]-1].nowPrice) / 2
                    val z = q > w && w > e

                    if(x && y && m && z) {
                        var yangT = three
                        for(i in 4 .. 7) {
                            val t = list[i-1]
                            if(yang(t)) {
                                yangT = t
                            } else {
                                if(t.totalPrice > yangT.totalPrice) {
                                    return
                                }
                            }
                        }
                        i(TAG, "${three.brieflyInfo()}")
                        mFitModeList.add(Pair(seven.range, seven))
                        record(three)
                    }
                    return
                }
            }



            if(yang(two)) {
                indexList.add(2)
                pList.add(two.totalPrice)

                if(pList.size == 3) {

                    val x = indexList[0] - indexList[1] > 1 && indexList[1] - indexList[2] > 1
                    val y = pList[0] > pList[1] * 1.1 && pList[1] > pList[2] * 1.1
                    val m = two.totalPrice > one.totalPrice

                    val q = (list[indexList[0]-1].beginPrice + list[indexList[0]-1].nowPrice) / 2
                    val w = (list[indexList[1]-1].beginPrice + list[indexList[1]-1].nowPrice) / 2
                    val e = (list[indexList[2]-1].beginPrice + list[indexList[2]-1].nowPrice) / 2
                    val z = q > w && w > e

                    if(x && y && m && z) {
                        var yangT = two
                        for(i in 3 .. 7) {
                            val t = list[i-1]
                            if(yang(t)) {
                                yangT = t
                            } else {
                                if(t.totalPrice > yangT.totalPrice) {
                                    return
                                }
                            }
                        }
                        i(TAG, "${two.brieflyInfo()}")
                        mFitModeList.add(Pair(seven.range, seven))
                        record(two)
                    }


                    return
                }
            }



            if(yang(one)) {
                indexList.add(1)
                pList.add(one.totalPrice)

                if(pList.size == 3) {

                    val x = indexList[0] - indexList[1] > 1 && indexList[1] - indexList[2] > 1
                    val y = pList[0] > pList[1] * 1.1 && pList[1] > pList[2] * 1.1
                    val m = one.totalPrice > zero.totalPrice

                    val q = (list[indexList[0]-1].beginPrice + list[indexList[0]-1].nowPrice) / 2
                    val w = (list[indexList[1]-1].beginPrice + list[indexList[1]-1].nowPrice) / 2
                    val e = (list[indexList[2]-1].beginPrice + list[indexList[2]-1].nowPrice) / 2
                    val z = q > w && w > e

                    if(x && y && m && z) {
                        var yangT = one
                        for(i in 2 .. 7) {
                            val t = list[i-1]
                            if(yang(t)) {
                                yangT = t
                            } else {
                                if(t.totalPrice > yangT.totalPrice) {
                                    return
                                }
                            }
                        }
                        i(TAG, "${one.brieflyInfo()}")
                        mFitModeList.add(Pair(seven.range, seven))
                        record(one)
                    }

                    return
                }
            }
        }
    }

    private fun record(one: SharesRecordActivity.ShareInfo) {
        val info = ReportShareInfo()
        info.code = one.code
        info.time = one.time
        info.name = one.name
        info.updateTime = one.time
        info.ext5 = "3"
        ReportShareDatabase.getInstance()?.getReportShareDao()?.insert(info)
    }

    private fun yang(info: SharesRecordActivity.ShareInfo): Boolean {
        return info.range > 0 && info.nowPrice >= info.beginPrice
    }

    override fun shouldAnalysis(context: Context): Boolean {
        return false
    }

    override fun des(): String {
        return "连续放量"
    }
}