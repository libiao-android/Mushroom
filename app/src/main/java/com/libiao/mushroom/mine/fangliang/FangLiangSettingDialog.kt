package com.libiao.mushroom.mine.fangliang

import android.app.Dialog
import android.content.Context
import com.libiao.mushroom.R
import kotlinx.android.synthetic.main.fangliang_setting_dialog_layout.*

class FangLiangSettingDialog : Dialog {

    constructor(context: Context, callback: (info: FangLiangSettingBean)->Unit): super(context, R.style.baseDialog) {
        setContentView(R.layout.fangliang_setting_dialog_layout)
        initView(callback)

    }

    private fun initView(callback: (info: FangLiangSettingBean)->Unit) {
        setting_btn_cancel.setOnClickListener {
            dismiss()
        }
        setting_btn_sure.setOnClickListener {
            val bean = FangLiangSettingBean()
            bean.timeChecked = dialog_cb_time.isChecked
            bean.timeValue = setting_et_time.text.toString().toInt()
            bean.timeValue2 = setting_et_time2.text.toString().toInt()


            bean.liangLeftChecked = dialog_cb_liang_left.isChecked
            bean.liangLeftValue = setting_et_liang_left.text.toString().toDouble()


            bean.zuiDaLiang = dialog_cb_zui_da_liang.isChecked

            bean.fangLiang = dialog_cb_fang_liang.isChecked

            bean.weekOne = dialog_cb_week_one.isChecked
            bean.weekTwo = dialog_cb_week_two.isChecked
            bean.weekThree = dialog_cb_week_three.isChecked
            bean.weekFour = dialog_cb_week_four.isChecked
            bean.weekFive = dialog_cb_week_five.isChecked
            bean.firstMax = dialog_cb_first_max.isChecked

            callback(bean)

            dismiss()
        }
    }

}