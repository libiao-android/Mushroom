package com.libiao.mushroom.room

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
class CollectShareInfo {

    @PrimaryKey(autoGenerate = true)//主键是否自动增长，默认为false
    var id: Int = 0

    var time: Long = 0L
    var code: String? = null
    var name: String? = null

    override fun toString(): String {
        return "${time}, ${code}, $name"
    }
}