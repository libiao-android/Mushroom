package com.libiao.mushroom.mine.jigou

import com.airbnb.mvrx.MavericksState
import com.libiao.mushroom.room.jigou.JiGouShareInfo

data class JiGouState(
    val infoList: MutableList<JiGouShareInfo> = mutableListOf()
): MavericksState