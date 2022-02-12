package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.mine.fangliang.FangLiangLabel
import com.libiao.mushroom.room.FangLiangShareDatabase
import com.libiao.mushroom.room.FangLiangShareInfo
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil
import com.libiao.mushroom.utils.LogUtil.i

class MoreMore3Mode : BaseMode {

    companion object {
        const val KEY = "MoreMore3Mode"
    }

    constructor(): super() {
    }
    constructor(showTime: Boolean): super(showTime) {
    }

    private val poolMap = HashMap<String, FangLiangShareInfo>()

    init {
        val fangLiangShares = FangLiangShareDatabase.getInstance()?.getFangLiangShareDao()?.getFangLiangShares()
        fangLiangShares?.forEach {
            //LogUtil.i(TAG, "getMineShares: ${it.code}")
            poolMap[it.code!!] = it
        }
        i(TAG, "init: ${poolMap.size}")
    }

    override fun analysis(day: Int, shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = day - 15
        if(mDeviationValue >  0) {

            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]
            val four = shares[mDeviationValue + 3]
            val five = shares[mDeviationValue + 4]
            val six = shares[mDeviationValue + 5]
            val seven = shares[mDeviationValue + 6]
            val eight = shares[mDeviationValue + 7]
            val nine = shares[mDeviationValue + 8]
            val ten = shares[mDeviationValue + 9]

            val ten1 = shares[mDeviationValue + 10]
            val ten2 = shares[mDeviationValue + 11]
            val ten3 = shares[mDeviationValue + 12]
            val ten4 = shares[mDeviationValue + 13]
            val ten5 = shares[mDeviationValue + 14]

            if(poolMap.contains(one.code)) {
                val info = poolMap[one.code]
                info?.also {

                    if(info.updateTime == ten5.time) {
                        i(TAG, "重复记录")
                    } else {
                        i(TAG, "更新记录")
                        it.updateTime = ten5.time
                        it.dayCount = it.dayCount + 1
                        FangLiangShareDatabase.getInstance()?.getFangLiangShareDao()?.update(it)
                    }
                }
                return
            }

            val preAvg = (one.totalPrice
                    + two.totalPrice
                    + three.totalPrice
                    + four.totalPrice
                    + five.totalPrice
                    + six.totalPrice
                    + seven.totalPrice
                    + eight.totalPrice
                    + nine.totalPrice
                    + ten.totalPrice
                    ) / 10

            val beiShu = 2.9

            var moreAvg = preAvg * beiShu
            if(moreAvg < 100000000.00) {
                moreAvg = 100000000.00
            }

            if(moreAvg > 0
                && (ten1.totalPrice > moreAvg || zhangTing(ten1))
                && (ten2.totalPrice > moreAvg)
                && ten3.totalPrice > moreAvg
                && ten4.totalPrice > moreAvg
                && ten5.totalPrice > moreAvg
            ) {
                val info = FangLiangShareInfo()
                info.time = ten5.time
                info.code = ten5.code
                info.name = ten5.name
                info.beginPrice = ten1.beginPrice
                info.preAvg = preAvg
                info.dayCount = 15
                info.updateTime = ten5.time

                if(zhangTing(ten1)) {
                    info.label1 = FangLiangLabel.ZHANG_TING
                }

                if((ten3.totalPrice > ten4.totalPrice && ten4.totalPrice > ten5.totalPrice)) {
                    info.label2 = FangLiangLabel.LESS_PRICE
                }
                if(ten5.nowPrice < ten1.beginPrice ) {
                    info.label3 = FangLiangLabel.MORE_BELOW
                }
                var redLine = 0
                if(ten1.nowPrice >= ten1.beginPrice) redLine++
                if(ten2.nowPrice >= ten2.beginPrice) redLine++
                if(ten3.nowPrice >= ten3.beginPrice) redLine++
                if(ten4.nowPrice >= ten4.beginPrice) redLine++
                if(ten5.nowPrice >= ten5.beginPrice) redLine++

                if(redLine < 3) {
                    info.label4 = FangLiangLabel.LESS_RED
                }

                var max = ten1.totalPrice
                var min = ten1.totalPrice
                if(zhangTing(ten1)) min = ten2.totalPrice
                if(ten2.totalPrice > max) max = ten2.totalPrice
                if(ten3.totalPrice > max) max = ten3.totalPrice
                if(ten4.totalPrice > max) max = ten4.totalPrice
                if(ten5.totalPrice > max) max = ten5.totalPrice
                if(max == ten1.totalPrice) {
                    info.label5 = FangLiangLabel.FIRST_PRICE
                }


                val id = FangLiangShareDatabase.getInstance()?.getFangLiangShareDao()?.insert(info)
                info.id = id?.toInt() ?: 0
                poolMap[one.code!!] = info

                i(TAG, "${ten5.brieflyInfo()}, $preAvg")
                ten5.post1 = "${String.format("%.2f",preAvg / 100000000)}亿"
                //ten5.post2 = String.format("%.1f",(ten1.range + ten2.range + ten3.range + ten4.range + ten5.range))
                //ten5.post2 = baoLiuXiaoShu(liangBi)
                mFitModeList.add(Pair(ten5.range, ten5))
                poolList.add(one.code!!)
            }
        }
    }

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - Constant.PRE

        //analysis(mDeviationValue, shares)

        if(Constant.PRE == 0) {
            analysis(mDeviationValue, shares)
        } else {
            i(TAG, "只记录当天")
        }
    }

    override fun des(): String {
        return "连续五天放量"
    }
}
//失败的量能
//金字塔型量能603803