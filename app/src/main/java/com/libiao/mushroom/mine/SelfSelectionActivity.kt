package com.libiao.mushroom.mine

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewpager2.widget.ViewPager2
import com.libiao.mushroom.R
import com.libiao.mushroom.base.BaseActivity
import com.libiao.mushroom.mine.tab.TabItem
import com.libiao.mushroom.mine.tab.Tabs
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil
import com.libiao.mushroom.utils.dip
import kotlinx.android.synthetic.main.self_selection_activity.*
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.self_selection_activity)
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
        val tabs = Tabs()
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

        cb_heart.setOnCheckedChangeListener { buttonView, isChecked ->
            currentTab?.fragment?.also {
                (it as ICommand).order(ICommand.HEART, isChecked)
            }
        }

        cb_network.setOnCheckedChangeListener { buttonView, isChecked ->
            btn_refresh.isEnabled = isChecked
            if(!isChecked) {
                currentTab?.fragment?.also {
                    (it as ICommand).order(ICommand.LOCAL)
                }
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
        }
    }


    fun notifyData(s: String, size: Int) {
        if(currentTab?.tag() == s) {
            hideLoading()
            tv_count.visibility = View.VISIBLE
            tv_count.text = "count: ${size}"
        }
    }


}
