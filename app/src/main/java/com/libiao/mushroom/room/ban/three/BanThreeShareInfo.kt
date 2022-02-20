package com.libiao.mushroom.room.ban.three

import androidx.room.Entity
import androidx.room.Ignore
import com.libiao.mushroom.room.ban.BanShareInfo

@Entity
class BanThreeShareInfo : BanShareInfo() {

    @Ignore
    override var ban = 3
}