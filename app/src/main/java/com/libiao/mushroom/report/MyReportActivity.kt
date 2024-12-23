package com.libiao.mushroom.report

import android.content.Intent
import android.os.Bundle
import com.airbnb.epoxy.TypedEpoxyController
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.UniqueOnly
import com.airbnb.mvrx.viewModel
import com.libiao.mushroom.R
import com.libiao.mushroom.base.BaseActivity
import com.libiao.mushroom.kline.KLineActivity
import com.libiao.mushroom.mine.FenShiDialog
import com.libiao.mushroom.mine.MoreDialog
import com.libiao.mushroom.mine.timeItemView
import com.libiao.mushroom.room.report.ReportShareDatabase
import com.libiao.mushroom.thread.ThreadPoolUtil
import com.libiao.mushroom.utils.ClipboardUtil
import com.libiao.mushroom.utils.LogUtil
import com.libiao.mushroom.utils.baoLiuXiaoShu
import kotlinx.android.synthetic.main.report_activity.*
import java.util.*

class MyReportActivity: BaseActivity(), MavericksView {

    companion object {
        const val TAG = "MyReportActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.report_activity)
        initView()
    }

    private val controller = ReportController()
    private val reportViewModel: ReportViewModel by viewModel()

    private var yearTotalR = 0.00

    private fun initView() {
        reportViewModel.setYear(2024)
        cb_21.setOnCheckedChangeListener { buttonView, isChecked ->
            yearTotalR = 0.00
            reportViewModel.setYear(2021)
        }
        cb_22.setOnCheckedChangeListener { buttonView, isChecked ->
            reportViewModel.setYear(2022)
            yearTotalR = 0.00
        }
        cb_23.setOnCheckedChangeListener { buttonView, isChecked ->
            reportViewModel.setYear(2023)
            yearTotalR = 0.00
        }
        cb_24.setOnCheckedChangeListener { buttonView, isChecked ->
            reportViewModel.setYear(2024)
            yearTotalR = 0.00
        }
        cb_all.setOnCheckedChangeListener { buttonView, isChecked ->
            reportViewModel.setLeiXing("all")
        }
        cb_zhu_ban.setOnCheckedChangeListener { buttonView, isChecked ->
            reportViewModel.setLeiXing("zhuBan")
        }
        cb_chuang.setOnCheckedChangeListener { buttonView, isChecked ->
            reportViewModel.setLeiXing("chuang")
        }


        btn_online.setOnClickListener {
            reportViewModel.online()
        }
        epoxy_view_report.setController(controller)

        reportViewModel.onEach(deliveryMode = UniqueOnly(UUID.randomUUID().toString())) {
            controller.setData(it)
        }
        seven_test.setOnClickListener {
            reportViewModel.fetchInfo(7) {
                ThreadPoolUtil.executeUI {
                    tv_total_range.text = it
                    yearTotalR += it.split(",")[1].trim().toDouble()
                    year_total_range.text = baoLiuXiaoShu(yearTotalR)
                }
            }
            tv_month.text = "7"
        }
        eight_test.setOnClickListener {
            reportViewModel.fetchInfo(8) {
                ThreadPoolUtil.executeUI {
                    tv_total_range.text = it
                    yearTotalR += it.split(",")[1].trim().toDouble()
                    year_total_range.text = baoLiuXiaoShu(yearTotalR)
                }
            }
            tv_month.text = "8"
        }

        nine_test.setOnClickListener {
            reportViewModel.fetchInfo(9){
                ThreadPoolUtil.executeUI {
                    tv_total_range.text = it
                    yearTotalR += it.split(",")[1].trim().toDouble()
                    year_total_range.text = baoLiuXiaoShu(yearTotalR)
                }
            }
            tv_month.text = "9"
        }
        ten_test.setOnClickListener {
            reportViewModel.fetchInfo(10){
                ThreadPoolUtil.executeUI {
                    tv_total_range.text = it
                    yearTotalR += it.split(",")[1].trim().toDouble()
                    year_total_range.text = baoLiuXiaoShu(yearTotalR)
                }
            }
            tv_month.text = "10"
        }
        eleven_test.setOnClickListener {
            reportViewModel.fetchInfo(11){
                ThreadPoolUtil.executeUI {
                    tv_total_range.text = it
                    yearTotalR += it.split(",")[1].trim().toDouble()
                    year_total_range.text = baoLiuXiaoShu(yearTotalR)
                }
            }
            tv_month.text = "11"
        }
        twelve_test.setOnClickListener {
            reportViewModel.fetchInfo(12){
                ThreadPoolUtil.executeUI {
                    tv_total_range.text = it
                    yearTotalR += it.split(",")[1].trim().toDouble()
                    year_total_range.text = baoLiuXiaoShu(yearTotalR)
                }
            }
            tv_month.text = "12"
        }

        one_test.setOnClickListener {
            reportViewModel.fetchInfo(1){
                ThreadPoolUtil.executeUI {
                    tv_total_range.text = it
                    yearTotalR += it.split(",")[1].trim().toDouble()
                    year_total_range.text = baoLiuXiaoShu(yearTotalR)
                }
            }
            tv_month.text = "1"
        }

        two_test.setOnClickListener {
            reportViewModel.fetchInfo(2){
                ThreadPoolUtil.executeUI {
                    tv_total_range.text = it
                    yearTotalR += it.split(",")[1].trim().toDouble()
                    year_total_range.text = baoLiuXiaoShu(yearTotalR)
                }
            }
            tv_month.text = "2"
        }
        three_test.setOnClickListener {
            reportViewModel.fetchInfo(3){
                ThreadPoolUtil.executeUI {
                    tv_total_range.text = it
                    yearTotalR += it.split(",")[1].trim().toDouble()
                    year_total_range.text = baoLiuXiaoShu(yearTotalR)
                }
            }
            tv_month.text = "3"
        }
        four_test.setOnClickListener {
            reportViewModel.fetchInfo(4){
                ThreadPoolUtil.executeUI {
                    tv_total_range.text = it
                    yearTotalR += it.split(",")[1].trim().toDouble()
                    year_total_range.text = baoLiuXiaoShu(yearTotalR)
                }
            }
            tv_month.text = "4"
        }
        five_test.setOnClickListener {
            reportViewModel.fetchInfo(5){
                ThreadPoolUtil.executeUI {
                    tv_total_range.text = it
                    yearTotalR += it.split(",")[1].trim().toDouble()
                    year_total_range.text = baoLiuXiaoShu(yearTotalR)
                }
            }
            tv_month.text = "5"
        }
        six_test.setOnClickListener {
            reportViewModel.fetchInfo(6){
                ThreadPoolUtil.executeUI {
                    tv_total_range.text = it
                    yearTotalR += it.split(",")[1].trim().toDouble()
                    year_total_range.text = baoLiuXiaoShu(yearTotalR)
                }
            }
            tv_month.text = "6"
        }
    }

