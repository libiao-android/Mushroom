package com.libiao.mushroom.room.ban.five

import androidx.room.Entity
import androidx.room.Ignore
import com.libiao.mushroom.room.ban.BanShareInfo

@Entity
class BanFiveShareInfo : BanShareInfo() {
    @Ignore
    override var ban = 5
}