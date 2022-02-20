package com.libiao.mushroom.mine.tab

import androidx.fragment.app.Fragment
import com.libiao.mushroom.mine.fragment.BanTwoChuangFragment

class BanTwoChuangTab: TabItem() {

    companion object {
        const val TAG = "BanTwoChuangTab"
    }

    override fun createFragment(): Fragment {
        if(fragment == null) {
            fragment = BanTwoChuangFragment()
        }
        return fragment!!
    }

    override fun name(): String {
        return "创二板"
    }

    override fun tag(): String {
        return TAG
    }

}