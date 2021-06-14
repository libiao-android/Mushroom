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


    fun content(): String {
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
            if(firstDay.totalPrice > 50000000 && multiple > 2.5 && multiple < 4) {
                //Log.i("libiao", "放量：${today.toFile()}, multiple: $multiple")
                return true
            }
        }
        return false
    }

    fun isShiZiXing(begin: Double, end: Double): Boolean {
        //return false
        return abs(begin - end) / min(begin, end) < 0.0019
    }

    fun getRange(begin: Double, end: Double): Double {
        val s = String.format("%.4f",(end - begin)  / begin * 100)
        return s.toDouble()
    }
}