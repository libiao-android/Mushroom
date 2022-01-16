package com.libiao.mushroom.room

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CandleEntry
import com.libiao.mushroom.SharesRecordActivity

@Entity
class FangLiangShareInfo {

    @PrimaryKey(autoGenerate = true)//主键是否自动增长，默认为false
    var id: Int = 0

    var time: String? = null
    var code: String? = null
    var name: String? = null
    var beginPrice: Double = 0.00
    var preAvg: Double = 0.00 //平均量能
    var dayCount: Int = 0
    var updateTime: String? = null
    var result: Int = 0 // 0 初始  1 成功  2失败
    var collect: Int = 0 // 0 未收藏  1 收藏
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
    var candleEntryList: List<CandleEntry>? = null
    @Ignore
    var barEntryList: List<BarEntry>? = null
    @Ignore
    var colorsList: List<Int>? = null
    @Ignore
    var lastShareInfo: SharesRecordActivity.ShareInfo? = null

    override fun toString(): String {
        return "${time}, ${code}, $name"
    }

    override fun hashCode(): Int {
		return code.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if(other !is FangLiangShareInfo) return false
        if(other.code == code) return true
        return false
    }
}