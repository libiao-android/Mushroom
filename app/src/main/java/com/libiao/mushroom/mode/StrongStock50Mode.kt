package com.libiao.mushroom.mode

import android.os.Environment
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil
import java.io.*
import java.nio.charset.Charset
import kotlin.math.max
import kotlin.math.min

class StrongStock50Mode() : BaseMode() {

    companion object {
        const val KEY = "StrongStock50Mode"
    }


    init {
        poolFile = File(fileNew, "my_50_pool")
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
        mDeviationValue = size - 10 - Constant.PRE

        if(mDeviationValue >= 0) {
            val day1 = shares[mDeviationValue + 0]
            if(day1.name?.contains("ST") == true) return
            val day2 = shares[mDeviationValue + 1]
            val day3 = shares[mDeviationValue + 2]
            val day4 = shares[mDeviationValue + 3]
            val day5 = shares[mDeviationValue + 4]
            val day6 = shares[mDeviationValue + 5]
            val day7 = shares[mDeviationValue + 6]
            val day8 = shares[mDeviationValue + 7]
            val day9 = shares[mDeviationValue + 8]
            val day10 = shares[mDeviationValue + 9]
            if(day1.huanShouLv > 0) {
                var min = day1.beginPrice
                var max = day1.nowPrice

                var temp = day2
                min = min(min, temp.beginPrice)
                max = max(max, temp.nowPrice)

                temp = day3
                min = min(min, temp.beginPrice)
                max = max(max, temp.nowPrice)

                temp = day4
                min = min(min, temp.beginPrice)
                max = max(max, temp.nowPrice)

                temp = day5
                min = min(min, temp.beginPrice)
                max = max(max, temp.nowPrice)

                temp = day6
                min = min(min, temp.beginPrice)
                max = max(max, temp.nowPrice)

                temp = day7
                min = min(min, temp.beginPrice)
                max = max(max, temp.nowPrice)

                temp = day8
                min = min(min, temp.beginPrice)
                max = max(max, temp.nowPrice)

                temp = day9
                min = min(min, temp.beginPrice)
                max = max(max, temp.nowPrice)

                temp = day10
                min = min(min, temp.beginPrice)
                max = max(max, temp.nowPrice)

                if(day10.beginPrice > day1.beginPrice && min > 0) {
                    val range = (max - min) / min * 100
                    if(range > 50) {
                        val info = "${day10.time}, ${day10.code}, ${day10.name}, ${range.toInt()}"
                        val result = contains(day10.code!!)
                        if(result == null) {
                            LogUtil.i(TAG, "${day10.brieflyInfo()}, $range")
                            poolList.add(info)
                            writeFileAppend(info)
                            day10.post1 = range.toInt().toString()
                            mFitModeList.add(Pair(range, day10))
                        } else {
//                            if(result.contains(day10.time!!)) {
//                                day10.post1 = range.toInt().toString()
//                                mFitModeList.add(Pair(range, day10))
//                            }
                        }
                    }
                }
            }
        }
    }

    private fun contains(code: String): String? {
        poolList.reversed().forEach {
            if(it.contains(code)) return it
        }
        return null
    }

    override fun des(): String {
        return "50"
    }
}