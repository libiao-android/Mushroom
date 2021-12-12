package com.libiao.mushroom

import android.os.*
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil.i
import okhttp3.*
import java.io.*
import java.lang.Exception
import java.lang.StringBuilder
import java.nio.charset.Charset
import java.util.*


class SharesRecordActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SharesRecordActivity"
    }

    private val mHandler = Handler()
    private val client = OkHttpClient()

    private val file = File(Environment.getExternalStorageDirectory(), "A_SharesInfo")
    private val file_2021 = File(file, "2021")

    private var progressTv: TextView? = null

    private var loadingPb: ProgressBar? = null
    private var btn: Button? = null
    private var time: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shares_record_activity)
        supportActionBar?.hide()


        if (!file.exists()) {
            file.mkdir()
        }

        progressTv = findViewById(R.id.tv_progress)

        loadingPb = findViewById(R.id.loading)
        btn = findViewById(R.id.btn)
    }

    fun startRecord(v: View) {
        if(isRecord()) {
            Toast.makeText(this, "已经记录过", Toast.LENGTH_LONG).show()
            return
        }
        btn?.isEnabled = false
        loadingPb?.visibility = View.VISIBLE
        Thread {
            i(TAG, "开始查询")
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
                //Log.i("libiao_A", "str: $str, $count")
                queryInfo(str.split(",")[0])
                str = reader.readLine()
            }
            reader.close()
            stream.close()
            mHandler.post{
                progressTv?.text = "$100%, $count"
                loadingPb?.visibility = View.GONE
                btn?.isEnabled = true
            }
            i(TAG, "结束查询")
        }.start()

    }

    private fun isRecord(): Boolean {

        val c = Calendar.getInstance();
        c.timeZone = TimeZone.getTimeZone("GMT+8:00");
        val mYear = c.get(Calendar.YEAR) // 获取当前年份
        val mMonth = c.get(Calendar.MONTH) + 1// 获取当前月份
        var mDay = c.get(Calendar.DAY_OF_MONTH)// 获取当前月份的日期号码
        var mWay = getWay(c.get(Calendar.DAY_OF_WEEK))

        if (mWay == 6) {
            mWay = 5
            c.set(Calendar.DATE, c.get(Calendar.DATE) - 1);
            mDay = c.get(Calendar.DAY_OF_MONTH)// 获取当前月份的日期号码

        } else if(mWay == 7) {
            mWay = 5
            c.set(Calendar.DATE, c.get(Calendar.DATE) - 2);
            mDay = c.get(Calendar.DAY_OF_MONTH)// 获取当前月份的日期号码
        }

        val time = "$mYear-$mMonth-$mDay-$mWay"
        Log.i("libiao_A", "time: $time")
        this.time = time

        val f = File(file_2021, "sz000001")
        if(f.exists()) {
            val stream = FileInputStream(f)
            val reader = BufferedReader(InputStreamReader(stream, Charset.defaultCharset()))
            val lines = reader.readLines()
            //Log.i("libiao_A", "$lines")
            val lastLine = lines.last()
            Log.i("libiao_A", "lastLine: $lastLine")
            reader.close()
            stream.close()
            val info = ShareInfo(lastLine)
            if(info.time != time) return false
        }
        return true
    }

    private fun getWay(get: Int): Int {
        return when (get) {
            1 -> 7
            2 -> 1
            3 -> 2
            4 -> 3
            5 -> 4
            6 -> 5
            7 -> 6
            else -> 0
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun queryInfo(code: String) {
        networkRequestForQt(code)
        Thread.sleep(80)
    }

    private fun networkRequestForQt(code: String) {
        val request = Request.Builder()
            .url("https://qt.gtimg.cn/q=$code")
            .build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.i("libiao_A", "onFailure: ${e}, $code")
            }

            override fun onResponse(call: Call?, response: Response?) {
                val value = response?.body()?.string()
                //Log.i("libiao_A", "response: ${value}")
                try {
                    if (value != null && value.length > 50) {
                        parseSharesInfoForQt(code, value)
                    } else {
                        Log.i("libiao_A", "exception value: $value, $code")
                    }
                } catch (e: Exception) {
                    Log.i("libiao_A", "parseSharesInfo exception: ${e.message}, $code, $value")
                }
            }
        })
    }

    private fun parseSharesInfoForQt(code: String, response: String?) {
        val values = response?.split("~")
        values?.also {
            if(it.size > 45) {
                val info = ShareInfo()
                info.code = code
                info.name = it[1]
                info.nowPrice = it[3].toDouble()
                info.yesterdayPrice = it[4].toDouble()
                info.beginPrice = it[5].toDouble()
                info.range = it[32].toDouble()
                info.maxPrice = it[33].toDouble()
                info.minPrice = it[34].toDouble()
                info.totalCount = it[36].toInt()
                info.totalPrice = it[37].toDouble() * 10000
                info.time = this.time
                info.huanShouLv = it[38].toDouble()
                info.liuTongShiZhi = it[44].toDouble()
                info.zongShiZhi = it[45].toDouble()

                if(info.beginPrice > 0) {
                    val rangeBegin = String.format("%.2f", (info.beginPrice - info.yesterdayPrice) / info.yesterdayPrice * 100)
                    val rangeMin = String.format("%.2f", (info.minPrice - info.yesterdayPrice) / info.yesterdayPrice * 100)
                    val rangeMax = String.format("%.2f", (info.maxPrice - info.yesterdayPrice) / info.yesterdayPrice * 100)

                    info.rangeBegin = rangeBegin.toDouble()
                    info.rangeMin = rangeMin.toDouble()
                    info.rangeMax = rangeMax.toDouble()
                }

                //Log.i(Constant.TAG, info.toString())

                val file = File(file_2021, code)
                if(file.exists()) {
                    val stream = FileInputStream(file)
                    val reader = BufferedReader(InputStreamReader(stream, Charset.defaultCharset()))
                    val lines = reader.readLines()
                    supplyAvgLines(info, lines)
                    reader.close()
                    stream.close()
                    //i(TAG, info.toFile())
                    writeFileAppend(file, info.toFile())
                } else {
                    Log.i("libiao_A", "file not exist: $info")
                }

            } else {
                Log.i(Constant.TAG, "parseSharesInfoForQt exception: $values, $code")
            }
        }
    }

    private fun supplyAvgLines(info: ShareInfo, lines: List<String>) {
        when {
            lines.size >= 19 -> {
                val avg_lines = lines.subList(lines.size - 19, lines.size)
                val shares = ArrayList<ShareInfo>()
                for(line in avg_lines) {
                    shares.add(ShareInfo(line))
                }
                var sum_20 = info.nowPrice
                var sum_10 = info.nowPrice
                var sum_5 = info.nowPrice
                shares.forEachIndexed { index, shareInfo ->
                    sum_20 += shareInfo.nowPrice
                    if(index >= 10) {
                        sum_10 += shareInfo.nowPrice
                    }
                    if(index >= 15) {
                        sum_5 += shareInfo.nowPrice
                    }
                }
                val str_20 = String.format("%.2f", sum_20 / 20)
                info.line_20 = str_20.toDouble()

                val str_10 = String.format("%.2f", sum_10 / 10)
                info.line_10 = str_10.toDouble()

                val str_5 = String.format("%.2f", sum_5 / 5)
                info.line_5 = str_5.toDouble()

            }
            lines.size >= 9 -> {
                val avg_lines = lines.subList(lines.size - 9, lines.size)
                val shares = ArrayList<ShareInfo>()
                for(line in avg_lines) {
                    shares.add(ShareInfo(line))
                }
                var sum_10 = info.nowPrice
                var sum_5 = info.nowPrice
                shares.forEachIndexed { index, shareInfo ->
                    sum_10 += shareInfo.nowPrice
                    if(index >= 5) {
                        sum_5 += shareInfo.nowPrice
                    }
                }

                val str_10 = String.format("%.2f", sum_10 / 10)
                info.line_10 = str_10.toDouble()

                val str_5 = String.format("%.2f", sum_5 / 5)
                info.line_5 = str_5.toDouble()
            }
            lines.size >= 4 -> {
                val avg_lines = lines.subList(lines.size - 4, lines.size)
                val shares = ArrayList<ShareInfo>()
                for(line in avg_lines) {
                    shares.add(ShareInfo(line))
                }
                var sum_5 = info.nowPrice
                shares.forEachIndexed { index, shareInfo ->
                    sum_5 += shareInfo.nowPrice
                }
                val str_5 = String.format("%.2f", sum_5 / 5)
                info.line_5 = str_5.toDouble()
            }
        }
    }

    private fun writeFileAppend(file: File, info: String) {
        var fileWriter: FileWriter? = null
        try {
            fileWriter = FileWriter(file, true)
            fileWriter.append("$info\n")
            fileWriter.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun deleteFileLastLine(code: String) {
        val f = File(file_2021, code)
        if(f.exists()) {
            val stream = FileInputStream(f)
            val reader = BufferedReader(InputStreamReader(stream, Charset.defaultCharset()))
            val lines = reader.readLines()
            if(lines.size != 11) {
                Log.i(Constant.TAG, "$code, ${lines.size}")
            }
            val sb = StringBuilder()
            for(i in 0 until lines.size - 1) {
                //Log.i(Constant.TAG, lines[i])
                sb.append("${lines[i]}\n")
            }

            var fileWriter: FileWriter? = null
            try {
                fileWriter = FileWriter(f, false)
                fileWriter.append("$sb")
                fileWriter.flush()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (fileWriter != null) {
                    try {
                        fileWriter.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun deleteAllFileLastLine() {
        Thread{
            val stream = FileInputStream(File(file, "stock_pool"))
            val reader = BufferedReader(InputStreamReader(stream, Charset.defaultCharset()))
            var str: String?
            str = reader.readLine()
            var count = 0
            while (str != null) {
                //Thread.sleep(50)
                count++
                //Log.i(Constant.TAG, "$count")
                deleteFileLastLine(str.split(",")[0])
                str = reader.readLine()
            }
        }.start()
    }

    class ShareInfo : Parcelable {
        override fun writeToParcel(dest: Parcel?, flags: Int) {
            //i(TAG, "writeToParcel")
            dest?.writeString(name)
            dest?.writeString(code)
            dest?.writeDouble(beginPrice)
            dest?.writeDouble(yesterdayPrice)
            dest?.writeDouble(nowPrice)
            dest?.writeDouble(maxPrice)
            dest?.writeDouble(minPrice)
            dest?.writeInt(totalCount)
            dest?.writeDouble(totalPrice)
            dest?.writeString(time)
            dest?.writeDouble(range)
            dest?.writeDouble(huanShouLv)
            dest?.writeDouble(liuTongShiZhi)
            dest?.writeDouble(zongShiZhi)
            dest?.writeDouble(line_5)
            dest?.writeDouble(line_10)
            dest?.writeDouble(line_20)
            dest?.writeDouble(rangeBegin)
            dest?.writeDouble(rangeMax)
            dest?.writeDouble(rangeMin)
            dest?.writeDouble(postRange)
            dest?.writeString(post1)
            dest?.writeString(post2)
        }

        override fun describeContents(): Int {
            //i(TAG, "describeContents")
            return 0
        }

        var name: String? = null //股票名称
        var code: String? = null //股票代码
        var beginPrice: Double = 0.00 //开盘价
        var yesterdayPrice: Double = 0.00 //昨天收盘价
        var nowPrice: Double = 0.00  //当前价格
        var maxPrice: Double = 0.00  //最高价格
        var minPrice: Double = 0.00  //最低价格
        var totalCount: Int = 0 //总成交手数，单位手
        var totalPrice: Double = 0.00 //总成交金额，单位元
        var time: String? = null //日期

        var range: Double = 0.00 //涨跌幅，单位%

        var huanShouLv: Double = 0.00 //换手率，单位%
        var liuTongShiZhi: Double = 0.00 //流通市值，单位亿
        var zongShiZhi: Double = 0.00 //总市值，单位亿

        var line_5: Double = 0.00 //10日线
        var line_10: Double = 0.00 //10日线
        var line_20: Double = 0.00 //10日线
        var rangeBegin: Double = 0.00 //开盘涨跌幅，单位%
        var rangeMax: Double = 0.00 //最大涨幅，单位%
        var rangeMin: Double = 0.00 //最大跌幅，单位%

        var postRange = 0.00
        var post1 = ""
        var post2 = ""

        constructor(parcel: Parcel) : this() {
            name = parcel.readString()
            code = parcel.readString()
            beginPrice = parcel.readDouble()
            yesterdayPrice = parcel.readDouble()
            nowPrice = parcel.readDouble()
            maxPrice = parcel.readDouble()
            minPrice = parcel.readDouble()
            totalCount = parcel.readInt()
            totalPrice = parcel.readDouble()
            time = parcel.readString()
            range = parcel.readDouble()
            huanShouLv = parcel.readDouble()
            liuTongShiZhi = parcel.readDouble()
            zongShiZhi = parcel.readDouble()
            line_5 = parcel.readDouble()
            line_10 = parcel.readDouble()
            line_20 = parcel.readDouble()
            rangeBegin = parcel.readDouble()
            rangeMax = parcel.readDouble()
            rangeMin = parcel.readDouble()
            postRange = parcel.readDouble()
            post1 = parcel.readString() ?: ""
            post2 = parcel.readString() ?: ""
        }

        constructor(){}
        constructor(info: String){
            val values = info.split(",")
            if(values.size >= 20) {
                time = values[0].trim()
                name = values[1].trim()
                code = values[2].trim()
                yesterdayPrice = values[3].trim().toDouble()
                beginPrice = values[4].trim().toDouble()
                nowPrice = values[5].trim().toDouble()
                maxPrice = values[6].trim().toDouble()
                minPrice = values[7].trim().toDouble()
                range = values[8].trim().toDouble()
                rangeBegin = values[9].trim().toDouble()
                rangeMax = values[10].trim().toDouble()
                rangeMin = values[11].trim().toDouble()
                line_5 = values[12].trim().toDouble()
                line_10 = values[13].trim().toDouble()
                line_20 = values[14].trim().toDouble()
                totalCount = values[15].trim().toInt()
                totalPrice = values[16].trim().toDouble()
                huanShouLv = values[17].trim().toDouble()
                liuTongShiZhi = values[18].trim().toDouble()
                zongShiZhi = values[19].trim().toDouble()
            }
        }

        override fun toString(): String {
            return "$time, $name, $code, beginPrice:$beginPrice, yesterdayPrice:$yesterdayPrice, nowPrice:$nowPrice," +
                    " maxPrice:$maxPrice, minPrice:$minPrice," +
                    " totalCount:$totalCount, totalPrice:$totalPrice, range: $range, huanShouLv: $huanShouLv, liuTongShiZhi: $liuTongShiZhi, zongShiZhi: $zongShiZhi"
        }

        fun toFile(): String {
            return "$time, $name, $code, $yesterdayPrice, $beginPrice, $nowPrice, $maxPrice, $minPrice, $range, $rangeBegin, $rangeMax, $rangeMin, $line_5, $line_10, $line_20, $totalCount, $totalPrice, $huanShouLv, $liuTongShiZhi, $zongShiZhi"
        }

        fun brieflyInfo(): String {
            return "$time, $name, $code, $zongShiZhi"
        }

        fun followUp(): String {
            return "($rangeBegin, $range, $rangeMin, $rangeMax)"
        }

        fun simpleInfo(): String {
            return "$code , $name , ${String.format("%.1f",totalPrice / 100000000)}亿 , ${zongShiZhi}亿 , $post1 , $post2"
        }

        companion object CREATOR : Parcelable.Creator<ShareInfo> {
            override fun createFromParcel(parcel: Parcel): ShareInfo {
                return ShareInfo(parcel)
            }

            override fun newArray(size: Int): Array<ShareInfo?> {
                return arrayOfNulls(size)
            }
        }
    }
}

//新浪的股票数据接口提供个股的最新行情。
//例如：要获取平安银行（000001）的最新行情，只需访问http://hq.sinajs.cn/list=sz000001，这个url会返回一串文本：
//var hq_str_sz000001="平安银行,9.170,9.190,9.060,9.180,9.050,9.060,9.070,42148125,384081266.460,624253,9.060,638540,9.050,210600,9.040,341700,9.030,2298300,9.020,227184,9.070,178200,9.080,188240,9.090,293536,9.100,295300,9.110,2016-09-14,15:11:03,00";
//这个字符串由许多数据拼接在一起，不同含义的数据用逗号隔开了，按照程序员的思路，顺序号从0开始。
//0：“平安银行”，股票名字；
//1：“9.170”，今日开盘价；
//2：“9.190”，昨日收盘价；
//3：“9.060”，当前价格；
//4：“9.180”，今日最高价；
//5：“9.050”，今日最低价；
//6：“9.060”，竞买价，即“买一“报价；
//7：“9.070”，竞卖价，即“卖一“报价；
//8：“42148125”，成交的股票数，由于股票交易以一百股为基本单位，所以在使用时，通常把该值除以一百；
//9：“384081266.460”，成交金额，单位为“元“，为了一目了然，通常以“万元“为成交金额的单位，所以通常把该值除以一万；
//10：“624253”，“买一”申请624253股，即6243手；
//11：“9.060”，“买一”报价；
//12：“638540”，“买二”申报股数；
//13：“9.050”，“买二”报价；
//14：“210600”，“买三”申报股数；
//15：“9.040”，“买三”报价；
//16：“341700”，“买四”申报股数；
//17：“9.030”，“买四”报价；
//18：“2298300”，“买五”申报股数；
//19：“9.020”，“买五”报价；
//20：“227184”，“卖一”申报227184股，即2272手；
//21：“9.070”，“卖一”报价；
//(22, 23), (24, 25), (26,27), (28, 29)分别为“卖二”至“卖五”的申报股数及其价格；
//30：“2016-09-14”，日期；
//31：“15:11:03”，时间；