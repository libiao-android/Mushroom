package com.libiao.mushroom.mine

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.libiao.mushroom.R
import com.libiao.mushroom.base.BaseActivity
import com.libiao.mushroom.mine.tab.TabItem
import com.libiao.mushroom.mine.tab.Tabs
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil
import com.libiao.mushroom.utils.dip
import kotlinx.android.synthetic.main.self_selection_activity.*
import java.lang.reflect.Field
import java.text.SimpleDateFormat
import java.util.*


class SelfSelectionActivity : BaseActivity() {

    companion object {
        private const val TAG = "SelfSelectionActivity"
    }

    private var time: String? = null

    private var mViewPageAdapter: SelfViewPagerAdapter? = null

    private var currentTab: TabItem? = null
    private var smallTab: TabItem? = null
    private var bigTab: TabItem? = null

    private var mScrollPosition = 0

    private val tabs = Tabs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.self_selection_activity)


        try {
            val recyclerViewField: Field = ViewPager2::class.java.getDeclaredField("mRecyclerView")
            recyclerViewField.isAccessible = true
            val recyclerView = recyclerViewField.get(self_view_pager) as RecyclerView
            val touchSlopField: Field = RecyclerView::class.java.getDeclaredField("mTouchSlop")
            touchSlopField.isAccessible = true
            val touchSlop = touchSlopField.get(recyclerView) as Int
            touchSlopField.set(recyclerView, touchSlop * 3) //6 is empirical value
        } catch (ignore: java.lang.Exception) {
        }

        initView()
        self_loading.visibility = View.VISIBLE
    }

    fun showLoading() {
        self_loading.visibility = View.VISIBLE
    }
    fun hideLoading() {
        self_loading.visibility = View.GONE
    }

    private fun initView() {
        mViewPageAdapter = SelfViewPagerAdapter(tabs, supportFragmentManager, lifecycle)
        self_view_pager.adapter = mViewPageAdapter
        self_view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                if(state == ViewPager2.SCROLL_STATE_IDLE) {
                    smallTab = null
                    bigTab = null
                }
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                //LogUtil.i(TAG, "onPageScrolled: $position, $positionOffset, $positionOffsetPixels")
                if(positionOffsetPixels != 0) {
                    if(mScrollPosition != position) {
                        smallTab = null
                    }
                    mScrollPosition = position
                    if(smallTab == null) {
                        smallTab = currentTab
                    }
                    if(tabs.position(smallTab) == position) {
                        val size = position + 1
                        if(size < tabs.tabInfos.size) {
                            bigTab = tabs.tabInfos[size]
                            smallTab?.scale(Constant.ORIGIN + Constant.SCALE, -positionOffset)
                            bigTab?.scale(Constant.ORIGIN, positionOffset)
                        }
                    } else {
                        val size = tabs.position(smallTab) - 1
                        if(size >= 0) {
                            bigTab = tabs.tabInfos[size]
                            smallTab?.scale(Constant.ORIGIN + Constant.SCALE, -(1 - positionOffset))
                            bigTab?.scale(Constant.ORIGIN, 1 - positionOffset)
                        }
                    }
                }
            }

            override fun onPageSelected(position: Int) {
                LogUtil.i(TAG, "onPageSelected: $position")
                currentTab = tabs.tabInfos[position]
                refreshFragmentStatus()
            }
        })

        tabs.tabInfos.forEachIndexed{ index, info ->
            val param = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            if(index == 0) {
                param.leftMargin = 6.dip.toInt()
            } else {
                param.leftMargin = 15.dip.toInt()
            }

            self_tab_layout.addView(info.textView(this), param)
        }

        currentTab = tabs.tabInfos[0]
        currentTab?.scale(Constant.ORIGIN, 1f)

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")// HH:mm:ss
        time = simpleDateFormat.format(Date())
        tv_current_time.text = "$time"



        iv_self_setting?.setOnClickListener {
            currentTab?.fragment?.also {
                (it as ICommand).order(ICommand.SETTING)
            }
        }

        cb_delete.setOnCheckedChangeListener { buttonView, isChecked ->
            currentTab?.fragment?.also {
                (it as ICommand).order(ICommand.HEART, isChecked)
            }
        }

        cb_network.setOnCheckedChangeListener { buttonView, isChecked ->
            btn_refresh.isEnabled = isChecked

            currentTab?.fragment?.also {
                (it as ICommand).order(ICommand.LOCAL, isChecked)
            }
        }

        btn_refresh.setOnClickListener {
            self_loading.visibility = View.VISIBLE
            tv_counting.visibility = View.VISIBLE
            btn_refresh.isEnabled = false
            cb_network.isEnabled = false
            currentTab?.fragment?.also {
                (it as ICommand).order(ICommand.NETWORK)
            }
        }

        iv_collect.setOnClickListener {
            val intent = Intent(this, CollectActivity::class.java)
            startActivity(intent)
        }

    }

    private fun refreshFragmentStatus() {
        currentTab?.fragment?.also {
            val size = (it as ICommand).obtain(ICommand.OBTAIN_SIZE) as Int
            if(size > 0) {
                tv_count.visibility = View.VISIBLE
                tv_count.text = "count: ${size}"
            } else {
                tv_count.visibility = View.GONE
            }

            val loadingStatus = (it as ICommand).obtain(ICommand.LOADING_STATUS) as Int
            if(loadingStatus == 1) {
                showLoading()
            } else {
                hideLoading()
            }

            val heartStatus = (it as ICommand).obtain(ICommand.HEART_STATUS) as Boolean
            cb_delete.isChecked = heartStatus

            val onLineStatus = (it as ICommand).obtain(ICommand.ONLINE_STATUS) as Boolean
            cb_network.isChecked = onLineStatus
        }
    }


    fun notifyData(type: Int, tabTag: String, size: Int) {
        when(type) {
            1 -> {
                //更新size
                if(currentTab?.tag() == tabTag) {
                    hideLoading()
                    tv_count.visibility = View.VISIBLE
                    tv_count.text = "count: ${size}"
                }
            }
            2 -> {
                //更新count
                if(currentTab?.tag() == tabTag) {
                    tv_counting.text = "$size"
                }
            }
            3 -> {
                // 线上刷新结束后更新UI
                if(currentTab?.tag() == tabTag) {
                    self_loading.visibility = View.GONE
                    btn_refresh.isEnabled = true
                    cb_network.isEnabled = true
                    tv_counting.visibility = View.GONE
                }
            }
        }

    }

    fun onItemClick(tabItem: TabItem) {
        val position = tabs.position(tabItem)
        LogUtil.i(TAG, "onItemClick: ${tabItem.name()}, $position")
        currentTab?.scale(Constant.ORIGIN, 0f)
        self_view_pager.setCurrentItem(position, false)
        tabItem.scale(Constant.ORIGIN, 1f)
    }

}
