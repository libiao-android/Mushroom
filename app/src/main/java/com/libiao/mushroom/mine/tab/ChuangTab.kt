package com.libiao.mushroom.mine.tab

import androidx.fragment.app.Fragment
import com.libiao.mushroom.R
import com.libiao.mushroom.mine.fragment.ChuangFragment
import com.libiao.mushroom.mine.fragment.Line20Fragment
import com.libiao.mushroom.mine.fragment.XinGaoFragment

class ChuangTab: TabItem() {

    companion object {
        const val TAG = "ChuangTab"
    }

    override fun createFragment(): Fragment {
        if(fragment == null) {
            fragment = ChuangFragment()
        }
        return fragment!!
    }

    override fun name(): String {
        return "创业/科创"
    }

    override fun tag(): String {
        return TAG
    }

}