package com.libiao.mushroom.mine.tab

import androidx.fragment.app.Fragment
import com.libiao.mushroom.mine.fragment.BanThreeFragment

class BanThreeTab: TabItem() {

    companion object {
        const val TAG = "BanThreeTab"
    }

    override fun createFragment(): Fragment {
        if(fragment == null) {
            fragment = BanThreeFragment()
        }
        return fragment!!
    }

    override fun name(): String {
        return "三板"
    }

    override fun tag(): String {
        return TAG
    }

}