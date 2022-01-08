package com.libiao.mushroom.mine

import android.app.Dialog
import android.content.Context
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.libiao.mushroom.R
import kotlinx.android.synthetic.main.fenshi_dialog_layout.*
import kotlinx.android.synthetic.main.more_dialog_layout.*

class SelfSettingDialog : Dialog {

    constructor(context: Context): super(context, R.style.baseDialog) {
        setContentView(R.layout.self_setting_dialog_layout)
    }

}