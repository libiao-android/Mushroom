package com.libiao.mushroom.mine.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.airbnb.epoxy.TypedEpoxyController
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.UniqueOnly
import com.airbnb.mvrx.fragmentViewModel
import com.libiao.mushroom.R
import com.libiao.mushroom.kline.KLineActivity
import com.libiao.mushroom.mine.ICommand
import com.libiao.mushroom.mine.SelfSelectionActivity
import com.libiao.mushroom.mine.fangliang.FangLiangState
import com.libiao.mushroom.mine.fangliang.FangLiangViewModel
import com.libiao.mushroom.mine.fangliang.fangLiangItemView
import com.libiao.mushroom.mine.tab.FangLiangTab
import com.libiao.mushroom.mine.tab.Line20Tab
import com.libiao.mushroom.mine.timeItemView
import com.libiao.mushroom.utils.LogUtil
import kotlinx.android.synthetic.main.fang_liang_fragment.*
import java.util.*

class FangLiangFragment: Fragment(R.layout.fang_liang_fragment), MavericksView, ICommand {

    override fun order(type: Int, data: Any?) {

    }

    override fun obtain(type: Int): Any? {
        when(type) {
            ICommand.OBTAIN_SIZE -> {
                return size
            }
        }
        return 0
    }

    companion object {
        private const val TAG = "FangLiangFragment"
    }

    private val controller = FangLiangController()
    private val fangLiangViewModel: FangLiangViewModel by fragmentViewModel()

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
        fangLiangViewModel.fetchInfo()
    }

    private fun initView() {
        epoxy_view_fang_liang.setController(controller)
        fangLiangViewModel.onEach(deliveryMode = UniqueOnly(UUID.randomUUID().toString())) {
            controller.setData(it)
        }
    }

    inner class FangLiangController: TypedEpoxyController<FangLiangState>() {

        override fun buildModels(data: FangLiangState?) {
            LogUtil.i(TAG, "buildModels: ${data?.infoList?.size}")
            size = data?.infoList?.size ?: 0
            if(size > 0) {
                (activity as SelfSelectionActivity).notifyData(FangLiangTab.TAG, size)
            }
            var time = ""
            data?.infoList?.forEach {
                if(it.time != time) {
                    time = it.time ?: ""
                    timeItemView {
                        id(it.id)
                        time(it.time)
                    }
                }
                fangLiangItemView {
                    id(it.id)
                    data(it)
                    click {view ->
                        val intent = Intent(context, KLineActivity::class.java)
                        intent.putExtra("code", it.code)
                        intent.putExtra("info", it.toString())
                        context?.startActivity(intent)
                    }
                }
            }
        }

    }
}