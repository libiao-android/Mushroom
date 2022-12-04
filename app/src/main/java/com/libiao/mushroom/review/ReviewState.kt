package com.libiao.mushroom.review

import com.airbnb.mvrx.MavericksState
import com.libiao.mushroom.room.review.ReviewInfo

data class ReviewState(
    val infoList: MutableList<ReviewInfo> = mutableListOf()
): MavericksState