package com.libiao.mushroom.room.ban.twochuang

import androidx.room.Entity
import androidx.room.Ignore
import com.libiao.mushroom.room.ban.BanShareInfo

@Entity
class BanTwoChuangShareInfo : BanShareInfo() {
    @Ignore
    override var ban = 2
}