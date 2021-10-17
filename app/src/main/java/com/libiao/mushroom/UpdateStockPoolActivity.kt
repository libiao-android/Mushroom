package com.libiao.mushroom

import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.libiao.mushroom.utils.CodeUtil
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.FileUtil
import com.libiao.mushroom.utils.LogUtil.i
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.lang.Exception
import java.lang.StringBuilder
import java.nio.charset.Charset

class UpdateStockPoolActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "UpdateStockPoolActivity"
    }

    private val mHandler = Handler()
    private val client = OkHttpClient()

    //private val file = File(Environment.getExternalStorageDirectory(), "SharesInfo")
    private val fileNew = File(Environment.getExternalStorageDirectory(), "A_SharesInfo")
    private val fileNew_2019 = File(fileNew, "2019")
    private val fileNew_2020 = File(fileNew, "2020")
    private val fileNew_2021 = File(fileNew, "2021")
    private var stockPoolFile: File? = null

    private var latelyTime: String? = null

    private var count = 0

    private val sb = StringBuilder()

    private var progressTv: TextView? = null

    private var loadingPb: ProgressBar? = null
    private var btn: Button? = null

    private var startTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.update_stockpool_activity)
        supportActionBar?.hide()


        /*if (!file.exists()) {
            file.mkdir()
        }*/
        if (!fileNew.exists()) {
            fileNew.mkdir()
        }
        if (!fileNew_2019.exists()) {
            fileNew_2019.mkdir()
        }
        if (!fileNew_2020.exists()) {
            fileNew_2020.mkdir()
        }

        if (!fileNew_2021.exists()) {
            fileNew_2021.mkdir()
        }

