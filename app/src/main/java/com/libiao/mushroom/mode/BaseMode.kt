package com.libiao.mushroom.mode

import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.LogUtil.i
import java.lang.StringBuilder
import kotlin.math.abs
import kotlin.math.min

abstract class BaseMode {

    val TAG = javaClass.simpleName

    var mDeviationValue = 0
    val mFitModeList = ArrayList<Pair<Double, SharesRecordActivity.ShareInfo?>>()


    abstract fun analysis(shares : ArrayList<SharesRecordActivity.ShareInfo>)
    abstract fun des(): String


    open fun content(): String {
        val size = mFitModeList.size
        i(TAG, "mFitModeList: $size")
        mFitModeList.sortByDescending { it.first }

        val sb = StringBuilder("")

        for(i in 0 until size) {
            sb.append("${mFitModeList[i].second?.code}" +
                    ",  ${mFitModeList[i].second?.name}" +
                    ",  ${String.format("%.4f",mFitModeList[i].first)}" +
                    ",  ${mFitModeList[i].second?.postRange}" +
                    ",  ${mFitModeList[i].second?.zongShiZhi}" +
                    " \n")
        }

        return sb.toString()
    }

    fun clear() {
        mFitModeList.clear()
    }

    fun more(firstDay: SharesRecordActivity.ShareInfo, secondDay: SharesRecordActivity.ShareInfo): Boolean {

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
        return code?.startsWith("sz300") ?: false
    }
}