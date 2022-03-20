package com.libiao.mushroom.mine.tab

import androidx.fragment.app.Fragment
import com.libiao.mushroom.mine.fragment.TestFragment

class TestTab: TabItem() {

    companion object {
        const val TAG = "TestTab"
    }

    override fun createFragment(): Fragment {
        if(fragment == null) {
            fragment = TestFragment()
        }
        return fragment!!
    }

    override fun name(): String {
        return "测试"
    }

    override fun tag(): String {
        return TAG
    }

}