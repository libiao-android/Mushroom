package com.libiao.mushroom

import android.app.Application
import android.content.Context

class MushRoomApplication: Application() {

    companion object {
        var sApplication: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        sApplication = this
    }
}