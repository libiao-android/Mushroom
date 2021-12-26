package com.libiao.mushroom

import android.content.Context
import android.content.Intent
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.libiao.mushroom.kline.KLineActivity
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.Charset


class MyPool20Activity : AppCompatActivity() {

    companion object {
        private const val TAG = "MyPool20Activity"
    }

    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: MyPoolInfoAdater? = null

    private val myPoolFile = File(Environment.getExternalStorageDirectory(), "A_SharesInfo/my_20_pool")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_pool_activity)
        title = "我的股票池-20"

        mRecyclerView = findViewById(R.id.my_pool_rv)
        mRecyclerView?.layoutManager = LinearLayoutManager(this)
        mAdapter = MyPoolInfoAdater(this)
        mRecyclerView?.adapter = mAdapter

        if(!myPoolFile.exists()) {
            myPoolFile.createNewFile()
        }

        val stream = FileInputStream(myPoolFile)
        val reader = BufferedReader(InputStreamReader(stream, Charset.defaultCharset()))
        val data = ArrayList<String>()
        var str: String?
        str = reader.readLine()
        while (str != null) {
            data.add(str)
            str = reader.readLine()
        }

        mAdapter?.setData(data)

    }


    class MyPoolInfoAdater(val context: Context) : RecyclerView.Adapter<MyPoolHolder>() {

        private var mSharesInfoList: ArrayList<String> = ArrayList()

        fun setData(sharesInfoList: ArrayList<String>) {
            mSharesInfoList = sharesInfoList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPoolHolder {
            return MyPoolHolder(context,
                LayoutInflater.from(context).inflate(
                    R.layout.my_pool_info_item,
                    null
                )
            )
        }

        override fun getItemCount(): Int {
            return mSharesInfoList.size
        }

        override fun onBindViewHolder(holder: MyPoolHolder, position: Int) {
            holder.bindData(mSharesInfoList.get(position))
        }
    }

    class MyPoolHolder(context: Context, view: View) : RecyclerView.ViewHolder(view) {

        private var mNameTv: TextView? = null
        private var mCodeTv: TextView? = null
        private var mPriceTv: TextView? = null
        private var mLine10Tv: TextView? = null
        private var mLine20Tv: TextView? = null

        private var info: String? = null

        private val file_2021 = File(Environment.getExternalStorageDirectory(), "A_SharesInfo/2021")


        init {
            mNameTv = view.findViewById(R.id.name)
            mCodeTv = view.findViewById(R.id.code)
            mPriceTv = view.findViewById(R.id.price)
            mLine10Tv = view.findViewById(R.id.line10)
            mLine20Tv = view.findViewById(R.id.line20)

            view.setOnClickListener {
                val intent = Intent(context, KLineActivity::class.java)
                intent.putExtra("code", info?.split(",")?.get(1)?.trim())
                intent.putExtra("info", info)
                context.startActivity(intent)
            }

        }

        fun bindData(info: String) {
            this.info = info
            mNameTv?.text = info
        }
    }
}
