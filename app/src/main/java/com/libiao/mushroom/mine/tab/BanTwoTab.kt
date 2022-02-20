package com.libiao.mushroom.mine.tab

import androidx.fragment.app.Fragment
import com.libiao.mushroom.mine.fragment.BanTwoFragment

class BanTwoTab: TabItem() {

    companion object {
        const val TAG = "BanTwoTab"
    }

    override fun createFragment(): Fragment {
        if(fragment == null) {
            fragment = BanTwoFragment()
        }
        return fragment!!
    }

    override fun name(): String {
        return "二板"
    }

    override fun tag(): String {
        return TAG
    }

}