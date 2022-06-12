package com.libiao.mushroom.mine.tab

import androidx.fragment.app.Fragment
import com.libiao.mushroom.mine.fragment.Up50Fragment

class Up50Tab: TabItem() {

    companion object {
        const val TAG = "Up50Tab"
    }

    override fun createFragment(): Fragment {
        if(fragment == null) {
            fragment = Up50Fragment()
        }
        return fragment!!
    }

    override fun name(): String {
        return "50"
    }

    override fun tag(): String {
        return TAG
    }

}