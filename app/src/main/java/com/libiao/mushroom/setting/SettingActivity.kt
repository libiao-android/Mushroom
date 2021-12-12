package com.libiao.mushroom.setting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.libiao.mushroom.R
import kotlinx.android.synthetic.main.setting_main.*
import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import com.libiao.mushroom.mode.*
import java.io.File


class SettingActivity : AppCompatActivity() {

    private var sharedPreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting_main)
        title = "设置"

        sharedPreferences = getSharedPreferences("mode", Context.MODE_PRIVATE) //私有数据
        editor = sharedPreferences?.edit()//获取编辑器

        initView()
    }

    private fun initView() {
        sharedPreferences?.also {

            cb_long_tou.isChecked = it.getBoolean(LeaderMode.KEY, false)
            cb_long_tou.setOnCheckedChangeListener { _, isChecked ->
                editor?.putBoolean(LeaderMode.KEY, isChecked)
                editor?.commit()//提交修改
            }

            cb_50.isChecked = it.getBoolean(StrongStock50Mode.KEY, false)
            cb_50.setOnCheckedChangeListener { _, isChecked ->
                editor?.putBoolean(StrongStock50Mode.KEY, isChecked)
                editor?.commit()//提交修改
            }

            cb_20.isChecked = it.getBoolean(Strong20Mode.KEY, false)
            cb_20.setOnCheckedChangeListener { _, isChecked ->
                editor?.putBoolean(Strong20Mode.KEY, isChecked)
                editor?.commit()//提交修改
            }

            cb_10.isChecked = it.getBoolean(StrongStock10Mode.KEY, false)
            cb_10.setOnCheckedChangeListener { _, isChecked ->
                editor?.putBoolean(StrongStock10Mode.KEY, isChecked)
                editor?.commit()//提交修改
            }

            cb_yi_zi_ban.isChecked = it.getBoolean(YiZiBanMode.KEY, false)
            cb_yi_zi_ban.setOnCheckedChangeListener { _, isChecked ->
                editor?.putBoolean(YiZiBanMode.KEY, isChecked)
                editor?.commit()//提交修改
            }

            cb_fang_liang_3.isChecked = it.getBoolean(MoreMore3Mode.KEY, false)
            cb_fang_liang_3.setOnCheckedChangeListener { _, isChecked ->
                editor?.putBoolean(MoreMore3Mode.KEY, isChecked)
                editor?.commit()//提交修改
            }

            cb_fen_qi_to_yi_zhi.isChecked = it.getBoolean(Difference2FitMode.KEY, false)
            cb_fen_qi_to_yi_zhi.setOnCheckedChangeListener { _, isChecked ->
                editor?.putBoolean(Difference2FitMode.KEY, isChecked)
                editor?.commit()//提交修改
            }

            cb_chuang_ye_ban.isChecked = it.getBoolean(ChuangYeBanTouJiMode.KEY, false)
            cb_chuang_ye_ban.setOnCheckedChangeListener { _, isChecked ->
                editor?.putBoolean(ChuangYeBanTouJiMode.KEY, isChecked)
                editor?.commit()//提交修改
            }

            cb_up_10.isChecked = it.getBoolean(UpLine10Mode.KEY, false)
            cb_up_10.setOnCheckedChangeListener { _, isChecked ->
                editor?.putBoolean(UpLine10Mode.KEY, isChecked)
                editor?.commit()//提交修改
            }
        }

        btn_delete_cache.setOnClickListener {
            val myPoolFile20 = File(Environment.getExternalStorageDirectory(), "A_SharesInfo/my_20_pool")
            val myPoolFile50 = File(Environment.getExternalStorageDirectory(), "A_SharesInfo/my_50_pool")
            myPoolFile20.delete()
            myPoolFile50.delete()
        }
    }
}