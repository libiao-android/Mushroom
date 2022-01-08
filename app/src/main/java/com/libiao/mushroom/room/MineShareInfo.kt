package com.libiao.mushroom.room

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.github.mikephil.charting.data.CandleEntry

@Entity
class MineShareInfo {

    @PrimaryKey(autoGenerate = true)//主键是否自动增长，默认为false
    var id: Int = 0

    var time: String? = null
    var code: String? = null
    var name: String? = null
    var price: Double = 0.00
    var nowPrice: Double = 0.00
    var dayCount: Int = 0
    var updateTime: String? = null

    @Ignore
    var totalRange: Double = 0.00 //涨停后至今日涨幅
    @Ignore
    var todayRange: Double = 0.00 //今日涨幅
    @Ignore
    var fangLiang: Boolean = false //是否放量
    @Ignore
    var redLine: Boolean = false //是否阳线
    @Ignore
    var priority: Int = 0
    @Ignore
    var moreInfo: String? = null
    @Ignore
    var candleEntryList: List<CandleEntry>? = null
    @Ignore
    var heart: Boolean = false

    override fun toString(): String {
        return "${time}, ${code}, $name"
    }
}