package com.libiao.mushroom.review

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.airbnb.epoxy.ModelView
import com.github.mikephil.charting.components.XAxis

@ModelView
class ReviewItemView @JvmOverloads constructor(
    context:Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0
): RelativeLayout(context, attributeSet, defStyleAttr) {

    override fun onFinishInflate() {
        super.onFinishInflate()
        initCombinedChart()
        intBarChart()
    }

    var xAxis: XAxis? = null
    private fun initCombinedChart() {

    }

    private fun intBarChart() {

    }
}