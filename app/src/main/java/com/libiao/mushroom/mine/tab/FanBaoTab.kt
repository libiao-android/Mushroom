package com.libiao.mushroom.mine.tab

import androidx.fragment.app.Fragment
import com.libiao.mushroom.R
import com.libiao.mushroom.mine.fragment.Line20Fragment
import com.libiao.mushroom.mine.fragment.TryFanBaoFragment
import com.libiao.mushroom.mine.fragment.XinGaoFragment

class FanBaoTab: TabItem() {

    companion object {
        const val TAG = "FanBaoTab"
    }

    override fun createFragment(): Fragment {
        if(fragment == null) {
            fragment = TryFanBaoFragment()
        }
        return fragment!!
    }

    override fun name(): String {
        return "反包"
    }

    override fun tag(): String {
        return TAG
    }

}