//        val file1 = File(fileNew, "sh601528")
//        if(!file1.exists()) {
//            file1.createNewFile()
//        }
//
//        val file2 = File(fileNew, "sz301004")
//        if(!file2.exists()) {
//            file2.createNewFile()
//        }


        progressTv = findViewById(R.id.tv_progress)

        loadingPb = findViewById(R.id.loading)
        btn = findViewById(R.id.btn)
    }


    fun updateStockPool(v: View) {
        btn?.isEnabled = false
        loadingPb?.visibility = View.VISIBLE
        sb.clear()
        count = 0

        startTime = System.currentTimeMillis()

        stockPoolFile = File(fileNew, "stock_pool")
        stockPoolFile?.also {
            it.delete()
            it.createNewFile()
        }

        Thread {
            Log.i("libiao", "开始查询")
            //深圳股票sz000，sz001，sz002，sz003
            for(i in 1 .. 3999) {
                val code = CodeUtil.getSzCode(i)
                // Log.i("libiao", "$code")
                queryInfo(code)
            }
            Log.i("libiao", "进度：40%")
            mHandler.post { progressTv?.text = "40%, $count" }
            //上海股票 sh600，sh601，sh603，sh605
            for(i in 0 .. 1999) {
                val code = CodeUtil.getShCode(i)
                //Log.i("libiao", "$code")
                queryInfo(code)
            }
            Log.i("libiao", "进度：60%")
            mHandler.post { progressTv?.text = "60%, $count" }
            for(i in 3000 .. 3999) {
                val code = CodeUtil.getShCode(i)
                // Log.i("libiao", "$code")
                queryInfo(code)
            }
            Log.i("libiao", "进度：80%")
            mHandler.post { progressTv?.text = "80%, $count" }
            for(i in 5001 .. 5999) {
                val code = CodeUtil.getShCode(i)
                // Log.i("libiao", "$code")
                queryInfo(code)
            }
            Log.i("libiao", "进度：90%")
            mHandler.post { progressTv?.text = "90%, $count" }
            //创业板股票 sz300
            for(i in 1 .. 1500) {
                val code = CodeUtil.getCyCode(i)
                //Log.i("libiao", code)
                queryInfo(code)
            }
            mHandler.post { progressTv?.text = "100%, $count" }

            //科创板股票 sh688
//            for(i in 1 .. 999) {
//                val code = getKcCode(i)
//                //Log.i("libiao", code)
//                queryInfo(code)
//            }
            val totalTime = System.currentTimeMillis() - startTime
            Log.i("libiao", "结束查询: $count, totalTime: $totalTime")

            val infos = sb.toString()
            sb.clear()
            writeFileAppend(infos)

            mHandler.post{
                loadingPb?.visibility = View.GONE
                btn?.isEnabled = true
            }

            i(TAG, "${set.size}, ${set.toString()}")

        }.start()

    }

    private fun queryInfo(code: String) {
        initData(code)
        Thread.sleep(100)
    }

    private fun initData(code: String) {

        val request = Request.Builder()
            .url("https://hq.sinajs.cn/list=$code")
            .build()

        val call = client.newCall(request)

        call.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.i("libiao", "onFailure: ${e}")
            }

            override fun onResponse(call: Call?, response: Response?) {
                val value = response?.body()?.string()
                //Log.i("libiao", "response: ${value}")
                mHandler.post {
                    try {
                        if (value != null && value.length > 50) {
                            parseSharesInfo(value)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.i("libiao", "parseSharesInfo exception: ${e.message}")
                    }
                }
            }
        })
    }

    val set = HashSet<String>()
    private fun parseSharesInfo(response: String?) {
        response?.also {

            val leftRight = it.split("=")
            if (leftRight.size < 2) return
            val info = leftRight[0].split("_")
            val code = info[info.size - 1]
            val params = leftRight[1].split(",")
            val name = params[0].substring(1)
            val time = params[30]
            val statusP = params[32]
            val status = statusP.substring(0, 2)
            set.add(status)
            //i(TAG, "status: $status")
//            if(status == "-3" || status == "-2" || status == "03") {
//                i(TAG, "$it")
//            }
//            if(status.trim().isEmpty()) {
//                i(TAG, "$it")
//            }
            //Log.i("libiao", "code: $code, name: $name, price: $price")
            if(code == "sz000001") {
                latelyTime = time
            }
            if(time == latelyTime && status != "-3" && status != "-2" && status != "03") {
                sb.append("$code, $name\n")
                count++
                if(count % 400 == 0) {
                    val infos = sb.toString()
                    sb.clear()
                    writeFileAppend(infos)
                }
            } else {
                i(TAG, " 抛弃 code: $code, time: $time, status: $status")
            }
        }
    }

    private fun writeFileAppend(info: String) {
        var fileWriter: FileWriter? = null
        try {
            fileWriter = FileWriter(stockPoolFile, true)
            fileWriter.append(info)
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



    private var isHistoryDoing = false
    fun history(v: View) {
        i(TAG, "isHistoryDoing: $isHistoryDoing")
        if(isHistoryDoing) return
        isHistoryDoing = true
        Thread{

            val stream = FileInputStream(File(fileNew, "stock_pool"))
            val reader = BufferedReader(InputStreamReader(stream, Charset.defaultCharset()))
            var str: String?
            str = reader.readLine()
            var count = 0
            while (str != null) {
                count++
                if(count % 100 == 0) {
                    i(TAG, "str: $str, $count")
                }
                val a = str.split(",")
                getHistoryData(a[0].trim(), a[1].trim())
                str = reader.readLine()
                Thread.sleep(200)
            }
            isHistoryDoing = false
            i(TAG, "history end")
        }.start()

        //getHistoryData("sz300999", "瑞玛工业")
    }

    private fun getHistoryData(code: String, name: String) {
        //i(TAG, "getHistoryData: $code, $name")
        //https://q.stock.sohu.com/hisHq?code=cn_601012&start=20210601&end=20210625
        val request = Request.Builder()
            .url("https://q.stock.sohu.com/hisHq?code=cn_${code.substring(2)}&start=20210927&end=20210930")
            .build()

        val call = client.newCall(request)

        call.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                i(TAG, "onFailure: ${e}")
            }

            override fun onResponse(call: Call?, response: Response?) {
                val value = response?.body()?.string()
                //Log.i("libiao", "response: ${value}")
                try {
                    if (value != null && value.length > 50) {
                        parseHistoryInfo(value, code, name)
                    } else {
                        i(TAG, "getHistoryData exception value: ${value}, $code, $name")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    i(TAG, "getHistoryData exception: ${e.message}, $code, $name")
                }
            }
        })
    }

    private fun parseHistoryInfo(value: String, code: String, name: String) {
        val array_5 = arrayOfNulls<Double>(5)
        val array_10 = arrayOfNulls<Double>(10)
        val array_20 = arrayOfNulls<Double>(20)
        val json = JSONArray(value)[0] as JSONObject
        val array = json.getJSONArray("hq")
        //i(TAG, "array: ${array.length()}")
        var index = 0
        var yesToday = 0.00
        val sharesInfoSb = StringBuilder()
        for( i in array.length()-1 downTo 0) {
            //i(TAG, "${array[i]}")
            val info = array[i] as JSONArray
            //val strs = info[0].toString().split(",")
            val time = info[0].toString()
            val beginPrice = info[1].toString().toDouble()
            val nowPrice = info[2].toString().toDouble()
            val range = info[4].toString()
            val rangeD = range.substring(0, range.length - 1).toDouble()
            val minPrice = info[5].toString().toDouble()
            val maxPrice = info[6].toString().toDouble()
            val totalCount = info[7].toString().toInt()
            val totalPrice = info[8].toString().toDouble() * 10000
            val huanShouLv = info[9].toString()
            //i(TAG, "huanShouLv: $huanShouLv")
            val huanShouLvD = safeToDouble(huanShouLv.substring(0, huanShouLv.length - 1))
//            i(TAG, "time: $time")
//            i(TAG, "beginPrice: $beginPrice")
//            i(TAG, "nowPrice: $nowPrice")
//            i(TAG, "rangeD: $rangeD")
//            i(TAG, "minPrice: $minPrice")
//            i(TAG, "maxPrice: $maxPrice")
//            i(TAG, "totalCount: $totalCount")
//            i(TAG, "totalPrice: $totalPrice")
//            i(TAG, "huanShouLvD: $huanShouLvD")

            //String.format("%.2f",(nowPrice - yesterdayPrice)  / yesterdayPrice * 100)

            val shareInfo = SharesRecordActivity.ShareInfo()
            shareInfo.time = time
            shareInfo.code = code
            shareInfo.name = name
            shareInfo.yesterdayPrice = yesToday
            shareInfo.beginPrice = beginPrice
            shareInfo.nowPrice = nowPrice
            shareInfo.range = rangeD
            shareInfo.minPrice = minPrice
            shareInfo.maxPrice = maxPrice
            shareInfo.totalCount = totalCount
            shareInfo.totalPrice = totalPrice
            shareInfo.huanShouLv = huanShouLvD
            if(yesToday != 0.00) {
                val rangeBegin = String.format("%.2f", (beginPrice - yesToday) / yesToday * 100)
                val rangeMin = String.format("%.2f", (minPrice - yesToday) / yesToday * 100)
                val rangeMax = String.format("%.2f", (maxPrice - yesToday) / yesToday * 100)
                shareInfo.rangeBegin = rangeBegin.toDouble()
                shareInfo.rangeMin = rangeMin.toDouble()
                shareInfo.rangeMax = rangeMax.toDouble()
            }
            array_5[index % 5] = nowPrice
            array_10[index % 10] = nowPrice
            array_20[index % 20] = nowPrice
            if(index >= 4) {
                val str_5 = String.format("%.2f", array_5.sumByDouble { it ?: 0.00 } / 5)
                shareInfo.line_5 = str_5.toDouble()
            }
            if(index >= 9) {
                val str_10 = String.format("%.2f", array_10.sumByDouble { it ?: 0.00 } / 10)
                shareInfo.line_10 = str_10.toDouble()
            }
            if(index >= 19) {
                val str_20 = String.format("%.2f", array_20.sumByDouble { it ?: 0.00 } / 20)
                shareInfo.line_20 = str_20.toDouble()
            }
            //i(TAG, shareInfo.toFile())
            sharesInfoSb.append("${shareInfo.toFile()}\n")
            yesToday = nowPrice
            index++

            if(index % 20 == 0) {
                writeToFile(code, sharesInfoSb.toString())
                sharesInfoSb.clear()
            }
        }
        writeToFile(code, sharesInfoSb.toString())
    }

    private fun safeToDouble(substring: String): Double {
        try {
            return substring.toDouble()
        }catch (e: Exception) {

        }
        return 0.00
    }

    private fun writeToFile(code: String, info: String) {
        //i(TAG, "$info")
        val file = File(fileNew_2021, code)
        if(!file.exists()) {
            file.createNewFile()
        }
        FileUtil.writeFileAppend(file, info)
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