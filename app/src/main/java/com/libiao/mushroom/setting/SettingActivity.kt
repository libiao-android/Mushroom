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

//            cb_long_tou.isChecked = it.getBoolean(LeaderMode.KEY, false)
//            cb_long_tou.setOnCheckedChangeListener { _, isChecked ->
//                editor?.putBoolean(LeaderMode.KEY, isChecked)
//                editor?.commit()//提交修改
//            }

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

            cb_up_10.isChecked = it.getBoolean(UpLine10Mode.KEY, false)
            cb_up_10.setOnCheckedChangeListener { _, isChecked ->
                editor?.putBoolean(UpLine10Mode.KEY, isChecked)
                editor?.commit()//提交修改
            }

            lian_ban_1.isChecked = it.getBoolean(LianBan1Mode.KEY, false)
            lian_ban_1.setOnCheckedChangeListener { _, isChecked ->
                editor?.putBoolean(LianBan1Mode.KEY, isChecked)
                editor?.commit()//提交修改
            }

            lian_ban_2.isChecked = it.getBoolean(LianBan2Mode.KEY, false)
            lian_ban_2.setOnCheckedChangeListener { _, isChecked ->
                editor?.putBoolean(LianBan2Mode.KEY, isChecked)
                editor?.commit()//提交修改
            }

            lian_ban_3.isChecked = it.getBoolean(LianBan3Mode.KEY, false)
            lian_ban_3.setOnCheckedChangeListener { _, isChecked ->
                editor?.putBoolean(LianBan3Mode.KEY, isChecked)
                editor?.commit()//提交修改
            }

            lian_ban_4.isChecked = it.getBoolean(LianBan4Mode.KEY, false)
            lian_ban_4.setOnCheckedChangeListener { _, isChecked ->
                editor?.putBoolean(LianBan4Mode.KEY, isChecked)
                editor?.commit()//提交修改
            }

            lian_ban_5.isChecked = it.getBoolean(LianBan5Mode.KEY, false)
            lian_ban_5.setOnCheckedChangeListener { _, isChecked ->
                editor?.putBoolean(LianBan5Mode.KEY, isChecked)
                editor?.commit()//提交修改
            }

            lian_ban_6.isChecked = it.getBoolean(LianBan6Mode.KEY, false)
            lian_ban_6.setOnCheckedChangeListener { _, isChecked ->
                editor?.putBoolean(LianBan6Mode.KEY, isChecked)
                editor?.commit()//提交修改
            }

            chuang_1.isChecked = it.getBoolean(LianBanChuang1Mode.KEY, false)
            chuang_1.setOnCheckedChangeListener { _, isChecked ->
                editor?.putBoolean(LianBanChuang1Mode.KEY, isChecked)
                editor?.commit()//提交修改
            }

            chuang_2.isChecked = it.getBoolean(LianBanChuang2Mode.KEY, false)
            chuang_2.setOnCheckedChangeListener { _, isChecked ->
                editor?.putBoolean(LianBanChuang2Mode.KEY, isChecked)
                editor?.commit()//提交修改
            }

            chuang_3.isChecked = it.getBoolean(LianBanChuang3Mode.KEY, false)
            chuang_3.setOnCheckedChangeListener { _, isChecked ->
                editor?.putBoolean(LianBanChuang3Mode.KEY, isChecked)
                editor?.commit()//提交修改
            }

            lian_ban_2_strong.isChecked = it.getBoolean(LianBan2StrongMode.KEY, false)
            lian_ban_2_strong.setOnCheckedChangeListener { _, isChecked ->
                editor?.putBoolean(LianBan2StrongMode.KEY, isChecked)
                editor?.commit()//提交修改
            }

            lian_ban_3_strong.isChecked = it.getBoolean(LianBan3StrongMode.KEY, false)
            lian_ban_3_strong.setOnCheckedChangeListener { _, isChecked ->
                editor?.putBoolean(LianBan3StrongMode.KEY, isChecked)
                editor?.commit()//提交修改
            }

            chuang_1_strong.isChecked = it.getBoolean(LianBanChuang1StrongMode.KEY, false)
            chuang_1_strong.setOnCheckedChangeListener { _, isChecked ->
                editor?.putBoolean(LianBanChuang1StrongMode.KEY, isChecked)
                editor?.commit()//提交修改
            }

            lian_ban_1_strong.isChecked = it.getBoolean(LianBan1StrongMode.KEY, false)
            lian_ban_1_strong.setOnCheckedChangeListener { _, isChecked ->
                editor?.putBoolean(LianBan1StrongMode.KEY, isChecked)
                editor?.commit()//提交修改
            }

            mine.isChecked = it.getBoolean(MineMode.KEY, false)
            mine.setOnCheckedChangeListener { _, isChecked ->
                editor?.putBoolean(MineMode.KEY, isChecked)
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