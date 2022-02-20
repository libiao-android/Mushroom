package com.libiao.mushroom.mine.ban

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.libiao.mushroom.room.ban.BanShareInfo
import com.libiao.mushroom.utils.dip
import com.libiao.mushroom.utils.huanSuanYi
import kotlinx.android.synthetic.main.ban_item_view.view.*

@ModelView
class BanItemView @JvmOverloads constructor(
    context:Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0
): RelativeLayout(context, attributeSet, defStyleAttr) {

    override fun onFinishInflate() {
        super.onFinishInflate()
        initCandleChart()
        intBarChart()
    }

    private fun intBarChart() {
        ban_item_bar_chart.also {
            it.description.isEnabled = false
            it.setTouchEnabled(false)
            //it.setBackgroundColor(Color.WHITE)
            it.setDrawGridBackground(false)
            it.setDrawBarShadow(false)
            it.isHighlightFullBarEnabled = false

            it.legend.isEnabled = false

            val xAxis = it.xAxis
            xAxis.isEnabled = false

            val yAxis = it.axisLeft
            yAxis.isEnabled = false

            yAxis.axisMinimum = 0f

            it.axisRight.isEnabled = false

        }
    }

    private fun initCandleChart() {
        ban_item_candler_chart.also {
            it.setTouchEnabled(false)
            it.description.isEnabled = false
            it.setDrawGridBackground(false) // 是否显示表格颜色
            //it.setBackgroundColor(Color.WHITE) // 设置背景
            it.setPinchZoom(false) // if disabled, scaling can be done on x- and y-axis separately
            it.isLogEnabled = true
            it.isSelected = false

            val xAxis: XAxis = it.xAxis
            xAxis.isEnabled = false

            val leftAxis: YAxis = it.axisLeft
            leftAxis.isEnabled = false

            val rightAxis: YAxis = it.axisRight
            rightAxis.isEnabled = false

            val l: Legend = it.legend // 设置比例图标示
            l.isEnabled = false //决定显不显示标签

        }
    }

    @ModelProp
    fun setData(info: BanShareInfo) {
        ban_item_code.text = info.code?.substring(2)
        ban_item_name.text = info.name
        ban_item_total_price.text = huanSuanYi(info.lastShareInfo?.totalPrice ?: 0.00)
        ban_item_range.text = info.lastShareInfo?.range?.toString()
        info.lastShareInfo?.also {
            if(it.range >= 0) {
                ban_item_range.setTextColor(Color.RED)
            } else {
                ban_item_range.setTextColor(Color.GREEN)
            }
        }

        info.candleEntryList?.also {
            val candleData = CandleData(getCanleDataSet(it))

            val candleParams = ban_item_candler_chart?.layoutParams
            candleParams?.width = getWidth(it.size)
            ban_item_candler_chart.layoutParams = candleParams

            ban_item_candler_chart.data = candleData
            ban_item_candler_chart.invalidate()


            val barData = BarData(getBarDataSet(info.barEntryList, info.colorsList))
            barData.setDrawValues(false)

            val barParams = ban_item_bar_chart?.layoutParams
            barParams?.width = getWidth(it.size)
            ban_item_bar_chart.layoutParams = barParams
            ban_item_bar_chart.data = barData
            ban_item_bar_chart.invalidate()
        }

        if(info.moreInfo == null) {
            ban_item_more_info.visibility = View.GONE
        } else {
            ban_item_more_info.visibility = View.VISIBLE
            ban_item_more_info.text = info.moreInfo
        }
    }

    private fun getBarDataSet(barData: List<BarEntry>?, colorData: List<Int>?): MutableList<IBarDataSet>? {
        val set = BarDataSet(barData, "liang neng")
        set.setDrawIcons(false)
        set.isHighlightEnabled = false
        set.colors = colorData
        val dataSets = ArrayList<IBarDataSet>()
        dataSets.add(set)

        return dataSets
    }

    private fun getCanleDataSet(values: List<CandleEntry>): CandleDataSet {
        val set1 = CandleDataSet(values, "")
        set1.formSize = 0f
        set1.isHighlightEnabled = false
        set1.enableDashedHighlightLine(10f, 5f, 0f)
        set1.setDrawValues(false)
        set1.setDrawIcons(false)
        set1.shadowColorSameAsCandle = true
        //set1.shadowColor = Color.BLUE
        set1.shadowWidth = 1f
        set1.decreasingColor = Color.GREEN
        set1.decreasingPaintStyle = Paint.Style.FILL
        set1.increasingColor = Color.RED
        set1.increasingPaintStyle = Paint.Style.STROKE
        set1.neutralColor = Color.RED
        //set1.highLightColor = Color.BLACK
        return set1
    }

    private fun getWidth(size: Int): Int {
        var w:Int = size * 7
        if(w < 120) w = 120
        if(w > 230) w = 230
        return w.dip.toInt()
    }

    @CallbackProp
    fun setClick(callback: ((view: View) -> Unit)?) {
        setOnClickListener {
            callback?.invoke(it)
        }
        ban_item_code.setOnClickListener {
            callback?.invoke(it)
        }
    }

    @CallbackProp
    fun setLongClick(callback: ((view: View) -> Unit)?) {
        setOnLongClickListener {
            callback?.invoke(this)
            true
        }
    }

}