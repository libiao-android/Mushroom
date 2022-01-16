package com.libiao.mushroom.mine.fangliang

import com.airbnb.mvrx.MavericksState
import com.libiao.mushroom.room.FangLiangShareInfo

data class FangLiangState(
    val infoList: MutableList<FangLiangShareInfo> = mutableListOf()
): MavericksState