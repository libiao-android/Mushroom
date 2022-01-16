package com.libiao.mushroom.mine.tab

import androidx.fragment.app.Fragment
import com.libiao.mushroom.R
import com.libiao.mushroom.mine.fragment.Line20Fragment

class Line20Tab: TabItem() {

    companion object {
        const val TAG = "Line20Tab"
    }

    override fun createFragment(): Fragment {
        if(fragment == null) {
            fragment = Line20Fragment()
        }
        return fragment!!
    }

    override fun name(): String {
        return "20日线"
    }

    override fun tag(): String {
        return TAG
    }

}