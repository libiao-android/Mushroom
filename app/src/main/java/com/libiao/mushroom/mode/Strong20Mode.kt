package com.libiao.mushroom.mode

import android.os.Environment
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil
import com.libiao.mushroom.utils.LogUtil.i
import java.io.*
import java.nio.charset.Charset
import kotlin.math.abs

class Strong20Mode : BaseMode() {

    companion object {
        const val KEY = "Strong20Mode"
    }

    private val fileNew = File(Environment.getExternalStorageDirectory(), "A_SharesInfo")
    private val poolList = java.util.ArrayList<String>()
    private var poolFile: File? = null

    init {
        poolFile = File(fileNew, "my_20_pool")
        if(poolFile?.exists() == false) {
            poolFile?.createNewFile()
        } else {
            val stream = FileInputStream(poolFile)
            val reader = BufferedReader(InputStreamReader(stream, Charset.defaultCharset()))
            var str: String?
            str = reader.readLine()
            while (str != null) {
                poolList.add(str)
                str = reader.readLine()
            }
        }
    }

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - 9 - Constant.PRE
        if(mDeviationValue >=  0) {

            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]

            val four = shares[mDeviationValue + 3]
            val five = shares[mDeviationValue + 4]
            val six = shares[mDeviationValue + 5]
            val seven = shares[mDeviationValue + 6]
            val eight = shares[mDeviationValue + 7]
            val nine = shares[mDeviationValue + 8]

            val r1 = one.range + two.range + three.range > 16
            val r2 = four.range + two.range + three.range > 16
            val r3 = four.range + five.range + three.range > 16
            val r4 = four.range + five.range + six.range > 16

            var index = 0

            var zhangTing = zhangTing(one)
                    || zhangTing(two)
                    || zhangTing(three)
                    || zhangTing(four)
                    || zhangTing(five)
                    || zhangTing(six)
            if(r1) {
                index = 0
            }
            if(r2) {
                index = 1
            }
            if(r3) {
                index = 2
            }
            if(r4) {
                index = 3
            }

            if (r1 || r2 || r3 || r4) {

                if(six.minPrice > six.line_10
                    && seven.minPrice > seven.line_10
                    && eight.minPrice > eight.line_10
                    && eight.beginPrice > eight.nowPrice
                    && nine.nowPrice > nine.beginPrice
                    && nine.totalPrice > eight.totalPrice
                    && nine.minPrice >= nine.line_10
                    && nine.range < 5
                    && nine.rangeMax > 0
                    && (nine.rangeMax - nine.range) < 5
                    && (nine.minPrice - nine.line_10) / nine.line_10 < 0.03

                    && zhangTing
                ) {
                    val list = mutableListOf<SharesRecordActivity.ShareInfo>()
                    list.add(one)
                    list.add(two)
                    list.add(three)
                    list.add(four)
                    list.add(five)
                    list.add(six)
                    list.add(seven)
                    list.add(eight)
                    list.add(nine)
                    val isRedMore = isRedMore(list, index)
                    val isHight = isHight(list, index)

                    var total = 0.00
                    for( i in index until list.size - 1) {
                        if(list[i].totalPrice > total) total = list[i].totalPrice
                    }
                    val liang = total / list[list.size - 1].totalPrice
                    val d_10 = (nine.minPrice - nine.line_10) / nine.line_10 * 100

                    if(isRedMore && !isHight && liang > 10/7.0 && liang < 10/3.0) {
                        i(TAG, "${nine.brieflyInfo()}, $zhangTing, $index")
                        nine.post1 = baoLiuXiaoShu(d_10)
                        nine.post2 = baoLiuXiaoShu(liang)
                        mFitModeList.add(Pair(nine.range, nine))
                    }
                }
            }
        }
    }

    private fun isHight(list: MutableList<SharesRecordActivity.ShareInfo>, index: Int): Boolean {
        var p = 0.00
        for( i in index until list.size - 1) {
            if(list[i].nowPrice > p) p = list[i].nowPrice
        }
        return list[list.size - 1].nowPrice > p * 0.98
    }

    private fun isRedMore(list: MutableList<SharesRecordActivity.ShareInfo>, index: Int): Boolean {
        var total_red = 0.00
        var total_green = 0.00
        for( i in index until list.size) {
            val temp = list[i]
            if(temp.beginPrice > temp.nowPrice) {
                total_green += temp.totalPrice
            } else {
                total_red += temp.totalPrice
            }
        }
        return total_red > total_green
    }

    private fun writeFileAppend(info: String) {
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

    override fun des(): String {
        return "20"
    }
}