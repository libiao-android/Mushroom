package com.libiao.mushroom.mine.tab

import androidx.fragment.app.Fragment
import com.libiao.mushroom.R
import com.libiao.mushroom.mine.fragment.MineFragment

class MineTab: TabItem() {

    companion object {
        const val TAG = "MineTab"
    }

    override fun createFragment(): Fragment {
        if(fragment == null) {
            fragment = MineFragment()
        }
        return fragment!!
    }

    override fun name(): String {
        return "我的"
    }

    override fun tag(): String {
        return TAG
    }

}