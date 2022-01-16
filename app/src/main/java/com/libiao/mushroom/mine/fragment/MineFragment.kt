package com.libiao.mushroom.mine.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.libiao.mushroom.R
import com.libiao.mushroom.mine.ICommand
import com.libiao.mushroom.utils.LogUtil

class MineFragment: Fragment(R.layout.mine_fragment), ICommand {
    companion object {
        private const val TAG = "MineFragment"
    }

    override fun order(type: Int, data: Any?) {

    }

    override fun obtain(type: Int): Any? {
        return 0
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogUtil.i(TAG, "onViewCreated")
    }
}