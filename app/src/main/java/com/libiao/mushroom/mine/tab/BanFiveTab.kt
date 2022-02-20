package com.libiao.mushroom.mine.tab

import androidx.fragment.app.Fragment
import com.libiao.mushroom.mine.fragment.BanFiveFragment

class BanFiveTab: TabItem() {

    companion object {
        const val TAG = "BanFiveTab"
    }

    override fun createFragment(): Fragment {
        if(fragment == null) {
            fragment = BanFiveFragment()
        }
        return fragment!!
    }

    override fun name(): String {
        return "五板"
    }

    override fun tag(): String {
        return TAG
    }

}