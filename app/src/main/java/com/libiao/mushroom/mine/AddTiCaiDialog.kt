package com.libiao.mushroom.mine

import android.app.Dialog
import android.content.Context
import com.libiao.mushroom.R
import kotlinx.android.synthetic.main.add_ti_cai_dialog_layout.*

class AddTiCaiDialog : Dialog {

    constructor(context: Context, tiCai: String?, onClick: (value: String) -> Unit): super(context, R.style.baseDialog) {
        setContentView(R.layout.add_ti_cai_dialog_layout)
        if (tiCai?.isNotEmpty() == true) {
            ti_cai_input.setText(tiCai)
        }
        btn_dialog_confirm.setOnClickListener {
            onClick(ti_cai_input.text.toString())
            dismiss()
        }
    }

}