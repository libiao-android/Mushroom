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

            i(TAG, "????????????: ${codeList.size}")
            //????????????sz000???sz001???sz002???sz003
            for(i in 1000 .. 3099) {
                val code = CodeUtil.getSzCode(i)
                if(codeList.contains(code)) continue
                if(i % 100 == 0) {
                    i(TAG, "sz00, $i, $code")
                }
                queryInfo(code)
            }
            i(TAG, "?????????40%")
            mHandler.post { progressTv?.text = "40%, $count" }
            //???????????? sh600???sh601???sh603???sh605
            for(i in 1000 .. 1999) {
                val code = CodeUtil.getShCode(i)
                if(codeList.contains(code)) continue
                if(i % 100 == 0) {
                    i(TAG, "sh60, $i, $code")
                }
                queryInfo(code)
            }
            i(TAG, "?????????60%")
            mHandler.post { progressTv?.text = "60%, $count" }
            for(i in 3000 .. 3999) {
                val code = CodeUtil.getShCode(i)
                if(codeList.contains(code)) continue
                if(i % 100 == 0) {
                    i(TAG, "sh603, $i, $code")
                }
                queryInfo(code)
            }
            i(TAG, "?????????80%")
            mHandler.post { progressTv?.text = "80%, $count" }
            for(i in 5001 .. 5999) {
                val code = CodeUtil.getShCode(i)
                if(codeList.contains(code)) continue
                if(i % 100 == 0) {
                    i(TAG, "sh605, $i, $code")
                }
                queryInfo(code)
            }
            i(TAG, "?????????90%")
            mHandler.post { progressTv?.text = "90%, $count" }
            //??????????????? sz300
            for(i in 1000 .. 1500) {
                val code = CodeUtil.getCyCode(i)
                if(codeList.contains(code)) continue
                if(i % 100 == 0) {
                    i(TAG, "sz300, $i, $code")
                }
                queryInfo(code)
            }
            mHandler.post { progressTv?.text = "100%, $count" }

            //??????????????? sh688
//            for(i in 1 .. 999) {
//                val code = getKcCode(i)
//                //Log.i("libiao", code)
//                queryInfo(code)
//            }
            val totalTime = System.currentTimeMillis() - startTime
            i(TAG, "????????????: $count, totalTime: ${totalTime/(1000*60)}")
            i(TAG, "???????????????${newCodeList.size}")

            newCodeList.forEach {
                deleteTuiShiData(it.key, it.value)
                Thread.sleep(100)
            }
            Thread.sleep(3000)
            i(TAG, "????????????????????????????????????${codes.size}")

            val sb = StringBuilder()
            codes.forEach {
                sb.append("$it\n")
            }
            writeFileAppend(sb.toString())
            i(TAG, "??????????????????")

            codes.forEach {
                val a = it.split(",")
                getHistoryData(a[0].trim(), a[1].trim())
                Thread.sleep(500)
            }
            i(TAG, "????????????????????????")

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
                if(count % 100 == 0) {
                    //i(TAG, "str: $str, $count")
                }
                val a = str.split(",")
                val code  = a[0].trim()
                val f = File(fileNew_2021, code)
                if(f.exists()) {

                } else {
                    i(TAG, "?????????: $code")
                    f.createNewFile()
                    getHistoryData(a[0].trim(), a[1].trim())
                    Thread.sleep(500)
                }
                str = reader.readLine()
            }
            isHistoryDoing = false
            i(TAG, "history end")
        }.start()

        //getHistoryData("sz300999", "????????????")
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
            i(TAG, "3s????????????????????????${codes.size}")
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
                i(TAG, "??????????????????")
            }, 3000)
        }.start()

    }
    private fun deleteTuiShiData(code: String, name: String) {
        i(TAG, "deleteTuiShiData: $code, $name")
        //https://q.stock.sohu.com/hisHq?code=cn_601012&start=20210601&end=20210625
        val request = Request.Builder()
            .url("https://q.stock.sohu.com/hisHq?code=cn_${code.substring(2)}&start=20220508&end=20220715")
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
                        val f = File(fileNew_2021, code)
                        if(f.exists()) {
                            f.delete()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    i(TAG, "getHistoryData exception: ${e.message}, $code, $name")
                    val f = File(fileNew_2021, code)
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
            .url("https://q.stock.sohu.com/hisHq?code=cn_${code.substring(2)}&start=20220101&end=20220715")
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
                        val f = File(fileNew_2021, code)
                        if(f.exists()) {
                            f.delete()
                        }

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    i(TAG, "getHistoryData exception: ${e.message}, $code, $name")
                    val f = File(fileNew_2021, code)
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
        val file = File(fileNew_2021, code)
        if(!file.exists()) {
            file.createNewFile()
        }
        FileUtil.writeFileAppend(file, info, false)
    }
}

//?????????????????????????????????????????????????????????
//?????????????????????????????????000001?????????????????????????????????http://hq.sinajs.cn/list=sz000001?????????url????????????????????????
//var hq_str_sz000001="????????????,9.170,9.190,9.060,9.180,9.050,9.060,9.070,42148125,384081266.460,624253,9.060,638540,9.050,210600,9.040,341700,9.030,2298300,9.020,227184,9.070,178200,9.080,188240,9.090,293536,9.100,295300,9.110,2016-09-14,15:11:03,00";
//?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????0?????????
//0???????????????????????????????????????
//1??????9.170????????????????????????
//2??????9.190????????????????????????
//3??????9.060?????????????????????
//4??????9.180????????????????????????
//5??????9.050????????????????????????
//6??????9.060??????????????????????????????????????????
//7??????9.070??????????????????????????????????????????
//8??????42148125??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
//9??????384081266.460???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
//10??????624253????????????????????????624253?????????6243??????
//11??????9.060???????????????????????????
//12??????638540?????????????????????????????????
//13??????9.050???????????????????????????
//14??????210600?????????????????????????????????
//15??????9.040???????????????????????????
//16??????341700?????????????????????????????????
//17??????9.030???????????????????????????
//18??????2298300?????????????????????????????????
//19??????9.020???????????????????????????
//20??????227184????????????????????????227184?????????2272??????
//21??????9.070???????????????????????????
//(22, 23), (24, 25), (26,27), (28, 29)??????????????????????????????????????????????????????????????????
//30??????2016-09-14???????????????
//31??????15:11:03???????????????