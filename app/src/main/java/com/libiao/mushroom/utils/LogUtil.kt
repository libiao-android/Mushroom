package com.libiao.mushroom.utils

import android.util.Log

object LogUtil {

    private const val TAG = "libiao_A"

    fun i(tag: String, msg: String) {
        Log.i(TAG, "[$tag] $msg")
    }
}