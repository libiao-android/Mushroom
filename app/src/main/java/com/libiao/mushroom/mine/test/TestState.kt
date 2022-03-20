package com.libiao.mushroom.mine.test

import com.airbnb.mvrx.MavericksState
import com.libiao.mushroom.room.TestShareInfo

data class TestState(
    val infoList: MutableList<TestShareInfo> = mutableListOf()
): MavericksState