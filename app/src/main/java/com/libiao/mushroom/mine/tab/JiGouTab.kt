package com.libiao.mushroom.mine.tab

import androidx.fragment.app.Fragment
import com.libiao.mushroom.mine.fragment.JiGouFragment
import com.libiao.mushroom.mine.fragment.TestFragment

class JiGouTab: TabItem() {

    companion object {
        const val TAG = "JiGouTab"
    }

    override fun createFragment(): Fragment {
        if(fragment == null) {
            fragment = JiGouFragment()
        }
        return fragment!!
    }

    override fun name(): String {
        return "机构"
    }

    override fun tag(): String {
        return TAG
    }

}