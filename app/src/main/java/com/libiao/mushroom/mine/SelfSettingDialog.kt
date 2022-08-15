package com.libiao.mushroom.mine

import android.app.Dialog
import android.content.Context
import com.libiao.mushroom.R
import kotlinx.android.synthetic.main.self_setting_dialog_layout.*

class SelfSettingDialog : Dialog {

    constructor(context: Context, callback: (info: SelfSettingBean)->Unit): super(context, R.style.baseDialog) {
        setContentView(R.layout.self_setting_dialog_layout)
        initView(callback)

    }

    private fun initView(callback: (info: SelfSettingBean)->Unit) {
        setting_btn_cancel.setOnClickListener {
            dismiss()
        }
        setting_btn_sure.setOnClickListener {
            val bean = SelfSettingBean()
            bean.timeChecked = dialog_cb_time.isChecked
            bean.timeValue = setting_et_time.text.toString().toInt()

            bean.rangeLeftChecked = dialog_cb_rang_left.isChecked
            bean.rangeLeftValue = setting_et_rang_left.text.toString().toDouble()

            bean.rangeRightChecked = dialog_cb_rang_right.isChecked
            bean.rangeRightValue = setting_et_rang_right.text.toString().toDouble()

            bean.liangLeftChecked = dialog_cb_liang_left.isChecked
            bean.liangLeftValue = setting_et_liang_left.text.toString().toDouble()

            bean.liangRightChecked = dialog_cb_liang_right.isChecked
            bean.liangRightValue = setting_et_liang_right.text.toString().toDouble()

            bean.redLine = dialog_cb_line.isChecked

            bean.youXuan = dialog_cb_you_xuan.isChecked

            bean.maxRangChecked = dialog_cb_max_rang.isChecked
            bean.maxRangValue = setting_et_max_rang.text.toString().toDouble()

            bean.zhiDie = dialog_cb_zhi_die.isChecked

            bean.duan_ceng_Checked = dialog_cb_line_10.isChecked

            bean.xin_gao = dialog_cb_xin_gao.isChecked

            callback(bean)

            dismiss()
        }
    }

}