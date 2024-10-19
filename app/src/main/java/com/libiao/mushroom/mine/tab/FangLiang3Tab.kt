package com.libiao.mushroom.mine.tab

import androidx.fragment.app.Fragment
import com.libiao.mushroom.R
import com.libiao.mushroom.mine.fragment.FangLiang3Fragment
import com.libiao.mushroom.mine.fragment.FangLiangFragment

class FangLiang3Tab: TabItem() {

    companion object {
        const val TAG = "FangLiang3Tab"
    }

    override fun createFragment(): Fragment {
        if(fragment == null) {
            fragment = FangLiang3Fragment()
        }
        return fragment!!
    }

    override fun name(): String {
        return "放量2"
    }

    override fun tag(): String {
        return TAG
    }
}