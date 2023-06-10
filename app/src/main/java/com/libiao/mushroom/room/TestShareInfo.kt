package com.libiao.mushroom.room

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.libiao.mushroom.SharesRecordActivity
import java.util.ArrayList

@Entity
data class TestShareInfo(
    @PrimaryKey(autoGenerate = true)//主键是否自动增长，默认为false
    var id: Int = 0,

    var time: String? = null,
    var code: String? = null,
    var name: String? = null,
    var beginPrice: Double = 0.00,
    var preAvg: Double = 0.00, //平均量能
    var dayCount: Int = 0,
    var startIndex: Int = 0,
    var updateTime: String? = null,
    var result: Int = 0, // 0 初始  1 成功  2失败
    var collect: Int = 0, // 0 未收藏  1 收藏
    var maxPrice: Double = 0.00,
    var maxCount: Int = 0,
    var label1: String? = null,
    var label2: String? = null,
    var label3: String? = null,
    var label4: String? = null,
    var label5: String? = null,
    var label6: String? = null,
    var label7: String? = null,
    var label8: String? = null,
    var label9: String? = null,
    var label10: String? = null,
    var ext1: String? = null,
    var ext2: String? = null,
    var ext3: String? = null,
    var ext4: String? = null, //间隔天数
    var ext5: String? = null, //类型 1新高 2新量 3新高two 4新低 5主升 6记录突破
    @Ignore
    var moreInfo: String? = null,

    @Ignore
    var expand: Boolean = true,

    @Ignore
    var count: String = "0",

    @Ignore
    var currentCb: Int = 1,

    @Ignore
    var next: Boolean = false,
    @Ignore
    var todayRange: Double = 0.00, //今日涨幅

    @Ignore
    var price: Double = 1.00, //开始的记录价格

    @Ignore
    var range: Double = 0.00, //开始的记录zhangfu

    @Ignore
    var tuPo: Boolean = false, //站稳新高

    @Ignore
    var xinGaoList: ArrayList<String>? = null,

    @Ignore
    var xinGaoIndexList: ArrayList<Int>? = null,

    @Ignore
    var maxPriceH: Double = 0.00,

    @Ignore
    var maxNowPriceH: Double= 0.00,

    @Ignore
    var totalRange: Double = 0.00, //记录后至今日涨幅

    @Ignore
    var todayMaxRange: Double = 0.00, //今日最大涨幅

    @Ignore
    var todayLiang: Double = 0.00, //今日量能

    @Ignore
    var liangBi: Double = 0.00,

    @Ignore
    var index: Int = 0,

    @Ignore
    var zuiDaLiangCount: Int = 0,

    @Ignore
    var candleEntryList: ArrayList<CandleEntry>? = null,
    @Ignore
    var barEntryList: ArrayList<BarEntry>? = null,
    @Ignore
    var colorsList: ArrayList<Int>? = null,
    @Ignore
    var values_5: ArrayList<Entry>? = null,
    @Ignore
    var values_10: ArrayList<Entry>? = null,
    @Ignore
    var values_20: ArrayList<Entry>? = null,
    @Ignore
    var lastShareInfo: SharesRecordActivity.ShareInfo? = null
) {



    override fun toString(): String {
        return "${time}, ${code}, $name"
    }

    fun copy(): TestShareInfo {
        val info = TestShareInfo()
        info.id = this.id
        info.time = this.time
        info.code = this.code
        info.name = this.name
        info.beginPrice = this.beginPrice
        info.preAvg = this.preAvg
        info.dayCount = this.dayCount
        info.updateTime = this.updateTime
        info.result = this.result
        info.collect = this.collect
        info.label1 = this.label1
        info.label2 = this.label2
        info.label3 = this.label3
        info.label4 = this.label4
        info.label5 = this.label5
        info.label6 = this.label6
        info.label7 = this.label7
        info.label8 = this.label8
        info.label9 = this.label9
        info.label10 = this.label10
        info.ext1 = this.ext1
        info.ext2 = this.ext2
        info.ext3 = this.ext3
        info.ext4 = this.ext4
        info.ext5 = this.ext5

        info.count = this.count

        info.expand = this.expand
        info.moreInfo = this.moreInfo

        info.index = this.index


        candleEntryList?.also {
            val candles = ArrayList<CandleEntry>()
            candles.addAll(it)
            info.candleEntryList = candles
        }

        barEntryList?.also {
            val bars = ArrayList<BarEntry>()
            bars.addAll(it)
            info.barEntryList = bars
        }

        colorsList?.also {
            val colors = ArrayList<Int>()
            colors.addAll(it)
            info.colorsList = colors
        }

        values_5?.also {
            val v = ArrayList<Entry>()
            v.addAll(it)
            info.values_5 = v
        }

        values_10?.also {
            val v = ArrayList<Entry>()
            v.addAll(it)
            info.values_10 = v
        }

        values_20?.also {
            val v = ArrayList<Entry>()
            v.addAll(it)
            info.values_20 = v
        }

        info.lastShareInfo = this.lastShareInfo

        return info
    }

}