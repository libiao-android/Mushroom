package com.libiao.mushroom.mine

import android.app.Dialog
import android.content.Context
import android.view.View
import com.libiao.mushroom.R
import kotlinx.android.synthetic.main.more_dialog_layout.*

class MoreDialog : Dialog {

    constructor(context: Context, onClick: (v: View) -> Unit): super(context, R.style.baseDialog) {
        setContentView(R.layout.more_dialog_layout)
        btn_dialog_delete.setOnClickListener {
            onClick(it)
            dismiss()
        }
    }

}