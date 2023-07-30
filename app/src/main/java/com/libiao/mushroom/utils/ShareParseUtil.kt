package com.libiao.mushroom.utils

import com.libiao.mushroom.SharesRecordActivity

class ShareParseUtil {

    companion object {

        fun parseFromNetwork(code: String, values: List<String>): SharesRecordActivity.ShareInfo {
            val info = SharesRecordActivity.ShareInfo()
            values.also {
                info.code = code
                info.name = it[1]
                info.nowPrice = it[3].toDouble()
                info.yesterdayPrice = it[4].toDouble()
                info.beginPrice = it[5].toDouble()
                info.range = it[32].toDouble()
                info.maxPrice = it[33].toDouble()
                info.minPrice = it[34].toDouble()
                info.totalCount = it[36].toInt()
                info.totalPrice = it[37].toDouble() * 10000
                info.huanShouLv = it[38].toDouble()
                info.liuTongShiZhi = it[44].toDouble()
                info.zongShiZhi = it[45].toDouble()

                if(info.beginPrice > 0) {
                    val rangeBegin = String.format("%.2f", (info.beginPrice - info.yesterdayPrice) / info.yesterdayPrice * 100)
                    val rangeMin = String.format("%.2f", (info.minPrice - info.yesterdayPrice) / info.yesterdayPrice * 100)
                    val rangeMax = String.format("%.2f", (info.maxPrice - info.yesterdayPrice) / info.yesterdayPrice * 100)

                    info.rangeBegin = rangeBegin.toDouble()
                    info.rangeMin = rangeMin.toDouble()
                    info.rangeMax = rangeMax.toDouble()
                }
            }
            return info
        }

        fun isFangLiang(one: SharesRecordActivity.ShareInfo,
                        two: SharesRecordActivity.ShareInfo,
                        three: SharesRecordActivity.ShareInfo,
                        four: SharesRecordActivity.ShareInfo
                        ): Boolean {
            val preAvg = one.totalPrice

            var beiShu = 4.0
            if(preAvg > 2000000000) {
                beiShu = 2.0
            } else if(preAvg > 1000000000) {
                beiShu = 2.5
            } else if(preAvg > 500000000) {
                beiShu = 3.0
            } else if(preAvg > 100000000) {
                beiShu = 3.5
            }

            val moreAvg = preAvg * beiShu

            if(moreAvg > 0 && two.totalPrice > moreAvg && three.totalPrice > moreAvg && four.totalPrice > moreAvg) {

                if(four.totalPrice > 100000000) {
                    if(three.totalPrice > two.totalPrice * 0.75) {
                        if(four.totalPrice > three.totalPrice * 0.75) {
                            return true
                        }
                    }
                }
            }
            return false
        }

        fun dieTing(info: SharesRecordActivity.ShareInfo): Boolean {
            var maxRange = 0.9
            if(info.code?.startsWith("sz3") == true) {
                maxRange = 0.8
            }
            var dieTing = info.yesterdayPrice * maxRange
            dieTing = String.format("%.2f",dieTing).toDouble()
            return info.nowPrice > 0 && info.nowPrice == dieTing
        }

        fun zhangTing(info: SharesRecordActivity.ShareInfo): Boolean {
            var maxRange = 1.1
            if(info.code?.startsWith("sz3") == true || info.code?.startsWith("sh68") == true) {
                maxRange = 1.2
            }
            var zhangTingPrice = info.yesterdayPrice * maxRange - 0.005
            zhangTingPrice = String.format("%.2f",zhangTingPrice).toDouble()
            return info.nowPrice > 0 && info.nowPrice >= zhangTingPrice && zhangTingPrice > 0
        }

        fun zhangTingMax(info: SharesRecordActivity.ShareInfo): Boolean {
            var maxRange = 1.1
            if(info.code?.startsWith("sz3") == true || info.code?.startsWith("sh68") == true) {
                maxRange = 1.2
            }
            var zhangTingPrice = info.yesterdayPrice * maxRange - 0.005
            zhangTingPrice = String.format("%.2f",zhangTingPrice).toDouble()
            return info.maxPrice > 0 && info.maxPrice >= zhangTingPrice && zhangTingPrice > 0
        }

        fun maxZhangTing(info: SharesRecordActivity.ShareInfo): Boolean {
            var maxRange = 1.1
            if(info.code?.startsWith("sz3") == true || info.code?.startsWith("sh68") == true) {
                maxRange = 1.2
            }
            var zhangTingPrice = info.yesterdayPrice * maxRange - 0.005
            zhangTingPrice = String.format("%.2f",zhangTingPrice).toDouble()
            return info.maxPrice > 0 && info.maxPrice >= zhangTingPrice && zhangTingPrice > 0
        }
    }
}