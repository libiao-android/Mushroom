package com.libiao.mushroom.mine.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.AdapterView
import com.airbnb.epoxy.TypedEpoxyController
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.UniqueOnly
import com.airbnb.mvrx.fragmentViewModel
import com.libiao.mushroom.R
import com.libiao.mushroom.kline.KLineActivity
import com.libiao.mushroom.mine.*
import com.libiao.mushroom.mine.tab.TestTab
import com.libiao.mushroom.mine.test.TestState
import com.libiao.mushroom.mine.test.TestViewModel
import com.libiao.mushroom.mine.test.testItemView
import com.libiao.mushroom.room.TestShareDatabase
import com.libiao.mushroom.room.test.TestShareDatabase2
import com.libiao.mushroom.utils.ClipboardUtil
import com.libiao.mushroom.utils.LogUtil
import com.libiao.mushroom.utils.baoLiuXiaoShu
import kotlinx.android.synthetic.main.test_fragment.*
import java.util.*

class TestFragment: BaseFragment(R.layout.test_fragment), MavericksView, ICommand {

    val handler = Handler(Looper.getMainLooper())

    var needTime = true

    override fun order(type: Int, data: Any?) {
        when(type) {
            ICommand.SETTING -> {

            }
            ICommand.HEART -> {
                heartChecked = data as Boolean

            }
            ICommand.LOCAL -> {
                onLineChecked = data as Boolean
                if(!onLineChecked) {
                    testViewModel.updateLocal()
                }

            }
            ICommand.NETWORK -> {
                mRefreshCount = 0
                testViewModel.updateNetWork(this)
            }
        }
    }

    override fun obtain(type: Int): Any? {
        when(type) {
            ICommand.OBTAIN_SIZE -> {
                return size
            }
            ICommand.LOADING_STATUS -> {
                return loadingStatus
            }
            ICommand.HEART_STATUS -> {
                return heartChecked
            }
            ICommand.ONLINE_STATUS -> {
                return onLineChecked
            }
        }
        return 0
    }

    companion object {
        private const val TAG = "TestFragment"
    }

    private val controller = TestController()
    private val testViewModel: TestViewModel by fragmentViewModel()

    private var size = 0

