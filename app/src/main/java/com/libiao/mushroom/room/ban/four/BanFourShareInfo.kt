package com.libiao.mushroom.room.ban.four

import androidx.room.Entity
import androidx.room.Ignore
import com.libiao.mushroom.room.ban.BanShareInfo

@Entity
class BanFourShareInfo : BanShareInfo() {
    @Ignore
    override var ban = 4
}