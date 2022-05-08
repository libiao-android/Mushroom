package com.libiao.mushroom.mine.up50

import com.airbnb.mvrx.MavericksState
import com.libiao.mushroom.room.TestShareInfo
import com.libiao.mushroom.room.Up50ShareInfo

data class Up50State(
    val infoList: MutableList<Up50ShareInfo> = mutableListOf()
): MavericksState