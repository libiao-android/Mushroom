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
    }
}