package com.libiao.mushroom.mode

import android.os.Environment
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.LogUtil
import com.libiao.mushroom.utils.LogUtil.i
import java.io.*
import java.nio.charset.Charset

class Strong20Mode : BaseMode() {

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
        mDeviationValue = size - 8 - Constant.PRE
        if(mDeviationValue >=  0) {

            val one = shares[mDeviationValue + 0]
            val two = shares[mDeviationValue + 1]
            val three = shares[mDeviationValue + 2]

            val four = shares[mDeviationValue + 3]
            val five = shares[mDeviationValue + 4]
            val six = shares[mDeviationValue + 5]
            val seven = shares[mDeviationValue + 6]
            val eight = shares[mDeviationValue + 7]

            val r1 = one.range + two.range + three.range > 20
            val r2 = four.range + two.range + three.range > 20
            val r3 = four.range + five.range + three.range > 20
            var m = 0.00
            if(r1) {
                m = (three.nowPrice + one.beginPrice) / 2
            }
            if(r2) {
                m = (four.nowPrice + two.beginPrice) / 2
            }
            if(r3) {
                m = (five.nowPrice + three.beginPrice) / 2
            }


            if (r1 || r2 || r3) {

                if(six.minPrice > six.line_10 && seven.minPrice > seven.line_10 && eight.minPrice > eight.line_10 && eight.nowPrice < eight.line_5) {

                    if(eight.nowPrice > m) {

                        if(!poolList.contains(eight.code)) {
                            eight.code?.also {
                                poolList.add(it)
                                writeFileAppend(it)
                            }
                            i(TAG, "${six.brieflyInfo()}, $m")
                            mFitModeList.add(Pair(six.range, six))

                        }
                    }

                }


            }
        }
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