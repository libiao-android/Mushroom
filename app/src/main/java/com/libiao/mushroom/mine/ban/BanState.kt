package com.libiao.mushroom.mine.ban

import com.airbnb.mvrx.MavericksState
import com.libiao.mushroom.room.ban.BanShareInfo

data class BanState(
    val infoList: MutableList<BanShareInfo> = mutableListOf()
): MavericksState