package com.libiao.mushroom.mine.tab

import androidx.fragment.app.Fragment
import com.libiao.mushroom.mine.fragment.BanOneFragment

class BanOneTab: TabItem() {

    companion object {
        const val TAG = "BanOneTab"
    }

    override fun createFragment(): Fragment {
        if(fragment == null) {
            fragment = BanOneFragment()
        }
        return fragment!!
    }

    override fun name(): String {
        return "一板"
    }

    override fun tag(): String {
        return TAG
    }

}