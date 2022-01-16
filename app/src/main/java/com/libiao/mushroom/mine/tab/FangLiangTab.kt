package com.libiao.mushroom.mine.tab

import androidx.fragment.app.Fragment
import com.libiao.mushroom.R
import com.libiao.mushroom.mine.fragment.FangLiangFragment

class FangLiangTab: TabItem() {

    companion object {
        const val TAG = "FangLiangTab"
    }

    override fun createFragment(): Fragment {
        if(fragment == null) {
            fragment = FangLiangFragment()
        }
        return fragment!!
    }

    override fun name(): String {
        return "放量"
    }

    override fun tag(): String {
        return TAG
    }
}