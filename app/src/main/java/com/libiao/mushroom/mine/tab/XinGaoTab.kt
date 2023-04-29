package com.libiao.mushroom.mine.tab

import androidx.fragment.app.Fragment
import com.libiao.mushroom.R
import com.libiao.mushroom.mine.fragment.Line20Fragment
import com.libiao.mushroom.mine.fragment.XinGaoFragment

class XinGaoTab: TabItem() {

    companion object {
        const val TAG = "XinGaoTab"
    }

    override fun createFragment(): Fragment {
        if(fragment == null) {
            fragment = XinGaoFragment()
        }
        return fragment!!
    }

    override fun name(): String {
        return "新高"
    }

    override fun tag(): String {
        return TAG
    }

}