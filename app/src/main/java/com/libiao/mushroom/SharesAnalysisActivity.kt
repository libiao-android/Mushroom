package com.libiao.mushroom

import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.libiao.mushroom.bean.SharesInfo
import com.libiao.mushroom.utils.Constant
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


class SharesAnalysisActivity : AppCompatActivity() {

    companion object {

    }

    private val matchSharesMore = StringBuilder()
    private val matchSharesLess = StringBuilder()
    private val matchSharesMoreLess = StringBuilder()
    private val matchSharesHorizontalPlate = StringBuilder()
    private val mHandler = Handler(Looper.getMainLooper())

    private var timeTv: TextView? = null
    private var afterTv: TextView? = null
    private var waitTv: TextView? = null

    private var moreLessTv: TextView? = null
    private var horizontalPlateTv: TextView? = null

    private var progressTv: TextView? = null

    private var loadingPb: ProgressBar? = null

    private var bigPowerBtn: Button? = null

    private val file = File(Environment.getExternalStorageDirectory(), "SharesInfo")


    private var time = "2021-4-30"

    private val moreList = ArrayList<Pair<Double, SharesRecordActivity.ShareInfo?>>()
    private val lessList = ArrayList<Pair<Double, SharesRecordActivity.ShareInfo?>>()
    private val threeLessList = ArrayList<Pair<Double, SharesRecordActivity.ShareInfo?>>()
    private val moreLessList = ArrayList<Pair<Double, SharesRecordActivity.ShareInfo?>>()

    private var STANDARD_DAY = 8



