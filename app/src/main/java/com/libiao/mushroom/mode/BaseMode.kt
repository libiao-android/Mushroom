package com.libiao.mushroom.mode

import android.content.Context
import android.os.Environment
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.LogUtil.i
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.lang.StringBuilder
import kotlin.math.abs
import kotlin.math.min

abstract class BaseMode {

    val TAG = javaClass.simpleName

    var mDeviationValue = 0
    val mFitModeList = ArrayList<Pair<Double, SharesRecordActivity.ShareInfo?>>()

    var showTime = false


    val fileNew = File(Environment.getExternalStorageDirectory(), "A_SharesInfo")
    val poolList = java.util.ArrayList<String>()
    var poolFile: File? = null


    constructor() {
    }

    constructor(showTime: Boolean) {
        this.showTime = showTime
    }


    abstract fun analysis(shares : ArrayList<SharesRecordActivity.ShareInfo>)
    abstract fun des(): String


    open fun analysis(day: Int, shares : ArrayList<SharesRecordActivity.ShareInfo>) {

    }

    open fun content(): String {
        val size = mFitModeList.size
        i(TAG, "mFitModeList: $size")
        if(showTime) {
            mFitModeList.sortBy { it.second?.time }
        } else {
            mFitModeList.sortByDescending { it.first }
        }


        val sb = StringBuilder("")

        for(i in 0 until size) {
            if(showTime) {
                sb.append("${mFitModeList[i].second?.time}" +
                        ",  ${mFitModeList[i].second?.code}" +
                        ",  ${mFitModeList[i].second?.name}" +
                        ",  \n${mFitModeList[i].second?.post1}" +
                        ",  ${mFitModeList[i].second?.post2}" +
                        " \n\n")
            } else {
                sb.append("${mFitModeList[i].second?.code}" +
                        ",  ${mFitModeList[i].second?.name}" +
                        ",  ${String.format("%.4f",mFitModeList[i].first)}" +
                        ",  ${mFitModeList[i].second?.postRange}" +
                        ",  ${mFitModeList[i].second?.zongShiZhi}" +
                        " \n")
            }

        }

        return sb.toString()
    }

    fun clear() {
        mFitModeList.clear()
    }

    open fun more(firstDay: SharesRecordActivity.ShareInfo, secondDay: SharesRecordActivity.ShareInfo): Boolean {

        if(secondDay.range > 5) { //当天涨幅大于5%
            var multiple = 0.00
            if(firstDay.totalPrice > 0) {
                multiple = secondDay.totalPrice / firstDay.totalPrice
            }
            var m = 3.5
            if(firstDay.totalPrice > 500000000) {
                m = 2.0
            } else if(firstDay.totalPrice > 250000000) {
                m = 2.5
            } else if(firstDay.totalPrice > 100000000) {
                m = 3.0
            }
            if(multiple > m) {
                //Log.i("libiao", "放量：${today.toFile()}, multiple: $multiple")
                return true
            }
        }
        return false
    }

    fun isShiZiXing(begin: Double, end: Double): Boolean {
        //return false
        if(abs(begin - end) <= 0.01) return true
        return abs(begin - end) / min(begin, end) < 0.0025
    }

    fun getRange(begin: Double, end: Double): Double {
        val s = String.format("%.4f",(end - begin)  / begin * 100)
        return s.toDouble()
    }

    fun isChuang(code: String?): Boolean {
        return code?.startsWith("sz3") ?: false
    }

    fun zhangTing(info: SharesRecordActivity.ShareInfo): Boolean {
        var maxRange = 1.1
        if(info.code?.startsWith("sz3") == true) {
            maxRange = 1.2
        }
        var zhangTingPrice = info.yesterdayPrice * maxRange
        zhangTingPrice = String.format("%.2f",zhangTingPrice).toDouble()
        return info.nowPrice > 0 && info.nowPrice >= zhangTingPrice && zhangTingPrice > 0
    }

    fun dieTing(info: SharesRecordActivity.ShareInfo): Boolean {
        var maxRange = 0.9
        if(info.code?.startsWith("sz3") == true) {
            maxRange = 0.8
        }
        var dieTing = info.yesterdayPrice * maxRange + 0.005
        dieTing = String.format("%.2f",dieTing).toDouble()
        return info.nowPrice > 0 && info.nowPrice == dieTing
    }

    open fun shouldAnalysis(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences("mode", Context.MODE_PRIVATE) //私有数据
        return sharedPreferences.getBoolean(TAG, false)
    }

    fun baoLiuXiaoShu(value: Double): String {
        return String.format("%.2f", value)
    }

    fun writeFileAppend(info: String) {
        var fileWriter: FileWriter? = null
        try {
            fileWriter = FileWriter(poolFile, true)
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
}