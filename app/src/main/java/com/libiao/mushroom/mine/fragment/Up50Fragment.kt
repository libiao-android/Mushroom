package com.libiao.mushroom.mine.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.airbnb.epoxy.TypedEpoxyController
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.UniqueOnly
import com.airbnb.mvrx.fragmentViewModel
import com.libiao.mushroom.R
import com.libiao.mushroom.kline.KLineActivity
import com.libiao.mushroom.mine.ICommand
import com.libiao.mushroom.mine.MoreDialog
import com.libiao.mushroom.mine.SelfSelectionActivity
import com.libiao.mushroom.mine.tab.Up50Tab
import com.libiao.mushroom.mine.timeItemView
import com.libiao.mushroom.mine.up50.Up50State
import com.libiao.mushroom.mine.up50.Up50ViewModel
import com.libiao.mushroom.mine.up50.up50ItemView
import com.libiao.mushroom.room.Up50ShareDatabase
import com.libiao.mushroom.utils.ClipboardUtil
import com.libiao.mushroom.utils.LogUtil
import kotlinx.android.synthetic.main.fang_liang_fragment.*
import java.util.*

class Up50Fragment: BaseFragment(R.layout.fang_liang_fragment), MavericksView, ICommand {

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
                    up50ViewModel.updateLocal()
                }

            }
            ICommand.NETWORK -> {
                mRefreshCount = 0
                up50ViewModel.updateNetWork(this)
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

    private val controller = Up50Controller()
    private val up50ViewModel: Up50ViewModel by fragmentViewModel()

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
        loadingStatus = 1

        up50ViewModel.fetchInfo()
    }

    private fun initView() {
        epoxy_view_fang_liang.setController(controller)
        up50ViewModel.onEach(deliveryMode = UniqueOnly(UUID.randomUUID().toString())) {
            controller.setData(it)
        }
    }

    inner class Up50Controller: TypedEpoxyController<Up50State>() {

        override fun buildModels(data: Up50State?) {
            LogUtil.i(TAG, "buildModels: ${data?.infoList?.size}")
            size = data?.infoList?.size ?: 0
            if(size > 0) {
                loadingStatus = 0
                (activity as SelfSelectionActivity).notifyData(1, Up50Tab.TAG, size)
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
                            up50ViewModel.expand(it.time!!)
                        }
                    }
                }
                if(it.expand) {
                    up50ItemView {
                        id(it.id)
                        data(it)
                        click {view ->
                            val id = view.id
                            when(id) {
                                R.id.up50_item_code -> {
                                    ClipboardUtil.clip(context!!, it.code)
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
                                        Up50ShareDatabase.getInstance()?.getUp50ShareDao()?.delete(it.code!!)
                                        up50ViewModel.deleteItem(it)
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
        (activity as SelfSelectionActivity).notifyData(2, Up50Tab.TAG, mRefreshCount)
        LogUtil.i(TAG, "mRefreshCount: $mRefreshCount")
        if(mRefreshCount == size) {
            (activity as SelfSelectionActivity).notifyData(3, Up50Tab.TAG, 0)
        }
    }
}