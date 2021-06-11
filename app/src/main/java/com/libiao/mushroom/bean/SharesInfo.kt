package com.libiao.mushroom.bean

class SharesInfo {

    var name: String? = null
    var code: String? = null
    var marketValue: Double = 0.00

    var time: String? = null
    var openingPrice: Double = 0.00
    var closingPrice: Double = 0.00
    var highestPrice: Double = 0.00
    var minimumPrice: Double = 0.00
    var totalHands: Double = 0.00

    var range: Double = 0.00 //涨跌幅

    override fun toString(): String {
        return "股票代码：$code, 日期：${time}, 开盘价：${openingPrice}," +
                " 收盘价：${closingPrice}, 最高价：${highestPrice}" +
                ", 最低价：${minimumPrice}, 总手数：$totalHands, 涨跌幅：$range"
    }
}