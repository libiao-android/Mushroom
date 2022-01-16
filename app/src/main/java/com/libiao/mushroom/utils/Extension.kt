package com.libiao.mushroom.utils

import com.libiao.mushroom.MushRoomApplication


inline val Int.dip: Float
    get() = MushRoomApplication.sApplication!!.resources.displayMetrics.density * this