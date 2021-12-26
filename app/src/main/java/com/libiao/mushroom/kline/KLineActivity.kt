package com.libiao.mushroom.kline

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.components.Legend.LegendForm
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.libiao.mushroom.R
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.ClipboardUtil
import com.libiao.mushroom.utils.LogUtil
import kotlinx.android.synthetic.main.k_line_main.*
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.nio.charset.Charset


//https://github.com/PhilJay/MPAndroidChart
class KLineActivity : AppCompatActivity() {

    companion object {
        const val TAG = "KLineActivity"
    }

    private val file = File(Environment.getExternalStorageDirectory(), "A_SharesInfo")
    private val file_2021 = File(file, "2021")

    private val infos = ArrayList<SharesRecordActivity.ShareInfo>()
    private val values = ArrayList<CandleEntry>()
    private val values_5 = ArrayList<Entry>()
    private val values_10 = ArrayList<Entry>()
    private val values_20 = ArrayList<Entry>()
    private val values_liang_neng = ArrayList<BarEntry>()
    private val colors_liang_neng = ArrayList<Int>()

    private var code: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.k_line_main)

        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        code = intent.getStringExtra("code")
        Log.i("libiao", "code: $code")
        initData()
        initView()
    }

    private fun initData() {
        val f = File(file_2021, code)
        if(f.exists()) {
            val stream = FileInputStream(f)
            val reader = BufferedReader(InputStreamReader(stream, Charset.defaultCharset()))
            val lines = reader.readLines()
            lines.forEachIndexed { index, s ->
                val info = SharesRecordActivity.ShareInfo(s)
                infos.add(info)
                if(info.beginPrice == 0.00) {
                    values.add(CandleEntry((index).toFloat(), info.nowPrice.toFloat(), info.nowPrice.toFloat(), info.nowPrice.toFloat(), info.nowPrice.toFloat()))
                } else {
                    values.add(CandleEntry((index).toFloat(), info.maxPrice.toFloat(), info.minPrice.toFloat(), info.beginPrice.toFloat(), info.nowPrice.toFloat()))
                }
                var line5 = info.line_5
                if(line5 == 0.00) { line5 = info.beginPrice }

                var line10 = info.line_10
                if(line10 == 0.00) { line10 = info.beginPrice }

                var line20 = info.line_20
                if(line20 == 0.00) { line20 = info.beginPrice }

                values_5.add(Entry(index.toFloat(), line5.toFloat()))
                values_10.add(Entry(index.toFloat(), line10.toFloat()))
                values_20.add(Entry(index.toFloat(), line20.toFloat()))


                val p = info.totalPrice.toFloat() / 100000000
                values_liang_neng.add(BarEntry(index.toFloat(), p))
                if(info.beginPrice > info.nowPrice) {
                    colors_liang_neng.add(Color.GREEN)
                } else if(info.beginPrice < info.nowPrice) {
                    colors_liang_neng.add(Color.RED)
                } else {
                    if(info.range >= 0) {
                        colors_liang_neng.add(Color.RED)
                    } else {
                        colors_liang_neng.add(Color.GREEN)
                    }
                }
            }
            reader.close()
            stream.close()
        }
    }

    private fun initView() {
        info.text = intent.getStringExtra("info")
        info.setOnClickListener {
            ClipboardUtil.clip(this, code)
        }
        //initCandleChart()
        //initLineChart()
        initCombinedChart()
        initBarChart()

    }

    private fun initBarChart() {
        bar_chart.description.isEnabled = false
        bar_chart.setBackgroundColor(Color.WHITE)
        bar_chart.setDrawGridBackground(false)
        bar_chart.setDrawBarShadow(false)
        bar_chart.isHighlightFullBarEnabled = false

        bar_chart.legend.isEnabled = false
        bar_chart.viewPortHandler.setMaximumScaleX(8f)

        var xAxis = bar_chart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.position = XAxisPosition.BOTTOM
        xAxis.labelCount = 8
        xAxis.setDrawAxisLine(false)

        xAxis.valueFormatter = object : ValueFormatter() {

            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                val index = value.toInt()
                if(index < infos.size) {
                    val times = infos[index].time?.split("-")
                    var m = times?.get(1)?.toInt()
                    val d = times?.get(2)?.toInt()
                    val time = "${m}.${d}"
                    return time
                }
                return ""
            }
        }


        var yAxis = bar_chart.axisLeft
        yAxis.setDrawGridLines(false)
        yAxis.labelCount = 2
        yAxis.setDrawAxisLine(false)
        bar_chart.axisRight.isEnabled = false

        val data = BarData(getBarDataSet())

        data.setDrawValues(false)

        xAxis.axisMaximum = data.xMax + 1f
        xAxis.axisMinimum = data.xMin - 1f

        bar_chart.data = data
        bar_chart.invalidate()

    }

    private fun initCombinedChart() {

        combined_chart.description.isEnabled = false
        combined_chart.setBackgroundColor(Color.WHITE)
        combined_chart.setDrawGridBackground(false)
        combined_chart.setDrawBarShadow(false)
        combined_chart.isHighlightFullBarEnabled = false
        combined_chart.isLogEnabled = true
        combined_chart.viewPortHandler.setMaximumScaleX(8f)

        combined_chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onNothingSelected() {
                Log.i("libiao", "onNothingSelected")
            }

            override fun onValueSelected(e: Entry?, h: Highlight?) {
                Log.i("libiao", "onValueSelected: $e")
                val index = e?.x?.toInt()
                index?.also {
                    val info = infos[it]
                    val sb = StringBuilder()

                    sb.append("日期：")
                    sb.append(info.time)
                    sb.append("\n")

                    sb.append("昨日收盘价：")
                    sb.append(info.yesterdayPrice)
                    sb.append("\n")

                    sb.append("当日开盘价：")
                    sb.append(info.beginPrice)
                    sb.append("\n")

                    sb.append("当日收盘价：")
                    sb.append(info.nowPrice)
                    sb.append("\n")

                    sb.append("当日最高价：")
                    sb.append(info.maxPrice)
                    sb.append("\n")

                    sb.append("当日最低价：")
                    sb.append(info.yesterdayPrice)
                    sb.append("\n")

                    sb.append("5日线：")
                    sb.append(info.line_5)
                    sb.append("\n")

                    sb.append("10日线：")
                    sb.append(info.line_10)
                    sb.append("\n")

                    sb.append("20日线：")
                    sb.append(info.line_20)
                    sb.append("\n")

                    share_info_1.text = sb.toString()


                    sb.clear()

                    sb.append("收盘涨跌：")
                    sb.append("${info.range}%")
                    sb.append("\n")

                    sb.append("开盘涨跌：")
                    sb.append("${info.rangeBegin}%")
                    sb.append("\n")

                    sb.append("最高涨跌：")
                    sb.append("${info.rangeMax}%")
                    sb.append("\n")

                    sb.append("最低涨跌：")
                    sb.append("${info.rangeMin}%")
                    sb.append("\n")

                    sb.append("换手率：")
                    sb.append("${info.huanShouLv}%")
                    sb.append("\n")

                    sb.append("成交数：")
                    sb.append(String.format("%.2f",info.totalCount.toDouble() / 10000) + "万手")
                    sb.append("\n")

                    sb.append("成交金额：")
                    sb.append(String.format("%.2f",info.totalPrice / 100000000) + "亿")
                    sb.append("\n")

                    sb.append("流通市值：")
                    sb.append("${info.liuTongShiZhi}亿")
                    sb.append("\n")

                    sb.append("总市值：")
                    sb.append("${info.zongShiZhi}亿")
                    sb.append("\n")

                    share_info_2.text = sb.toString()

                }
            }
        })

        // draw bars behind lines
        combined_chart.drawOrder = arrayOf(
            DrawOrder.BAR, DrawOrder.BUBBLE, DrawOrder.CANDLE, DrawOrder.LINE, DrawOrder.SCATTER
        )

        val l: Legend = combined_chart.legend
        l.isWordWrapEnabled = true
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)

        val leftAxis: YAxis = combined_chart.axisLeft
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawAxisLine(true)
        leftAxis.setLabelCount(10, false)
        combined_chart.axisRight.isEnabled = false

        val xAxis: XAxis = combined_chart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.labelCount = 8
        xAxis.position = XAxisPosition.BOTTOM
        xAxis.valueFormatter = object : ValueFormatter() {

            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                val index = value.toInt()
                if(index < infos.size) {
                    val times = infos[index].time?.split("-")
                    var m = times?.get(1)?.toInt()
                    val d = times?.get(2)?.toInt()
                    val time = "${m}.${d}"
                    return time
                }
                return ""
            }
        }


        val data = CombinedData()

        val dataSets = ArrayList<ILineDataSet>()
        dataSets.add(getLineDataSet(values_5, Color.RED, "line 5"))
        dataSets.add(getLineDataSet(values_10, Color.BLUE, "line 10"))
        dataSets.add(getLineDataSet(values_20, Color.YELLOW, "line 20"))
        data.setData(LineData(dataSets))

        data.setData(CandleData(getCanleDataSet()))

        xAxis.axisMaximum = data.xMax + 1f
        xAxis.axisMinimum = data.xMin - 1f

        combined_chart.data = data
        combined_chart.invalidate()
    }

    private fun getBarDataSet(): MutableList<IBarDataSet>? {
        val set = BarDataSet(values_liang_neng, "liang neng")
        set.setDrawIcons(false)
        set.isHighlightEnabled = false
        set.colors = colors_liang_neng
        val dataSets = ArrayList<IBarDataSet>()
        dataSets.add(set)

        return dataSets
    }

    private fun getCanleDataSet(): CandleDataSet {
        val set1 = CandleDataSet(values, "")
        set1.formSize = 0f
        set1.isHighlightEnabled = true
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

    private fun initLineChart() {
        line_chart.setBackgroundColor(Color.WHITE)
        line_chart.description.isEnabled = false
        line_chart.setTouchEnabled(true)
        line_chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onNothingSelected() {

            }

            override fun onValueSelected(e: Entry?, h: Highlight?) {

            }

        })
        line_chart.setDrawGridBackground(false)
        line_chart.setPinchZoom(false)

        var xAxis = line_chart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        var yAxis = line_chart.axisLeft
        yAxis.setDrawGridLines(false)

        // disable dual axis (only use LEFT axis)
        line_chart.axisRight.isEnabled = false

        // horizontal grid lines
        //yAxis.enableGridDashedLine(10f, 10f, 0f)

        // axis range
        //yAxis.axisMaximum = 200f
        //yAxis.axisMinimum = -50f


        val ll1 = LimitLine(150f, "Upper Limit")
        ll1.lineWidth = 4f
        ll1.enableDashedLine(10f, 10f, 0f)
        ll1.labelPosition = LimitLabelPosition.RIGHT_TOP
        ll1.textSize = 10f


        // draw limit lines behind data instead of on top
        yAxis.setDrawLimitLinesBehindData(true)
        xAxis.setDrawLimitLinesBehindData(true)


        // add limit lines
        //yAxis.addLimitLine(ll1)




        val dataSets = ArrayList<ILineDataSet>()
        dataSets.add(getLineDataSet(values_5, Color.RED, "line 5"))
        dataSets.add(getLineDataSet(values_10, Color.YELLOW, "line 10"))
        dataSets.add(getLineDataSet(values_20, Color.BLUE, "line 20"))
        val data = LineData(dataSets)
        line_chart.data = data

    }

    private fun getLineDataSet(values: ArrayList<Entry>, color: Int, lable: String): ILineDataSet {
        // create a dataset and give it a type
        val set1 = LineDataSet(values, lable)
        set1.isHighlightEnabled = false
        set1.setDrawIcons(false)
        set1.setDrawCircles(false)
        set1.color = color
        set1.lineWidth = 1f
//        set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
        set1.formSize = 10f
        //set1.valueTextSize = 9f
        set1.setDrawFilled(false)
        set1.setDrawValues(false)

        return set1
    }

    private fun initCandleChart() {
        candler_chart.description.isEnabled = false
        candler_chart.setDrawGridBackground(false) // 是否显示表格颜色
        candler_chart.setBackgroundColor(Color.WHITE) // 设置背景
        candler_chart.setPinchZoom(false) // if disabled, scaling can be done on x- and y-axis separately
        candler_chart.isLogEnabled = true

        candler_chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onNothingSelected() {
                Log.i("libiao", "onNothingSelected")
            }

            override fun onValueSelected(e: Entry?, h: Highlight?) {
                Log.i("libiao", "onValueSelected: $e, $h")
            }

        })

        val xAxis: XAxis = candler_chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.gridColor = Color.BLACK //X轴刻度线颜色
        xAxis.textColor = Color.BLACK //X轴文字颜色
        xAxis.labelCount = 10


        val leftAxis: YAxis = candler_chart.axisLeft
        leftAxis.isEnabled = true
        leftAxis.setLabelCount(7, false)
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawAxisLine(false)
        leftAxis.gridColor = Color.BLACK
        leftAxis.textColor = Color.BLACK

        val rightAxis: YAxis = candler_chart.axisRight
        rightAxis.isEnabled = false


        val l: Legend = candler_chart.legend // 设置比例图标示
        l.form = LegendForm.SQUARE // 样式
        l.formSize = 6f // 字号
        l.textColor = Color.BLACK // 颜色
        val labels: MutableList<String> = ArrayList()
        labels.add("红涨")
        labels.add("绿跌")
        val colors: MutableList<Int> = ArrayList()
        colors.add(Color.RED)
        colors.add(Color.GREEN)
        l.setExtra(colors.toIntArray(), labels.toTypedArray()) //设置标注的颜色及内容，设置的效果如下图
        l.isEnabled = false //决定显不显示标签

        val data = CandleData(getCanleDataSet())

        candler_chart.data = data
        candler_chart.invalidate()
    }

    private val f = FloatArray(9)
    fun plus(v: View) {
        combined_chart.viewPortHandler.matrixTouch.getValues(f)
        f[0] += 1f
        if(f[0] > 8f) f[0] = 8f
        //LogUtil.i(TAG, "+++++ ${f.toMutableList()}")
        combined_chart.viewPortHandler.matrixTouch.setValues(f)
        combined_chart.invalidate()

        bar_chart.viewPortHandler.matrixTouch.setValues(f)
        bar_chart.invalidate()
    }

    fun minus(v: View) {
        combined_chart.viewPortHandler.matrixTouch.getValues(f)
        //LogUtil.i(TAG, "----- ${f.toMutableList()}")
        if(f[0] <= 1f) {
            f[0] = 1f
            f[2] = 0f
        } else {
            f[0] -= 1f
        }
        combined_chart.viewPortHandler.matrixTouch.setValues(f)
        combined_chart.invalidate()

        bar_chart.viewPortHandler.matrixTouch.setValues(f)
        bar_chart.invalidate()
    }

    fun addToMine(v: View) {

    }
}