    override fun invalidate() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogUtil.i(TAG, "onViewCreated")
        initView()
        initData()
    }

    private fun initData() {
//        loadingStatus = 1
//
//        testViewModel.fetchInfo()
    }


    private fun initView() {
        epoxy_view_test.setController(controller)
        testViewModel.onEach(deliveryMode = UniqueOnly(UUID.randomUUID().toString())) {
            controller.setData(it)
        }

        one_test.setOnClickListener {
            loadingStatus = 1
            testViewModel.fetchInfo(1, dixi.text.toString().toDouble()) {
                more_info.text = it
            }
            tv_month.text = "1"
        }
        two_test.setOnClickListener {
            loadingStatus = 1
            testViewModel.fetchInfo(2, dixi.text.toString().toDouble()) {
                more_info.text = it
            }
            tv_month.text = "2"
        }
        three_test.setOnClickListener {
            loadingStatus = 1
            testViewModel.fetchInfo(3, dixi.text.toString().toDouble()) {
                more_info.text = it
            }
            tv_month.text = "3"
        }
        four_test.setOnClickListener {
            loadingStatus = 1
            testViewModel.fetchInfo(4, dixi.text.toString().toDouble()) {
                more_info.text = it
            }
            tv_month.text = "4"
        }
        five_test.setOnClickListener {
            loadingStatus = 1
            testViewModel.fetchInfo(5, dixi.text.toString().toDouble()) {
                more_info.text = it
            }
            tv_month.text = "5"
        }
        six_test.setOnClickListener {
            loadingStatus = 1
            testViewModel.fetchInfo(6, dixi.text.toString().toDouble()) {
                more_info.text = it
            }
            tv_month.text = "6"
        }
        seven_test.setOnClickListener {
            loadingStatus = 1
            testViewModel.fetchInfo(7, dixi.text.toString().toDouble()) {
                more_info.text = it
            }
            tv_month.text = "7"
        }
        eight_test.setOnClickListener {
            loadingStatus = 1
            testViewModel.fetchInfo(8, dixi.text.toString().toDouble()) {
                more_info.text = it
            }
            tv_month.text = "8"
        }
        nine_test.setOnClickListener {
            loadingStatus = 1
            testViewModel.fetchInfo(9, dixi.text.toString().toDouble()) {
                more_info.text = it
            }
            tv_month.text = "9"
        }
        ten_test.setOnClickListener {
            loadingStatus = 1
            testViewModel.fetchInfo(10, dixi.text.toString().toDouble()) {
                more_info.text = it
            }
            tv_month.text = "10"
        }

        eleven_test.setOnClickListener {
            loadingStatus = 1
            testViewModel.fetchInfo(11, dixi.text.toString().toDouble()) {
                more_info.text = it
            }
            tv_month.text = "11"
        }
        twelve_test.setOnClickListener {
            loadingStatus = 1
            testViewModel.fetchInfo(12, dixi.text.toString().toDouble()) {
                more_info.text = it
            }
            tv_month.text = "12"
        }

        cb_more_price.setOnCheckedChangeListener { buttonView, isChecked ->
            testViewModel.setMorePriceChecked(isChecked)
        }
        cb_less_price.setOnCheckedChangeListener { buttonView, isChecked ->
            testViewModel.setLessPricechecked(isChecked)
        }
        cb_2021.setOnCheckedChangeListener { buttonView, isChecked ->
            testViewModel.setYear(2021)
        }
        cb_2022.setOnCheckedChangeListener { buttonView, isChecked ->
            testViewModel.setYear(2022)
        }

        cb_2023.setOnCheckedChangeListener { buttonView, isChecked ->
            testViewModel.setYear(2023)
        }

        cb_2024.setOnCheckedChangeListener { buttonView, isChecked ->
            testViewModel.setYear(2024)
        }

        cb_max_price.setOnCheckedChangeListener { buttonView, isChecked ->
            testViewModel.setMaxPriceChecked(isChecked)
        }
        cb_xin_gao_again.setOnCheckedChangeListener { buttonView, isChecked ->
            testViewModel.setXinGaoAgainChecked(isChecked)
        }

        cb_120.setOnCheckedChangeListener { buttonView, isChecked ->
            testViewModel.set120Checked(isChecked)
        }



        cb_da_zhang.setOnCheckedChangeListener { buttonView, isChecked ->
            testViewModel.setDaZhang(isChecked)
        }

        cb_da_zhang.isChecked = true

//        cb_fang_liang_test.isChecked = true
//        cb_more_price.isChecked = true

        //cb_xin_gao_test.isChecked = true
        cb_max_price.isChecked = true
        cb_more_price.isChecked = true

        cb_xin_gao_2.isChecked = false


        cb_xin_liang2.setOnCheckedChangeListener { buttonView, isChecked ->
            testViewModel.setGao2XinLiangChecked(isChecked)
        }
        cb_gao2_time.setOnCheckedChangeListener { buttonView, isChecked ->
            needTime = isChecked
            testViewModel.setTimeChecked(isChecked)
        }
        cb_gao2_time.isChecked = false
        cb_xin_liang2.isChecked = true
        cb_gao2_liang.setOnCheckedChangeListener { buttonView, isChecked ->
            val liang = et_liang.text.toString()
            testViewModel.setGao2LiangChecked(isChecked, if(liang.isEmpty()) 0 else liang.toInt())

        }
        load.setOnClickListener {
            testViewModel.fetchInfo(1, dixi.text.toString().toDouble()){

            }
        }

        gao2_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                LogUtil.i(TAG, "onItemSelected: $position, $id")
                testViewModel.setPaiXuSelect(position)
            }
        }

        cb_gao2_zheng_xu.setOnCheckedChangeListener { buttonView, isChecked ->
            testViewModel.setZhengXuChecked(isChecked)
        }

        cb_gao2_he_xin.setOnCheckedChangeListener { buttonView, isChecked ->
            testViewModel.setHeXinChecked(isChecked)
        }

    }

    inner class TestController: TypedEpoxyController<TestState>() {

        override fun buildModels(data: TestState?) {
            LogUtil.i(TAG, "buildModels: ${data?.infoList?.size}")
            size = data?.infoList?.size ?: 0
            if(size > 0) {
                loadingStatus = 0
                (activity as SelfSelectionActivity).notifyData(1, TestTab.TAG, size)
            }
            var time = ""
            data?.infoList?.forEach {
                if(needTime && it.time != time) {
                    time = it.time ?: ""
                    timeItemView {
                        id(time)
                        time("${it.time}, ${baoLiuXiaoShu(it.oneR)}")
                        count(it.count)
                        expand(it.expand)
                        click {v ->
                            testViewModel.expand(it.time!!)
                        }
                    }
                }
                if(it.expand) {
                    testItemView {
                        id(it.id)
                        data(it)
                        click {view ->
                            val id = view.id
                            when(id) {
                                R.id.test_item_code -> {
                                    ClipboardUtil.clip(context!!, it.code)
                                }
                                R.id.test_item_name -> {
                                    FenShiDialog(context!!, it.code!!, it.time).show()
                                }
                                else -> {
                                    val intent = Intent(context, KLineActivity::class.java)
                                    intent.putExtra("code", it.code)
                                    intent.putExtra("info", it.toString())
                                    intent.putExtra("time", it.time)
                                    context?.startActivity(intent)
                                }
                            }
                        }
                        longClick {view ->
                            MoreDialog(context!!){view2 ->
                                when(view2.id) {
                                    R.id.btn_dialog_delete -> {
                                        if(cb_xin_gao_2.isChecked) {
                                            TestShareDatabase2.getInstance()?.getTestShareDao()?.delete(it.code!!)
                                        } else {
                                            TestShareDatabase.getInstance()?.getTestShareDao()?.delete(it.code!!)
                                        }
                                        testViewModel.deleteItem(it)
                                    }
                                }
                            }.show()
                        }
                    }
                }
            }
        }

    }

    override fun shiShiRefresh() {
        mRefreshCount ++
        (activity as SelfSelectionActivity).notifyData(2, TestTab.TAG, mRefreshCount)
        LogUtil.i(TAG, "mRefreshCount: $mRefreshCount")
        if(mRefreshCount == size) {
            (activity as SelfSelectionActivity).notifyData(3, TestTab.TAG, 0)
        }
    }
}