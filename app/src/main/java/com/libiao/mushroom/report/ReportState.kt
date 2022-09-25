package com.libiao.mushroom.report

import com.airbnb.mvrx.MavericksState
import com.libiao.mushroom.room.report.ReportShareInfo

data class ReportState(
    val infoList: MutableList<ReportShareInfo> = mutableListOf()
): MavericksState