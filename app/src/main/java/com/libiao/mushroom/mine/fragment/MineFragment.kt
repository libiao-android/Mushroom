package com.libiao.mushroom.mine.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.libiao.mushroom.R
import com.libiao.mushroom.mine.ICommand
import com.libiao.mushroom.mine.SelfSelectionActivity
import com.libiao.mushroom.mine.tab.Line20Tab
import com.libiao.mushroom.mine.tab.MineTab
import com.libiao.mushroom.utils.LogUtil

class MineFragment: BaseFragment(R.layout.mine_fragment), ICommand {
    companion object {
        private const val TAG = "MineFragment"
    }

    override fun order(type: Int, data: Any?) {
        when(type) {
            ICommand.SETTING -> {

            }
            ICommand.HEART -> {
                heartChecked = data as Boolean

            }
            ICommand.LOCAL -> {
                onLineChecked = data as Boolean

            }
            ICommand.NETWORK -> {
                (activity as SelfSelectionActivity).notifyData(3, MineTab.TAG, 0)
            }
        }
    }

    override fun obtain(type: Int): Any? {
        when(type) {
            ICommand.OBTAIN_SIZE -> {
                return 0
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogUtil.i(TAG, "onViewCreated")
    }
}