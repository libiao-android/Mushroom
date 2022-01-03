package com.libiao.mushroom.mine

import android.app.Dialog
import android.content.Context
import android.view.View
import com.bumptech.glide.Glide
import com.libiao.mushroom.R
import kotlinx.android.synthetic.main.fenshi_dialog_layout.*
import kotlinx.android.synthetic.main.more_dialog_layout.*

class FenShiDialog : Dialog {

    constructor(context: Context, code: String): super(context, R.style.baseDialog) {
        setContentView(R.layout.fenshi_dialog_layout)
        Glide.with(context).load("https://image.sinajs.cn/newchart/min/n/${code}.gif").asGif().into(iv_fenshi)
    }

}