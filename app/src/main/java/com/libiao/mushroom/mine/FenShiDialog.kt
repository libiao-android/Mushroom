package com.libiao.mushroom.mine

import android.app.Dialog
import android.content.Context
import android.os.Environment
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.Target
import com.libiao.mushroom.MushRoomApplication
import com.libiao.mushroom.R
import com.libiao.mushroom.thread.ThreadPoolUtil
import com.libiao.mushroom.utils.FileUtil
import com.libiao.mushroom.utils.LogUtil
import kotlinx.android.synthetic.main.fenshi_dialog_layout.*
import kotlinx.android.synthetic.main.more_dialog_layout.*
import java.io.File

class FenShiDialog : Dialog {

    constructor(context: Context, code: String, time: String? = null): super(context, R.style.baseDialog) {
        setContentView(R.layout.fenshi_dialog_layout)
        Glide.with(context).load("https://image.sinajs.cn/newchart/min/n/${code}.gif")
            .asGif()
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(iv_fenshi)

        if(time == null) {
            btn_download.visibility = View.GONE
            btn_download2.visibility = View.GONE
        } else {
            btn_download.visibility = View.VISIBLE
            btn_download.setOnClickListener {
                ThreadPoolUtil.execute {
                    //i("HomeActivity", "load 111")
                    val sourceFile = Glide.with(MushRoomApplication.sApplication)
                        .load("https://image.sinajs.cn/newchart/min/n/${code}.gif")
                        .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .get()
                    LogUtil.i("FenShiDialog", "load 222: ${sourceFile.path}")
                    val appDir = File(Environment.getExternalStorageDirectory(), "A_SharesInfo/fenshi")
                    if(!appDir.exists()) {
                        appDir.mkdirs()
                    }
                    val fileName = "${code}-${time}.jpg"
                    val destFile = File(appDir, fileName)
                    if(destFile.exists()) {
                        destFile.delete()
                    }
                    FileUtil.copy(sourceFile, destFile)
                }
            }

            btn_download2.visibility = View.VISIBLE
            btn_download2.setOnClickListener {
                ThreadPoolUtil.execute {
                    //i("HomeActivity", "load 111")
                    val sourceFile = Glide.with(MushRoomApplication.sApplication)
                        .load("https://image.sinajs.cn/newchart/min/n/${code}.gif")
                        .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .get()
                    LogUtil.i("FenShiDialog", "load 222: ${sourceFile.path}")
                    val appDir = File(Environment.getExternalStorageDirectory(), "A_SharesInfo/fenshi")
                    if(!appDir.exists()) {
                        appDir.mkdirs()
                    }
                    val fileName = "${code}-${time}-2.jpg"
                    val destFile = File(appDir, fileName)
                    if(destFile.exists()) {
                        destFile.delete()
                    }
                    FileUtil.copy(sourceFile, destFile)
                }
            }
        }
    }
}