    private val moreListTest = ArrayList<Pair<Double, String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.more_and_less_activity)
        title = "股票分析"

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")// HH:mm:ss
        time = simpleDateFormat.format(Date())

        //time = "2021-5-20"

        //time = "2021-4-7"

        Log.i("libiao", "t: $time")

        timeTv = findViewById(R.id.time)
        afterTv = findViewById(R.id.tv_after)
        waitTv = findViewById(R.id.tv_wait)

        moreLessTv = findViewById(R.id.tv_more_less)
        moreLessTv?.visibility = View.GONE

        horizontalPlateTv = findViewById(R.id.tv_horizontal_plate)

        progressTv = findViewById(R.id.tv_progress)

        loadingPb = findViewById(R.id.loading)

        bigPowerBtn = findViewById(R.id.btn_big_power_mode)

        timeTv?.text = "日期：$time"
        //queryInfo("sz000001")
        //startQueryAll()
    }

    private fun startQueryAll() {
        moreList.clear()
        lessList.clear()
        threeLessList.clear()
        moreLessList.clear()
        Thread {
            Log.i("libiao", "开始查询")
            matchSharesMore.clear()
            matchSharesLess.clear()
            matchSharesMoreLess.clear()
            matchSharesHorizontalPlate.clear()
            matchSharesMore.append("符合放量追涨：\n")
            matchSharesMoreLess.append("放量后缩量：\n")
            matchSharesLess.append("符合缩量埋伏：\n")
            matchSharesHorizontalPlate.append("平盘震荡：\n")

            val stream = FileInputStream(File(file, "stock_pool"))
            val reader = BufferedReader(InputStreamReader(stream, Charset.defaultCharset()))
            var str: String?
            str = reader.readLine()
            var count = 0
            while (str != null) {
                count++
                if(count % 100 == 0) {
                    val p = (count / 4000F * 100).toInt()
                    mHandler.post { progressTv?.text = "$p%, $count" }
                }
                //Log.i("libiao", "str: $str, $count")
                //queryInfo(str.split(",")[0])

                analysis(str.split(",")[0])

                str = reader.readLine()
            }

            Log.i("libiao", "结束查询")
            mHandler.post{
                progressTv?.text = "$100%, $count"
                loadingPb?.visibility = View.GONE
                bigPowerBtn?.isEnabled = true
            }

            showMore()
            //showMoreLess()
            showLess()
            showHorizontalPlate()


//            moreListTest.sortByDescending { it.first }
//            for( p in moreListTest) {
//                Log.i("libiao_A", "${p.first}, ${p.second}")
//            }



        }.start()
    }

    private fun showHorizontalPlate() {
        mHandler.post{
            horizontalPlateTv?.text = matchSharesHorizontalPlate.toString()
        }
    }

    private fun showMoreLess() {
        Log.i("libiao", "moreLessList: ${moreLessList.size}")
        moreLessList.sortByDescending { it.first }
        var moreLessSize = moreLessList.size

        for(i in 0 until moreLessSize) {
            matchSharesMoreLess.append("${moreLessList[i].second?.code}, ${moreLessList[i].second?.name} \n")
        }
        mHandler.post{
            moreLessTv?.text = matchSharesMoreLess.toString()
        }
    }

    private fun showMore() {
        Log.i("libiao", "moreList: ${moreList.size}")
        moreList.sortByDescending { it.first }
        var moreSize = moreList.size
        if(moreSize > 5) { moreSize = 5 }

        for(i in 0 until moreSize) {
            matchSharesMore.append("${moreList[i].second?.code}, ${moreList[i].second?.name} \n")
        }
        mHandler.post{
            afterTv?.text = matchSharesMore.toString()
        }
    }

    private fun showLess() {
        Log.i("libiao", "lessList: ${lessList.size}")

        lessList.sortByDescending { it.first }

        //Log.i("libiao", "lessList: ${lessList}")

        var lessSize = lessList.size
        //if(lessSize > 10) { lessSize = 10 }
        matchSharesLess.append("两天缩量：\n")
        for(i in 0 until lessSize) {
            matchSharesLess.append("${lessList[i].second?.code}, ${lessList[i].second?.name} \n")
        }

        val three = threeLessList.size
        if(three > 0) {
            matchSharesLess.append("三天缩量：\n")
            for(i in 0 until three) {
                matchSharesLess.append("${threeLessList[i].second?.code}, ${threeLessList[i].second?.name} \n")
            }
        }

        mHandler.post{
            waitTv?.text = matchSharesLess.toString()
        }
    }


    private fun analysis(code: String) {
        val f = File(file, code)
        if(f.exists()) {
            val stream = FileInputStream(f)
            val reader = BufferedReader(InputStreamReader(stream, Charset.defaultCharset()))
            val lines = reader.readLines()
            //Log.i("libiao", "$lines")
            val shares = ArrayList<SharesRecordActivity.ShareInfo>()
            for(line in lines) {
                shares.add(SharesRecordActivity.ShareInfo(line))
            }

            modeMore(shares)
            //modeMoreLess(shares)
            modeLessLess(shares)
            modeHorizontalPlate(shares)





//            if(size > STANDARD_DAY + 1) {
//                //放量
//                twoDayMore(shares[STANDARD_DAY], shares[STANDARD_DAY + 1])
//            } else {
//                Log.i("libiao", "more no data")
//            }
//
//            if(size > STANDARD_DAY + 2) {
//                //缩量
//                threeDayLess(shares[STANDARD_DAY], shares[STANDARD_DAY + 1], shares[STANDARD_DAY + 2])
//            } else {
//                Log.i("libiao", "less no data")
//            }
        }
    }

    private fun modeHorizontalPlate(shares: java.util.ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        STANDARD_DAY = size - 4
        if(STANDARD_DAY >  0) {
            val one = shares[STANDARD_DAY + 0]
            val two = shares[STANDARD_DAY + 1]
            val three = shares[STANDARD_DAY + 2]
            val four = shares[STANDARD_DAY + 3]


            var middleOne = (one.beginPrice + one.nowPrice) / 2

            var middletwo = (two.beginPrice + two.nowPrice) / 2

            var middleThree = (three.beginPrice + three.nowPrice) / 2

            var middleFour = (four.beginPrice + four.nowPrice) / 2

            if(four.nowPrice > 30) {
                middleOne = String.format("%.1f",middleOne).toDouble()
                middletwo = String.format("%.1f",middletwo).toDouble()
                middleThree = String.format("%.1f",middleThree).toDouble()
                middleFour = String.format("%.1f",middleFour).toDouble()
            }




            var totalPriceMin = min(one.totalPrice, two.totalPrice)
            totalPriceMin = min(totalPriceMin, three.totalPrice)
            totalPriceMin = min(totalPriceMin, four.totalPrice)
            //val incremental = (middleOne < middletwo && middletwo < middleThree) || (middletwo < middleThree && middleThree < middleFour)
            val incremental = false
            val decreasing = (middleOne > middletwo && middletwo > middleThree) || (middletwo > middleThree && middleThree > middleFour)

            if(totalPriceMin > 100000000 && four.nowPrice > 10 && !incremental && !decreasing) {

                var middleMax = max(middleOne, middletwo)
                middleMax = max(middleMax, middleThree)
                middleMax = max(middleMax, middleFour)

                var middleMin = min(middleOne, middletwo)
                middleMin = min(middleMin, middleThree)
                middleMin = min(middleMin, middleFour)


                if((middleMax - middleMin) / middleMin < 0.05) {
                    if(abs(one.range) < 1 && abs(two.range) < 1 && abs(three.range) < 1 && abs(four.range) < 1) {
                        //Log.i(Constant.TAG, "震荡模式：${shares.last().brieflyInfo()}")
                        matchSharesHorizontalPlate.append("${four.code}, ${four.name} \n")
                    }
                }

            }


            //val five = shares[STANDARD_DAY + 4]
//            if(totalPriceMin > 100000000 && four.nowPrice > 10 && !incremental && !decreasing) {
//                var nowPriceMax = max(one.nowPrice, two.nowPrice)
//                nowPriceMax = max(nowPriceMax, three.nowPrice)
//                nowPriceMax = max(nowPriceMax, four.nowPrice)
//                //nowPriceMax = max(nowPriceMax, five.nowPrice)
//                var nowPriceMin = min(one.nowPrice, two.nowPrice)
//                nowPriceMin = min(nowPriceMin, three.nowPrice)
//                nowPriceMin = min(nowPriceMin, four.nowPrice)
//                //nowPriceMin = min(nowPriceMin, five.nowPrice)
//
//                val oneH = (abs(one.beginPrice - one.nowPrice)) / one.beginPrice
//                val twoH = (abs(two.beginPrice - two.nowPrice)) / two.beginPrice
//                val threeH = (abs(three.beginPrice - three.nowPrice)) / three.beginPrice
//                val fourH = (abs(four.beginPrice - four.nowPrice)) / four.beginPrice
//
//
////                if(nowPriceMax > 0 && nowPriceMin > 0) {
////                    //if(abs(one.range) < 1 && abs(two.range) < 1 && abs(three.range) < 1 && abs(four.range) < 1) {
////                        if(oneH < 0.01 && twoH < 0.01 && threeH < 0.01 && fourH < 0.01) {
////                            if((nowPriceMax - nowPriceMin) / nowPriceMin < 0.02) {
////
////
////
////                                var maxPrice = max(one.maxPrice, two.maxPrice)
////                                maxPrice = max(maxPrice, three.maxPrice)
////                                maxPrice = max(maxPrice, four.maxPrice)
////                                //maxPrice = max(maxPrice, five.maxPrice)
////
////                                var minPrice = min(one.minPrice, two.minPrice)
////                                minPrice = min(minPrice, three.minPrice)
////                                minPrice = min(minPrice, four.minPrice)
////                                //minPrice = min(minPrice, five.minPrice)
////
////                                if((maxPrice - minPrice) / minPrice < 0.04) {
////                                    Log.i(Constant.TAG, "震荡模式：${shares.last().brieflyInfo()}")
////                                }
////
////                            }
////                        }
////                    //}
////
////                }
//            }

        }
    }

    private fun modeLessLess(shares: java.util.ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        STANDARD_DAY = size - 3
        if(STANDARD_DAY >= 0) {
            val today = shares[STANDARD_DAY + 2]
            val pre1 = shares[STANDARD_DAY + 1]
            val pre2 = shares[STANDARD_DAY]

            if(today.nowPrice > 4 && today.totalPrice > 150000000) {
                if(pre2.nowPrice > pre2.beginPrice && pre2.range > 0 && (pre2.range < 2 || pre2.maxPrice - pre2.nowPrice < pre2.nowPrice - pre2.beginPrice)) {
                    val a = pre1.totalPrice / pre2.totalPrice
                    val b = today.totalPrice / pre1.totalPrice
                    if(a < 0.8 && a > 0.7) {
                        if(b < 0.8 && b > 0.7) {
                            if(today.zongShiZhi > 30 && today.zongShiZhi > 100) {
                                //Log.i(Constant.TAG, "两天缩量：${last.brieflyInfo()}, ${post.range}, ${post2.range}")
                                lessList.add(Pair(today.totalPrice, today))
                            }
                        }
                    }
                }
            }
        }

        STANDARD_DAY--

        if(STANDARD_DAY >= 0) {
            val today = shares[STANDARD_DAY + 3]
            val pre1 = shares[STANDARD_DAY + 2]
            val pre2 = shares[STANDARD_DAY + 1]
            val pre3 = shares[STANDARD_DAY]

            if(today.nowPrice > 4 && today.totalPrice > 100000000) {
                if(pre2.nowPrice > pre2.beginPrice && pre2.range > 0 && (pre2.range < 2 || pre2.maxPrice - pre2.nowPrice < pre2.nowPrice - pre2.beginPrice)) {

                    val a = pre2.totalPrice / pre3.totalPrice
                    val b = pre1.totalPrice / pre2.totalPrice
                    val c = today.totalPrice / pre1.totalPrice
                    if(a < 0.8 && a > 0.6) {
                        if(b < 0.8 && b > 0.6) {
                            if(c < 0.8 && c > 0.6) {
                                //Log.i(Constant.TAG, "三天缩量：${last.brieflyInfo()}, ${post.range}, ${post2.range}")
                                threeLessList.add(Pair(today.totalPrice, today))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun modeMoreLess(shares: java.util.ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        STANDARD_DAY = size - 3
        if(STANDARD_DAY >= 0) {
            val today = shares[STANDARD_DAY + 2]
            if(more(shares[STANDARD_DAY], shares[STANDARD_DAY + 1])) {
                if(today.totalPrice * 1.8 < shares[STANDARD_DAY + 1].totalPrice) {
                    Log.i(Constant.TAG, "放量后缩量：$today")
                    moreLessList.add(Pair(today.totalPrice, today))
                }
            }
        }
    }

    private fun modeMore(shares: java.util.ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        STANDARD_DAY = size - 2
        if(STANDARD_DAY >= 0) {
            if(more(shares[STANDARD_DAY], shares[STANDARD_DAY + 1])) {
                var total = 0.00
                var begin = STANDARD_DAY - 6
                if(begin < 0) begin = 0
                for(i in begin .. STANDARD_DAY) {
                    total += shares[i].nowPrice
                }
                val avg = total / (STANDARD_DAY - begin + 1)
                val avgR = (shares[STANDARD_DAY + 1].nowPrice - avg)/avg * 100
                if(shares.last().zongShiZhi < 100 && avgR < 16) {
                    moreList.add(Pair(shares.last().range, shares.last()))
                }
//                val third = shares[STANDARD_DAY + 2]
//                if(third.beginPrice > 0) {
//                    val gaoKai = ((third.beginPrice - third.yesterdayPrice) / third.yesterdayPrice) * 100
//                    val r = ((third.nowPrice - third.beginPrice) / third.beginPrice) * 100
//                    val s = String.format("%.2f",r)
//                    val range = s.toDouble()
//                    if(shares.last().zongShiZhi < 100 && avgR < 16) {
//                        moreListTest.add(Pair(range, "${third.brieflyInfo()}, ${shares.last().zongShiZhi}, $avgR, $gaoKai"))
//                    }
//                }
            }
        }
    }

    private fun threeDayLess(
        today: SharesRecordActivity.ShareInfo,
        pre1: SharesRecordActivity.ShareInfo,
        pre2: SharesRecordActivity.ShareInfo
    ) {
        if(today.nowPrice > 4 && today.totalPrice > 150000000) {
            if(pre2.nowPrice > pre2.beginPrice && pre2.range > 0 && (pre2.range < 2 || pre2.maxPrice - pre2.nowPrice < pre2.nowPrice - pre2.beginPrice)) {
                val a = pre1.totalPrice / pre2.totalPrice
                val b = today.totalPrice / pre1.totalPrice
                if(a < 0.8 && a > 0.7) {
                    if(b < 0.8 && b > 0.7) {
                        lessList.add(Pair(today.totalPrice, today))
//                        if(today.range > 0) {
//                            Log.i("libiao", "缩量红：$a, $b, ${today.toFile()}")
//                        }
//                        if(today.nowPrice < pre1.nowPrice && pre1.nowPrice < pre2.nowPrice) {
//                            Log.i("libiao", "缩量绿：${today.toFile()}")
//                        } else {
//                            Log.i("libiao", "缩量红：${today.toFile()}")
//                        }
                    }
                }
            }
        }
    }


    private fun twoDayMore(
        today: SharesRecordActivity.ShareInfo,
        pre1: SharesRecordActivity.ShareInfo
    ) {
        if(today.range > 5) { //当天涨幅大于5%
            var multiple = 0.00
            if(pre1.totalPrice > 0) {
                multiple = today.totalPrice / pre1.totalPrice
            }
            if(pre1.totalPrice > 100000000 && multiple > 2.5) {
                //Log.i("libiao", "放量：${today.toFile()}, multiple: $multiple")
                moreList.add(Pair(today.totalPrice, today))
            }
        }
    }

    private fun more(firstDay: SharesRecordActivity.ShareInfo, secondDay: SharesRecordActivity.ShareInfo): Boolean {

        if(secondDay.range > 5) { //当天涨幅大于5%
            var multiple = 0.00
            if(firstDay.totalPrice > 0) {
                multiple = secondDay.totalPrice / firstDay.totalPrice
            }
            if(firstDay.totalPrice > 50000000 && multiple > 2.5 && multiple < 4) {
                //Log.i("libiao", "放量：${today.toFile()}, multiple: $multiple")
                return true
            }
        }
        return false
    }

    private fun less(firstDay: SharesInfo?, secondDay: SharesInfo?): Boolean {
        if(firstDay == null || secondDay == null) return false
//        Log.i("libiao", firstDay.toString())
//        Log.i("libiao", secondDay.toString())
//        val one = secondDay.totalHands * 2 < firstDay.totalHands
//        val two = secondDay.totalHands * secondDay.openingPrice > 500000
//        val three = firstDay.closingPrice > firstDay.openingPrice
//        val four = abs(secondDay.closingPrice - secondDay.openingPrice) < secondDay.openingPrice * 0.025
//        val five = abs(secondDay.highestPrice - max(secondDay.openingPrice, secondDay.closingPrice)) < abs(secondDay.minimumPrice - min(secondDay.openingPrice, secondDay.closingPrice))

//        Log.i("libiao", "one: $one")
//        Log.i("libiao", "two: $two")
//        Log.i("libiao", "three: $three")
//        Log.i("libiao", "four: $four")
//        Log.i("libiao", "five: $five")
        val up = secondDay.highestPrice - max(secondDay.openingPrice, secondDay.closingPrice)
        val down = min(secondDay.openingPrice, secondDay.closingPrice) - secondDay.minimumPrice
        val middle = secondDay.closingPrice - secondDay.openingPrice
        val f = down > up && up > abs(middle)
        val strict = middle < 0 && secondDay.closingPrice > 10 && secondDay.range < 0
        if(secondDay.totalHands * 2 < firstDay.totalHands
            && secondDay.totalHands * secondDay.openingPrice > 500000
            //&& firstDay.closingPrice > firstDay.openingPrice
            && abs(secondDay.closingPrice - secondDay.openingPrice) < secondDay.openingPrice * 0.025
            && f
        ) {
            // 缩量
            return true
        }
        return false
    }


    fun bigPower(view: View) {
        startQueryAll()
        //test()
        loadingPb?.visibility = View.VISIBLE
        bigPowerBtn?.isEnabled = false
    }

    private fun test() {
        analysis("sz300767")
    }
}
//2021-05-01 18:15:06.877 23142-23236/com.libiao.mushroom I/libiao: 开始查询
//2021-05-01 18:32:19.894 23142-23236/com.libiao.mushroom I/libiao: 结束查询


//2021-05-03 10:58:46.518 21442-21502/com.libiao.mushroom I/libiao: 开始查询
//2021-05-03 11:07:27.071 21442-21502/com.libiao.mushroom I/libiao: 结束查询
