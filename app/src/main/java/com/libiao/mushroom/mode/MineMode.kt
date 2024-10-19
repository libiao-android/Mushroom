package com.libiao.mushroom.mode

import android.os.Environment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.libiao.mushroom.MushRoomApplication
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.room.MineShareDatabase
import com.libiao.mushroom.room.MineShareInfo
import com.libiao.mushroom.room.TestShareDatabase
import com.libiao.mushroom.room.TestShareInfo
import com.libiao.mushroom.room.report.ReportShareDatabase
import com.libiao.mushroom.room.report.ReportShareInfo
import com.libiao.mushroom.thread.ThreadPoolUtil
import com.libiao.mushroom.utils.Constant
import com.libiao.mushroom.utils.FileUtil
import com.libiao.mushroom.utils.LogUtil
import com.libiao.mushroom.utils.LogUtil.i
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.max
import kotlin.math.min

class MineMode : BaseMode {

    companion object {
        const val KEY = "MineMode"
    }

    constructor(): super() {
    }
    constructor(showTime: Boolean): super(showTime) {
    }

    private val poolMap = HashMap<String, MineShareInfo>()

    init {
        val mineShares = MineShareDatabase.getInstance()?.getMineShareDao()?.getMineShares()
        mineShares?.forEach {
            //LogUtil.i(TAG, "getMineShares: ${it.code}")
            poolMap[it.code!!] = it
        }
        LogUtil.i(TAG, "init: ${poolMap.size}")
    }

    override fun analysis(day: Int, shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = day - 20
        if(mDeviationValue >= 20) {
            val today = shares[mDeviationValue + 19]
            if (today.code?.startsWith("sh688") == true || today.name!!.contains("ST")) return
            if(poolMap.contains(today.code)) {
                i(TAG, "contains: ${today.code}")
                val info = poolMap[today.code]
                if(info?.updateTime == today.time || info == null) {
                    i(TAG, "重复记录")
                    return
                }
               val d = today.minPrice < info.price
                if (d) {
                    MineShareDatabase.getInstance()?.getMineShareDao()?.delete(today.code!!)
                    poolMap.remove(today.code)
                } else {
                    info.updateTime = today.time
                    info.dayCount = info.dayCount + 1
                    info.nowPrice = today.nowPrice
                    if (today.maxPrice > info.maxPrice) {
                        info.maxPrice = today.maxPrice
                    }
                    MineShareDatabase.getInstance()?.getMineShareDao()?.update(info)
                }
            } else {

                val one = shares[mDeviationValue + 0]
                val minList = mutableListOf<Double>()
                minList.add(one.minPrice)
                val maxList = mutableListOf<Double>()
                maxList.add(one.maxPrice)

                val maxPList = mutableListOf<Double>()
                maxPList.add(one.totalPrice)
                for (i in 1 .. 19) {
                    val two = shares[mDeviationValue + i]
                    minList.add(two.minPrice)
                    maxList.add(two.maxPrice)
                    maxPList.add(two.totalPrice)
                }

                minList.sortBy { it }
                if (one.minPrice != minList[0]) return

                maxList.sortByDescending { it }

                val h = maxList[0]
                val l = minList[0]

                if (l == 0.00) return

                val a = (h - l) / l * 100

                maxPList.sortByDescending { it }

                val b =maxPList[0] > 100000000

                if (a < 35 && a > 15 && b) {
                    val now = shares[mDeviationValue + 19]

                    val info = MineShareInfo()
                    info.time = now.time
                    info.code = now.code
                    info.name = now.name
                    info.nowPrice = now.nowPrice
                    info.price = l
                    info.dayCount = 1
                    info.updateTime = now.time
                    info.maxPrice = h

                    val id = MineShareDatabase.getInstance()?.getMineShareDao()?.insert(info)
                    info.id = id?.toInt() ?: 0
                    poolMap.put(now.code!!, info)
                    mFitModeList.add(Pair(now.range, now))
                    i(TAG, "${now.brieflyInfo()}")
                }
            }
        }
    }

    private fun reportXiaYinXian(six: SharesRecordActivity.ShareInfo, startTime: String?) {
        val a = min(six.rangeBegin, six.range) - six.rangeMin
        if(six.range < 5 && six.range > -5 && a > 2.5) {
            val info = ReportShareInfo()
            info.code = six.code
            info.time = six.time
            info.name = six.name
            info.label1 = startTime
            info.yinXianLength = a
            ReportShareDatabase.getInstance()?.getReportShareDao()?.insert(info)
            ThreadPoolUtil.execute {
                //i("HomeActivity", "load 111")
                val sourceFile = Glide.with(MushRoomApplication.sApplication)
                    .load("https://image.sinajs.cn/newchart/min/n/${six.code}.gif")
                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get()
                i(TAG, "load 222: ${sourceFile.path}")
                val appDir = File(Environment.getExternalStorageDirectory(), "A_SharesInfo/fenshi")
                if(!appDir.exists()) {
                    appDir.mkdirs()
                }
                val fileName = "${six.code}-${six.time}.jpg"
                val destFile = File(appDir, fileName)
                FileUtil.copy(sourceFile, destFile)
            }
        }
    }

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - Constant.PRE

        analysis(mDeviationValue, shares)

//        if(Constant.PRE == 0) {
//            analysis(mDeviationValue, shares)
//        } else {
//            i(TAG, "只记录当天")
//        }
    }

    private fun upLine5(info: SharesRecordActivity.ShareInfo): Boolean {
        return info.totalPrice > 0 && info.nowPrice >= info.line_5
    }

    override fun des(): String {
        return "我的"
    }
}