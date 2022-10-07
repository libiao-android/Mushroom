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
        mDeviationValue = day - 2
        if(mDeviationValue >= 20) {
            val zero = shares[mDeviationValue]
            val one = shares[mDeviationValue + 1]
            if(poolMap.contains(one.code)) {
                i(TAG, "contains: ${one.code}")
                val info = poolMap[one.code]
                if(info?.updateTime == one.time) {
                    i(TAG, "重复记录")
                    return
                }
                if(one.nowPrice < one.line_20) {
                    info?.also {
                        if(it.delete) {
                            i(TAG, "delete: ${one.code}")
                            MineShareDatabase.getInstance()?.getMineShareDao()?.delete(one.code!!)
                            poolMap.remove(one.code)
                            mFitModeList.add(Pair(one.range, one))
                        } else {
                            it.updateTime = one.time
                            it.dayCount = it.dayCount + 1

                            if(it.dayCount < 3) {
                                if(one.totalPrice > it.maxPrice) {
                                    it.maxPrice = one.totalPrice
                                }
                            } else {
                                if(one.totalPrice > it.maxPrice) {
                                    it.maxCount ++
                                    val f = TestShareInfo()
                                    f.time = one.time
                                    f.code = one.code
                                    f.name = one.name
                                    f.dayCount = it.dayCount
                                    f.startIndex = mDeviationValue + 1
                                    f.maxPrice = it.maxPrice
                                    f.maxCount = it.maxCount
                                    TestShareDatabase.getInstance()?.getTestShareDao()?.insert(f)
                                }
                            }

                            it.nowPrice = one.nowPrice
                            it.delete = true
                            if(one.minPrice <= it.duanCengPrice) {
                                it.duanCeng = false
                            }
                            MineShareDatabase.getInstance()?.getMineShareDao()?.update(it)

                            reportXiaYinXian(one)
                        }
                    }
                } else {
                    info?.also {
                        if(info.updateTime == one.time) {
                            i(TAG, "重复记录")
                        } else {
                            if(one.beginPrice > one.nowPrice && zero.nowPrice > zero.beginPrice && one.totalPrice > zero.totalPrice) {
                                it.youXuan = false
                            }
                            i(TAG, "更新记录")
                            it.updateTime = one.time
                            it.dayCount = it.dayCount + 1
                            if(it.dayCount < 3) {
                                if(one.totalPrice > it.maxPrice) {
                                    it.maxPrice = one.totalPrice
                                }
                            } else {
                                if(one.totalPrice > it.maxPrice) {
                                    it.maxCount ++
                                    val f = TestShareInfo()
                                    f.time = one.time
                                    f.code = one.code
                                    f.name = one.name
                                    f.dayCount = it.dayCount
                                    f.startIndex = mDeviationValue + 1
                                    f.maxPrice = it.maxPrice
                                    f.maxCount = it.maxCount
                                    TestShareDatabase.getInstance()?.getTestShareDao()?.insert(f)
                                }
                            }
                            it.nowPrice = one.nowPrice
                            it.delete = false
                            if(one.minPrice <= it.duanCengPrice) {
                                it.duanCeng = false
                            }
                            MineShareDatabase.getInstance()?.getMineShareDao()?.update(it)

                            reportXiaYinXian(one)


//                            if(!zhangTing(one) && one.totalPrice < 100000000) {
//                                MineShareDatabase.getInstance()?.getMineShareDao()?.delete(one.code!!)
//                            } else {
//
//                            }
                        }
                    }
                }
            } else {
                if(one.range >= 8) {
                    val info = MineShareInfo()
                    info.time = one.time
                    info.code = one.code
                    info.name = one.name
                    info.price = one.nowPrice
                    info.nowPrice = one.nowPrice
                    info.updateTime = one.time
                    info.maxPrice = one.totalPrice
                    info.youXuan = true
                    info.duanCeng = false
                    info.delete = false
                    if(one.minPrice > zero.maxPrice) {
                        info.duanCeng = true
                        info.duanCengPrice = zero.maxPrice
                    }
                    val id = MineShareDatabase.getInstance()?.getMineShareDao()?.insert(info)
                    info.id = id?.toInt() ?: 0
                    poolMap.put(one.code!!, info)
                    i(TAG, "${one.brieflyInfo()}")
                }
            }
        }
    }

    private fun reportXiaYinXian(one: SharesRecordActivity.ShareInfo) {
        val a = min(one.rangeBegin, one.range) - one.rangeMin
        if(one.range < 5 && one.range > -5 && a > 2.5) {
            val info = ReportShareInfo()
            info.code = one.code
            info.time = one.time
            info.name = one.name
            info.yinXianLength = a
            ReportShareDatabase.getInstance()?.getReportShareDao()?.insert(info)
            ThreadPoolUtil.execute {
                //i("HomeActivity", "load 111")
                val sourceFile = Glide.with(MushRoomApplication.sApplication)
                    .load("https://image.sinajs.cn/newchart/min/n/${one.code}.gif")
                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get()
                i(TAG, "load 222: ${sourceFile.path}")
                val appDir = File(Environment.getExternalStorageDirectory(), "A_SharesInfo/fenshi")
                if(!appDir.exists()) {
                    appDir.mkdirs()
                }
                val fileName = "${one.code}-${one.time}.jpg"
                val destFile = File(appDir, fileName)
                FileUtil.copy(sourceFile, destFile)
            }
        }
    }

    override fun analysis(shares: ArrayList<SharesRecordActivity.ShareInfo>) {
        val size = shares.size
        mDeviationValue = size - Constant.PRE

        //analysis(mDeviationValue, shares)

        if(Constant.PRE == 0) {
            analysis(mDeviationValue, shares)
        } else {
            i(TAG, "只记录当天")
        }
    }

    override fun des(): String {
        return "我的"
    }
}