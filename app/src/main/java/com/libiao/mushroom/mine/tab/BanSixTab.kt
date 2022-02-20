package com.libiao.mushroom.mine.tab

import androidx.fragment.app.Fragment
import com.libiao.mushroom.mine.fragment.BanFiveFragment
import com.libiao.mushroom.mine.fragment.BanSixFragment

class BanSixTab: TabItem() {

    companion object {
        const val TAG = "BanSixTab"
    }

    override fun createFragment(): Fragment {
        if(fragment == null) {
            fragment = BanSixFragment()
        }
        return fragment!!
    }

    override fun name(): String {
        return "6板/创3"
    }

    override fun tag(): String {
        return TAG
    }

}