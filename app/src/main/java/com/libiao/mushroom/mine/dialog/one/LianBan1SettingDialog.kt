package com.libiao.mushroom.mine.dialog.one

import android.app.Dialog
import android.content.Context
import com.libiao.mushroom.R
import kotlinx.android.synthetic.main.lian_ban_1_setting_dialog_layout.*

class LianBan1SettingDialog : Dialog {

    constructor(context: Context, callback: (info: LianBan1SettingBean)->Unit): super(context, R.style.baseDialog) {
        setContentView(R.layout.lian_ban_1_setting_dialog_layout)
        initView(callback)

    }

    private fun initView(callback: (info: LianBan1SettingBean)->Unit) {
        setting_btn_cancel.setOnClickListener {
            dismiss()
        }
        setting_btn_sure.setOnClickListener {
            val bean = LianBan1SettingBean()
            bean.thirdDayRed = dialog_cb_time.isChecked

            callback(bean)

            dismiss()
        }
    }

}