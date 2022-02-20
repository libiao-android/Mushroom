package com.libiao.mushroom.room.ban.two

import androidx.room.Entity
import androidx.room.Ignore
import com.libiao.mushroom.room.ban.BanShareInfo

@Entity
class BanTwoShareInfo : BanShareInfo() {

    @Ignore
    override var ban = 2
}