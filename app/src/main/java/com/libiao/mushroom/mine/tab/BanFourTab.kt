package com.libiao.mushroom.mine.tab

import androidx.fragment.app.Fragment
import com.libiao.mushroom.mine.fragment.BanFourFragment

class BanFourTab: TabItem() {

    companion object {
        const val TAG = "BanFourTab"
    }

    override fun createFragment(): Fragment {
        if(fragment == null) {
            fragment = BanFourFragment()
        }
        return fragment!!
    }

    override fun name(): String {
        return "四板"
    }

    override fun tag(): String {
        return TAG
    }

}