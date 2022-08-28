package com.libiao.mushroom.mine.jigou

import android.os.Bundle
import android.view.View
import com.libiao.mushroom.R
import com.libiao.mushroom.base.BaseActivity
import com.libiao.mushroom.room.jigou.JiGouShareInfo
import com.libiao.mushroom.room.jigou.JiGouShareDatabase
import com.libiao.mushroom.utils.CodeUtil
import kotlinx.android.synthetic.main.ji_gou_record_activity.*
import java.util.*

class JiGouInfoRecordActivity: BaseActivity() {

    var data: MutableList<JiGouShareInfo>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.ji_gou_record_activity)
        initView()
        initData()

    }

    private fun initData() {
        data = JiGouShareDatabase.getInstance()?.getJiGouShareDao()?.getShares()
    }

    private fun initView() {
        val c = Calendar.getInstance();
        c.timeZone = TimeZone.getTimeZone("GMT+8:00");
        val mYear = c.get(Calendar.YEAR) // 获取当前年份
        val mMonth = c.get(Calendar.MONTH) + 1// 获取当前月份
        var mDay = c.get(Calendar.DAY_OF_MONTH)// 获取当前月份的日期号码
        var mWay = getWay(c.get(Calendar.DAY_OF_WEEK))

        if (mWay == 6) {
            mWay = 5
            c.set(Calendar.DATE, c.get(Calendar.DATE) - 1);
            mDay = c.get(Calendar.DAY_OF_MONTH)// 获取当前月份的日期号码

        } else if(mWay == 7) {
            mWay = 5
            c.set(Calendar.DATE, c.get(Calendar.DATE) - 2);
            mDay = c.get(Calendar.DAY_OF_MONTH)// 获取当前月份的日期号码
        }

        val time = "$mYear-$mMonth-$mDay-$mWay"

        et_time.setText(time)
    }

    fun jiGouRecord(v: View) {
        val time = et_time.text.trim().toString()
        val code = et_code.text.trim().toString()
        val info = et_info.text.trim().toString()
        var shareInfo: JiGouShareInfo? = null
        if(time.isNotEmpty() && code.isNotEmpty() && info.isNotEmpty()) {

            if(data != null && data!!.size > 0) {
                data!!.forEach {
                    if(it.code == code) {
                        shareInfo = it
                        return@forEach
                    }
                }
            }
            if(shareInfo == null) {
                shareInfo = JiGouShareInfo()
                shareInfo?.time = time
                shareInfo?.code = CodeUtil.getCode(code)
                shareInfo?.jiGouInfo = "${time}#$info"
                JiGouShareDatabase.getInstance()?.getJiGouShareDao()?.insert(shareInfo!!)
                tv_result.text = "新增成功"
            } else {
                shareInfo?.jiGouInfo = "${shareInfo?.jiGouInfo}, ${time}#$info"
                JiGouShareDatabase.getInstance()?.getJiGouShareDao()?.update(shareInfo!!)
                tv_result.text = "更新成功"
            }
            et_info.setText("")
        } else {
            tv_result.text = "不能为空"
        }
    }

    private fun getWay(get: Int): Int {
        return when (get) {
            1 -> 7
            2 -> 1
            3 -> 2
            4 -> 3
            5 -> 4
            6 -> 5
            7 -> 6
            else -> 0
        }
    }
}