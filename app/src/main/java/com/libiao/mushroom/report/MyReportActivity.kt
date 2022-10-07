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
import com.libiao.mushroom.utils.ClipboardUtil
import com.libiao.mushroom.utils.LogUtil
import kotlinx.android.synthetic.main.report_activity.*
import kotlinx.android.synthetic.main.report_item_view.view.*
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

    private fun initView() {
        cb_heart.setOnCheckedChangeListener { buttonView, isChecked ->
            reportViewModel.setOnlySeeHeart(isChecked)
        }
        epoxy_view_report.setController(controller)

        reportViewModel.onEach(deliveryMode = UniqueOnly(UUID.randomUUID().toString())) {
            controller.setData(it)
        }


        one_test.setOnClickListener {
            reportViewModel.fetchInfo(1)
            tv_month.text = "1"
        }
        two_test.setOnClickListener {
            reportViewModel.fetchInfo(2)
            tv_month.text = "2"
        }
        three_test.setOnClickListener {
            reportViewModel.fetchInfo(3)
            tv_month.text = "3"
        }
        four_test.setOnClickListener {
            reportViewModel.fetchInfo(4)
            tv_month.text = "4"
        }
        five_test.setOnClickListener {
            reportViewModel.fetchInfo(5)
            tv_month.text = "5"
        }
        six_test.setOnClickListener {
            reportViewModel.fetchInfo(6)
            tv_month.text = "6"
        }
        seven_test.setOnClickListener {
            reportViewModel.fetchInfo(7)
            tv_month.text = "7"
        }
        eight_test.setOnClickListener {
            reportViewModel.fetchInfo(8)
            tv_month.text = "8"
        }
        nine_test.setOnClickListener {
            reportViewModel.fetchInfo(9)
            tv_month.text = "9"
        }
    }

    override fun invalidate() {

    }

    inner class ReportController: TypedEpoxyController<ReportState>() {
        override fun buildModels(data: ReportState?) {
            LogUtil.i(TAG, "buildModels: ${data?.infoList?.size}")
            var time = ""
            var index = 0
            data?.infoList?.forEach {
                if (it.time != time) {
                    time = it.time ?: ""
                    index = 0
                    timeItemView {
                        id(time)
                        time(it.time)
                        count(it.ext1)
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