    override fun invalidate() {

    }

    inner class ReportController: TypedEpoxyController<ReportState>() {
        override fun buildModels(data: ReportState?) {
            LogUtil.i(TAG, "buildModels: ${data?.infoList?.size}")
            var time = ""
            var index = 0
            size.text = "${data?.infoList?.size}"
            data?.infoList?.forEach {
                if (it.time != time) {
                    time = it.time ?: ""
                    index = 0
                    timeItemView {
                        id(time)
                        time("${it.time}, ${baoLiuXiaoShu(it.oneR)}")
                        count(it.count)
                        expand(it.expand)
                        click { v ->
                            reportViewModel.expand(it.time!!)
                        }
                    }
                }
                if (it.expand) {
                    index ++
                    reportItemView {
                        id(it.id)
                        index(index)
                        data(it)
                        click { view ->
                            val id = view.id
                            when (id) {
                                R.id.test_item_code -> {
                                    ClipboardUtil.clip(this@MyReportActivity, it.code)
                                }
                                R.id.test_item_name -> {
                                    FenShiDialog(this@MyReportActivity, it.code!!, it.time).show()
                                }
                                R.id.test_item_heart -> {
                                    reportViewModel.shouCang(it)
                                }
                                else -> {
                                    val intent = Intent(this@MyReportActivity, KLineActivity::class.java)
                                    intent.putExtra("code", it.code)
                                    intent.putExtra("info", it.toString())
                                    intent.putExtra("time", it.time)
                                    this@MyReportActivity.startActivity(intent)
                                }
                            }
                        }
                        longClick { view ->
                            MoreDialog(this@MyReportActivity) { view2 ->
                                when (view2.id) {
                                    R.id.btn_dialog_delete -> {
                                        ReportShareDatabase.getInstance()?.getReportShareDao()
                                            ?.delete(it.code!!)
                                        reportViewModel.deleteItem(it)
                                    }
                                }
                            }.show()
                        }
                    }
                }
            }
        }
    }
}