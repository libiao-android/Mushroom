package com.libiao.mushroom.room

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CandleEntry
import com.libiao.mushroom.SharesRecordActivity
import java.util.ArrayList

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
    var heart: Boolean = false

    var label1: String? = null
    var label2: String? = null
    var label3: String? = null
    var label4: String? = null
    var label5: String? = null
    var label6: String? = null
    var label7: String? = null
    var label8: String? = null
    var label9: String? = null
    var label10: String? = null
    var ext1: String? = null
    var ext2: String? = null
    var ext3: String? = null
    var ext4: String? = null
    var ext5: String? = null

    @Ignore
    var totalRange: Double = 0.00 //记录后至今日涨幅
    @Ignore
    var todayRange: Double = 0.00 //今日涨幅
    @Ignore
    var todayLiang: Double = 0.00 //今日量能
    @Ignore
    var fangLiang: Boolean = false //是否放量
    @Ignore
    var redLine: Boolean = false //是否阳线
    @Ignore
    var priority: Int = 0
    @Ignore
    var moreInfo: String? = null
    @Ignore
    var candleEntryList: ArrayList<CandleEntry>? = null
    @Ignore
    var barEntryList: ArrayList<BarEntry>? = null
    @Ignore
    var colorsList: ArrayList<Int>? = null
    @Ignore
    var sanLianYin: Boolean = false //三连阴
    @Ignore
    var maxRange: Double = 0.00
    @Ignore
    var zhiDie: Boolean = false

    @Ignore
    var shareInfo: SharesRecordActivity.ShareInfo? = null

    override fun toString(): String {
        return "${time}, ${code}, $name"
    }
}