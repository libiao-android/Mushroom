package com.libiao.mushroom.mine.up50

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.libiao.mushroom.room.Up50ShareInfo
import com.libiao.mushroom.utils.dip
import com.libiao.mushroom.utils.huanSuanYi
import kotlinx.android.synthetic.main.report_item_view.view.*
import kotlinx.android.synthetic.main.up50_item_view.view.*

@ModelView
class Up50ItemView @JvmOverloads constructor(
    context:Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0
): RelativeLayout(context, attributeSet, defStyleAttr) {

    override fun onFinishInflate() {
        super.onFinishInflate()
        //initCandleChart()
        initCombinedChart()
        intBarChart()
    }

    var xAxis: XAxis? = null
    private fun initCombinedChart() {
        up50_combined_chart?.also {
            it.setTouchEnabled(false)
            it.description.isEnabled = false
            it.setDrawGridBackground(false) // 是否显示表格颜色
            //it.setBackgroundColor(Color.WHITE) // 设置背景
            it.setPinchZoom(false) // if disabled, scaling can be done on x- and y-axis separately
            it.isLogEnabled = true
            it.isSelected = false
            xAxis = it.xAxis
            xAxis?.isEnabled = false

            val leftAxis: YAxis = it.axisLeft
            leftAxis.isEnabled = false

            val rightAxis: YAxis = it.axisRight
            rightAxis.isEnabled = false

            val l: Legend = it.legend // 设置比例图标示
            l.isEnabled = false //决定显不显示标签
        }
    }

    private fun intBarChart() {
        up50_item_bar_chart.also {
            it.description.isEnabled = false
            it.setTouchEnabled(false)
            //it.setBackgroundColor(Color.WHITE)
            it.setDrawGridBackground(false)
            it.setDrawBarShadow(false)
            it.isHighlightFullBarEnabled = false

            it.legend.isEnabled = false

            var xAxis = it.xAxis
            xAxis.isEnabled = false

            var yAxis = it.axisLeft
            yAxis.isEnabled = false

            yAxis.axisMinimum = 0f

            it.axisRight.isEnabled = false

        }
    }

    private fun initCandleChart() {
        up50_item_candler_chart.also {
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
    fun setData(info: Up50ShareInfo) {
        up50_item_code.text = info.code?.substring(2)
        up50_item_name.text = info.name
        up50_item_total_price.text = huanSuanYi(info.lastShareInfo?.totalPrice ?: 0.00)
        up50_item_range.text = info.lastShareInfo?.range?.toString()
        up50_item_pre_avg.text = huanSuanYi(info.preAvg)
        info.lastShareInfo?.also {
            if(it.range >= 0) {
                up50_item_range.setTextColor(Color.RED)
            } else {
                up50_item_range.setTextColor(Color.GREEN)
            }
        }

        info.candleEntryList?.also {
//            val candleData = CandleData(getCanleDataSet(it))
//
//            val candleParams = up50_item_candler_chart?.layoutParams
//            candleParams?.width = getWidth(it.size)
//            up50_item_candler_chart.layoutParams = candleParams
//
//            up50_item_candler_chart.data = candleData
//            up50_item_candler_chart.invalidate()


            val data = CombinedData()

            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(getLineDataSet(info.values_5!!, Color.BLUE, "line 5"))
            dataSets.add(getLineDataSet(info.values_10!!, Color.RED, "line 10"))
            dataSets.add(getLineDataSet(info.values_20!!, Color.GREEN, "line 20"))
            data.setData(LineData(dataSets))

            data.setData(CandleData(getCanleDataSet(it)))

            xAxis?.axisMaximum = data.xMax + 1f
            xAxis?.axisMinimum = data.xMin - 1f

            val params = up50_combined_chart?.layoutParams
            params?.width = getWidth(it.size)
            params?.height = getHeight(it.size)
            up50_combined_chart?.layoutParams = params

            up50_combined_chart?.data = data
            up50_combined_chart?.invalidate()


            val barData = BarData(getBarDataSet(info.barEntryList, info.colorsList))
            barData.setDrawValues(false)

            val barParams = up50_item_bar_chart?.layoutParams
            barParams?.width = getWidth(it.size)
            up50_item_bar_chart.layoutParams = barParams
            up50_item_bar_chart.data = barData
            up50_item_bar_chart.invalidate()
        }

        if(info.moreInfo == null) {
            up50_item_more_info.visibility = View.GONE
        } else {
            up50_item_more_info.visibility = View.VISIBLE
            up50_item_more_info.text = info.moreInfo
        }

        up50_item_label_view.removeAllViews()
        if(info.label1 != null) {
            val tv = TextView(context)
            tv.text = info.label1
            val param = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            up50_item_label_view.addView(tv, param)
        }
        if(info.label2 != null) {
            val tv = TextView(context)
            tv.text = info.label2
            val param = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            up50_item_label_view.addView(tv, param)
        }
        if(info.label3 != null) {
            val tv = TextView(context)
            tv.text = info.label3
            val param = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            up50_item_label_view.addView(tv, param)
        }
        if(info.label4 != null) {
            val tv = TextView(context)
            tv.text = info.label4
            val param = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            up50_item_label_view.addView(tv, param)
        }
        if(info.label5 != null) {
            val tv = TextView(context)
            tv.text = info.label5
            val param = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            up50_item_label_view.addView(tv, param)
        }
    }

    private fun getLineDataSet(values: ArrayList<Entry>, color: Int, lable: String): ILineDataSet {
        // create a dataset and give it a type
        val set1 = LineDataSet(values, lable)
        set1.isHighlightEnabled = false
        set1.setDrawIcons(false)
        set1.setDrawCircles(false)
        set1.color = color
        set1.lineWidth = 0.5f
//        set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
        set1.formSize = 10f
        //set1.valueTextSize = 9f
        set1.setDrawFilled(false)
        set1.setDrawValues(false)

        return set1
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

    private fun getHeight(size: Int): Int {
        if(size < 10) return 240
        if(size > 100) return 600
        return 240 + (size - 10) * 4
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
        up50_item_code.setOnClickListener {
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