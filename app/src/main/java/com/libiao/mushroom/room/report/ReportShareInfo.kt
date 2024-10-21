package com.libiao.mushroom.room.report

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.libiao.mushroom.SharesRecordActivity
import java.util.ArrayList

@Entity
data class ReportShareInfo(
    @PrimaryKey(autoGenerate = true)//主键是否自动增长，默认为false
    var id: Int = 0,

    var time: String? = null,
    var code: String? = null,
    var name: String? = null,
    var yinXianLength: Double = 0.00,

    var beginPrice: Double = 0.00,
    var preAvg: Double = 0.00, //平均量能
    var dayCount: Int = 0,
    var startIndex: Int = 0,
    var updateTime: String? = null,
    var result: Int = 0, // 0 初始  1 成功  2失败
    var collect: Int = 0, // 0 未收藏  1 收藏
    var maxPrice: Double = 0.00,
    var maxCount: Int = 0,
    var delete: Int = 0,
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
    var ext4: String? = null,
    var ext5: String? = null,
    @Ignore
    var moreInfo: String? = null,
    @Ignore
    var moreInfo2: String? = null,
    @Ignore
    var moreInfo3: String? = null,

    @Ignore
    var currentCb: Int = 1,

    @Ignore
    var todayRange: Double = 0.00, //今日涨幅

    @Ignore
    var expand: Boolean = true,

    @Ignore
    var fanbao: Boolean = false,

    @Ignore
    var count: String = "0",

    @Ignore
    var rT: Double = 0.00,
    @Ignore
    var oneR: Double = 0.00,

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
    var lastShareInfo: SharesRecordActivity.ShareInfo? = null,
    @Ignore
    var fenShiPath: String? = null,
    @Ignore
    var fenShiPath2: String? = null
) {
    override fun toString(): String {
        return "${time}, ${code}, $name"
    }

    fun copy(): ReportShareInfo {
        val info = ReportShareInfo()
        info.id = this.id
        info.time = this.time
        info.code = this.code
        info.name = this.name
        info.beginPrice = this.beginPrice
        info.preAvg = this.preAvg
        info.startIndex = this.startIndex
        info.dayCount = this.dayCount
        info.updateTime = this.updateTime
        info.result = this.result
        info.collect = this.collect
        info.maxCount = this.maxCount
        info.maxPrice = this.maxPrice
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

        info.expand = this.expand
        info.moreInfo = this.moreInfo
        info.moreInfo2 = this.moreInfo2
        info.moreInfo3 = this.moreInfo3


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

        info.fenShiPath = this.fenShiPath
        info.fenShiPath2 = this.fenShiPath2
        info.yinXianLength = this.yinXianLength

        return info
    }

}