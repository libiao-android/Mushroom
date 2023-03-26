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
import com.libiao.mushroom.utils.CodeUtil.Companion.getKcCode
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.FileUtil
import com.libiao.mushroom.utils.LogUtil
import com.libiao.mushroom.utils.LogUtil.i
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.lang.Exception
import java.nio.charset.Charset
import kotlin.text.StringBuilder

class UpdateStockPoolActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "UpdateStockPoolActivity"
    }

    private val mHandler = Handler()
    private val client = OkHttpClient()

    //private val file = File(Environment.getExternalStorageDirectory(), "SharesInfo")
    private val fileNew = File(Environment.getExternalStorageDirectory(), "A_SharesInfo")

    private val fileNew_2023 = File(fileNew, "2023")
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

        if (!fileNew_2023.exists()) {
            fileNew_2023.mkdir()
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

    fun test(v: View) {
        Thread {
            fileNew_2023.listFiles().forEach {
                if(it.name.startsWith("sh688")) {
                    i(TAG, "delete file: ${it.name}")
                    it.delete()
                }
            }
        }.start()
    }


    val newCodeList = HashMap<String, String>()
    fun updateStockPool(v: View) {
        btn?.isEnabled = false
        loadingPb?.visibility = View.VISIBLE
        sb.clear()
        count = 0

        startTime = System.currentTimeMillis()

        stockPoolFile = File(fileNew, "stock_pool")
//        stockPoolFile?.also {
//            it.delete()
//            it.createNewFile()
//        }

        Thread {

            val stream = FileInputStream(stockPoolFile)
            val reader = BufferedReader(InputStreamReader(stream, Charset.defaultCharset()))
            var str: String?
            str = reader.readLine()
            val codeList = HashSet<String>()
            while (str != null) {
                val a = str.split(",")
                val code  = a[0].trim()
                codeList.add(code)
                str = reader.readLine()
            }

            i(TAG, "开始查询: ${codeList.size}")
            //深圳股票sz000，sz001，sz002，sz003
            for(i in 1000 .. 3099) {
                val code = CodeUtil.getSzCode(i)
                if(codeList.contains(code)) continue
                if(i % 100 == 0) {
                    i(TAG, "sz00, $i, $code")
                }
                queryInfo(code)
            }
            i(TAG, "进度：40%")
            mHandler.post { progressTv?.text = "40%, $count" }
            //上海股票 sh600，sh601，sh603，sh605
            for(i in 1000 .. 1999) {
                val code = CodeUtil.getShCode(i)
                if(codeList.contains(code)) continue
                if(i % 100 == 0) {
                    i(TAG, "sh60, $i, $code")
                }
                queryInfo(code)
            }
            i(TAG, "进度：60%")
            mHandler.post { progressTv?.text = "60%, $count" }
            for(i in 3000 .. 3999) {
                val code = CodeUtil.getShCode(i)
                if(codeList.contains(code)) continue
                if(i % 100 == 0) {
                    i(TAG, "sh603, $i, $code")
                }
                queryInfo(code)
            }
            i(TAG, "进度：80%")
            mHandler.post { progressTv?.text = "80%, $count" }
            for(i in 5001 .. 5999) {
                val code = CodeUtil.getShCode(i)
                if(codeList.contains(code)) continue
                if(i % 100 == 0) {
                    i(TAG, "sh605, $i, $code")
                }
                queryInfo(code)
            }
            i(TAG, "进度：90%")
            mHandler.post { progressTv?.text = "90%, $count" }
            //创业板股票 sz300
            for(i in 1000 .. 1500) {
                val code = CodeUtil.getCyCode(i)
                if(codeList.contains(code)) continue
                if(i % 100 == 0) {
                    i(TAG, "sz300, $i, $code")
                }
                queryInfo(code)
            }
            mHandler.post { progressTv?.text = "95%, $count" }

            i(TAG, "进度：95%")
            //科创板股票 sh688
            for(i in 1 .. 999) {
                val code = getKcCode(i)
                if(codeList.contains(code)) continue
                //Log.i("libiao", code)
                queryInfo(code)
            }
            mHandler.post { progressTv?.text = "95%, $count" }
            val totalTime = System.currentTimeMillis() - startTime
            i(TAG, "结束查询: $count, totalTime: ${totalTime/(1000*60)}")
            i(TAG, "新增个数：${newCodeList.size}")

            newCodeList.forEach {
                deleteTuiShiData(it.key, it.value)
                Thread.sleep(100)
            }
            Thread.sleep(3000)
            i(TAG, "清理退市数据后实际新增：${codes.size}")

            val sb = StringBuilder()
            codes.forEach {
                sb.append("$it\n")
            }
            writeFileAppend(sb.toString())
            i(TAG, "新增写入完成")

//            codes.forEach {
//                val a = it.split(",")
//                getHistoryData(a[0].trim(), a[1].trim())
//                Thread.sleep(500)
//            }
//            i(TAG, "补充历史数据完成")

            mHandler.post {
                loadingPb?.visibility = View.GONE
                btn?.isEnabled = true
            }
        }.start()

    }

    private fun queryInfo(code: String) {
        //i(TAG, "queryInfo: $code")
        initData(code)
        Thread.sleep(100)
    }

    private fun initData(code: String) {

        val request = Request.Builder()
            .url("https://qt.gtimg.cn/q=$code")
            .build()

        val call = client.newCall(request)

        call.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                i(TAG, "onFailure: ${e}")
            }

            override fun onResponse(call: Call?, response: Response?) {
                val value = response?.body()?.string()
                //i(TAG, "response: ${value}")
                mHandler.post {
                    try {
                        if (value != null && value.length > 50) {
                            parseSharesInfo(value)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        i(TAG, "parseSharesInfo exception: ${e.message}")
                    }
                }
            }
        })
    }

    private fun parseSharesInfo(response: String?) {
        response?.also {
            val leftRight = it.split("~")
            if (leftRight.size > 2) {
                val name = leftRight[1]
                val code = leftRight[0].substring(2, 10)
                //i(TAG, "$name, $code")
                newCodeList[code] = name
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
                if(count == 2) {
                    //i(TAG, "str: $str, $count")
                    //break
                }
                val a = str.split(",")
                val code  = a[0].trim()
                val f = File(fileNew_2023, code)
                if(f.exists()) {

                } else {
                    i(TAG, "不存在: $code")
                    f.createNewFile()
                    getHistoryData(a[0].trim(), a[1].trim())
                    Thread.sleep(500)
                }
                str = reader.readLine()
            }
            isHistoryDoing = false
            i(TAG, "history end")
        }.start()

        //getHistoryData("sz300999", "瑞玛工业")
    }

    private var isDeleteTuiShiDoing = false
    private val codes = arrayListOf<String>()
    fun deleteTuiShi(v: View) {
        i(TAG, "isDeleteTuiShiDoing: $isDeleteTuiShiDoing")
        if(isDeleteTuiShiDoing) return
        isDeleteTuiShiDoing = true

        Thread{

            val stream = FileInputStream(File(fileNew, "stock_pool"))
            val reader = BufferedReader(InputStreamReader(stream, Charset.defaultCharset()))
            var str: String?
            str = reader.readLine()
            var count = 0
            while (str != null) {
                count++
                if(count % 100 == 0) {
                    //i(TAG, "str: $str, $count")
                }
                val a = str.split(",")

                deleteTuiShiData(a[0].trim(), a[1].trim())
                Thread.sleep(100)

                str = reader.readLine()
            }
            isDeleteTuiShiDoing = false
            i(TAG, "deleteTuiShi end")
            i(TAG, "3s后开始重新写入：${codes.size}")
            mHandler.postDelayed({
                stockPoolFile = File(fileNew, "stock_pool")
                stockPoolFile?.also {
                    it.delete()
                    it.createNewFile()
                }
                val sb = StringBuilder()
                codes.forEach {
                    sb.append("$it\n")
                }
                writeFileAppend(sb.toString())
                i(TAG, "重新写入完成")
            }, 3000)
        }.start()

    }
    private fun deleteTuiShiData(code: String, name: String) {
        i(TAG, "deleteTuiShiData: $code, $name")
        //https://q.stock.sohu.com/hisHq?code=cn_601012&start=20210601&end=20210625
        val request = Request.Builder()
            .url("https://q.stock.sohu.com/hisHq?code=cn_${code.substring(2)}&start=20230101&end=20230325")
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
                    if (value != null && value.length > 100) {
                        mHandler.post {
                            codes.add("$code, $name")
                        }
                    } else {
                        i(TAG, "getHistoryData exception value: ${value}, $code, $name")
                        val f = File(fileNew_2023, code)
                        if(f.exists()) {
                            f.delete()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    i(TAG, "getHistoryData exception: ${e.message}, $code, $name")
                    val f = File(fileNew_2023, code)
                    if(f.exists()) {
                        f.delete()
                    }
                }
            }
        })
    }

    private fun getHistoryData(code: String, name: String) {
        //i(TAG, "getHistoryData: $code, $name")
        //https://q.stock.sohu.com/hisHq?code=cn_601012&start=20210601&end=20210625
        val request = Request.Builder()
            .url("https://q.stock.sohu.com/hisHq?code=cn_${code.substring(2)}&start=20210601&end=20230325")
            .build()

        val call = client.newCall(request)

        call.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                i(TAG, "onFailure: ${e}")
            }

            override fun onResponse(call: Call?, response: Response?) {
                val value = response?.body()?.string()
                //i(TAG, "response: ${value}")
                try {
                    if (value != null && value.length > 50) {
                        parseHistoryInfo(value, code, name)
                    } else {
                        i(TAG, "getHistoryData exception value: ${value}, $code, $name")
                        val f = File(fileNew_2023, code)
                        if(f.exists()) {
                            f.delete()
                        }

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    i(TAG, "getHistoryData exception: ${e.message}, $code, $name")
                    val f = File(fileNew_2023, code)
                    if(f.exists()) {
                        f.delete()
                    }
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
        val file = File(fileNew_2023, code)
        if(!file.exists()) {
            file.createNewFile()
        }
        FileUtil.writeFileAppend(file, info, false)
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