package com.libiao.mushroom.room.ban

import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CandleEntry
import com.libiao.mushroom.SharesRecordActivity
import java.util.ArrayList

open class BanShareInfo(
    @PrimaryKey(autoGenerate = true)//主键是否自动增长，默认为false
    var id: Int = 0,
    var time: String? = null,
    var code: String? = null,
    var name: String? = null,
    var dayCount: Int = 0,
    var updateTime: String? = null,
    var collect: Int = 0, // 0 未收藏  1 收藏
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
    var candleEntryList: ArrayList<CandleEntry>? = null,
    @Ignore
    var barEntryList: ArrayList<BarEntry>? = null,
    @Ignore
    var colorsList: ArrayList<Int>? = null,
    @Ignore
    var lastShareInfo: SharesRecordActivity.ShareInfo? = null
) {


    @Ignore
    open var ban: Int = 1

    override fun toString(): String {
        return "${time}, ${code}, $name"
    }

    fun copy(): BanShareInfo {
        val info = BanShareInfo()
        info.id = this.id
        info.time = this.time
        info.code = this.code
        info.name = this.name
        info.dayCount = this.dayCount
        info.updateTime = this.updateTime
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

        info.ban = this.ban


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

        info.lastShareInfo = this.lastShareInfo

        return info
    }

    override fun hashCode(): Int {
        var result = dayCount
        result = 31 * result + code.hashCode()
        result = 31 * result + (candleEntryList?.size ?: 0)
        return result
    }

    override fun equals(other: Any?): Boolean {
        if(other is BanShareInfo) {
            if(other.candleEntryList?.size != this.candleEntryList?.size) {
                return false
            }
            return other.code == this.code
        }
        return false
    }

}