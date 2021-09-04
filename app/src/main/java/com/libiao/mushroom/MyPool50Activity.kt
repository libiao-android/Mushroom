package com.libiao.mushroom

import android.content.Context
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.Charset


class MyPool50Activity : AppCompatActivity() {

    companion object {
        private const val TAG = "MyPool50Activity"
    }

    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: MyPoolInfoAdater? = null

    private val myPoolFile = File(Environment.getExternalStorageDirectory(), "A_SharesInfo/my_50_pool")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_pool_activity)
        title = "我的股票池-50"

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
            return MyPoolHolder(
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

    class MyPoolHolder(view: View) : RecyclerView.ViewHolder(view) {

        private var mNameTv: TextView? = null
        private var mCodeTv: TextView? = null
        private var mPriceTv: TextView? = null
        private var mLine10Tv: TextView? = null
        private var mLine20Tv: TextView? = null

        private val file_2021 = File(Environment.getExternalStorageDirectory(), "A_SharesInfo/2021")


        init {
            mNameTv = view.findViewById(R.id.name)
            mCodeTv = view.findViewById(R.id.code)
            mPriceTv = view.findViewById(R.id.price)
            mLine10Tv = view.findViewById(R.id.line10)
            mLine20Tv = view.findViewById(R.id.line20)

        }

        fun bindData(info: String) {

            val f = File(file_2021, info)
            if(f.exists()) {
                val stream = FileInputStream(f)
                val reader = BufferedReader(InputStreamReader(stream, Charset.defaultCharset()))
                val lines = reader.readLines()
                val shares = ArrayList<SharesRecordActivity.ShareInfo>()
                for(line in lines) {
                    shares.add(SharesRecordActivity.ShareInfo(line))
                }
                if(shares.size > 0) {
                    val last = shares.last()
                    mNameTv?.text = last.name
                    mCodeTv?.text = last.code
                    mPriceTv?.text = last.nowPrice.toString()
                    mLine10Tv?.text = last.line_10.toString()
                    mLine20Tv?.text = last.line_20.toString()
                }
            }
        }
    }
}
