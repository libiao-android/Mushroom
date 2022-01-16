package com.libiao.mushroom.mine

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.libiao.mushroom.mine.tab.Tabs

class SelfViewPagerAdapter(var tabInfo: Tabs, fragmentManager: FragmentManager,
                           lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return tabInfo.tabInfos.size
    }

    override fun createFragment(position: Int): Fragment {
        return tabInfo.tabInfos[position].createFragment()
    }
}