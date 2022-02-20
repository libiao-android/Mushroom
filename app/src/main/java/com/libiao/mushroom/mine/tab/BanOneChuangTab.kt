package com.libiao.mushroom.mine.tab

import androidx.fragment.app.Fragment
import com.libiao.mushroom.mine.fragment.BanOneChuangFragment

class BanOneChuangTab: TabItem() {

    companion object {
        const val TAG = "BanOneChuangTab"
    }

    override fun createFragment(): Fragment {
        if(fragment == null) {
            fragment = BanOneChuangFragment()
        }
        return fragment!!
    }

    override fun name(): String {
        return "创一板"
    }

    override fun tag(): String {
        return TAG
    }

}