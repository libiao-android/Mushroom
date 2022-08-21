package com.libiao.mushroom.mine.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import com.airbnb.epoxy.TypedEpoxyController
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.UniqueOnly
import com.airbnb.mvrx.fragmentViewModel
import com.libiao.mushroom.R
import com.libiao.mushroom.kline.KLineActivity
import com.libiao.mushroom.mine.ICommand
import com.libiao.mushroom.mine.MoreDialog
import com.libiao.mushroom.mine.SelfSelectionActivity
import com.libiao.mushroom.mine.tab.TestTab
import com.libiao.mushroom.mine.test.TestState
import com.libiao.mushroom.mine.test.TestViewModel
import com.libiao.mushroom.mine.test.testItemView
import com.libiao.mushroom.mine.timeItemView
import com.libiao.mushroom.room.TestShareDatabase
import com.libiao.mushroom.utils.ClipboardUtil
import com.libiao.mushroom.utils.LogUtil
import kotlinx.android.synthetic.main.fang_liang_fragment.*
import kotlinx.android.synthetic.main.test_fragment.*
import java.util.*

class TestFragment: BaseFragment(R.layout.test_fragment), MavericksView, ICommand {

    val handler = Handler(Looper.getMainLooper())

    override fun order(type: Int, data: Any?) {
        when(type) {
            ICommand.SETTING -> {
                testViewModel.sortMaxPrice {
                    handler.post {
                        Toast.makeText(context, "${it}", Toast.LENGTH_LONG).show()
                    }
                }
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
        private const val TAG = "BanTwoFragment"
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
            testViewModel.fetchInfo(1)
            tv_month.text = "1"
        }
        two_test.setOnClickListener {
            loadingStatus = 1
            testViewModel.fetchInfo(2)
            tv_month.text = "2"
        }
        three_test.setOnClickListener {
            loadingStatus = 1
            testViewModel.fetchInfo(3)
            tv_month.text = "3"
        }
        four_test.setOnClickListener {
            loadingStatus = 1
            testViewModel.fetchInfo(4)
            tv_month.text = "4"
        }
        five_test.setOnClickListener {
            loadingStatus = 1
            testViewModel.fetchInfo(5)
            tv_month.text = "5"
        }
        six_test.setOnClickListener {
            loadingStatus = 1
            testViewModel.fetchInfo(6)
            tv_month.text = "6"
        }
        seven_test.setOnClickListener {
            loadingStatus = 1
            testViewModel.fetchInfo(7)
            tv_month.text = "7"
        }
        eight_test.setOnClickListener {
            loadingStatus = 1
            testViewModel.fetchInfo(8)
            tv_month.text = "8"
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
                if(it.time != time) {
                    time = it.time ?: ""
                    timeItemView {
                        id(time)
                        time(it.time)
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
                                else -> {
                                    val intent = Intent(context, KLineActivity::class.java)
                                    intent.putExtra("code", it.code)
                                    intent.putExtra("info", it.toString())
                                    context?.startActivity(intent)
                                }
                            }
                        }
                        longClick {view ->
                            MoreDialog(context!!){view2 ->
                                when(view2.id) {
                                    R.id.btn_dialog_delete -> {
                                        TestShareDatabase.getInstance()?.getTestShareDao()?.delete(it.code!!)
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