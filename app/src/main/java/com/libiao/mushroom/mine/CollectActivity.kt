package com.libiao.mushroom.mine

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.libiao.mushroom.R
import com.libiao.mushroom.base.BaseActivity
import com.libiao.mushroom.kline.KLineActivity
import com.libiao.mushroom.room.CollectShareInfo
import com.libiao.mushroom.room.MineShareDatabase
import com.libiao.mushroom.utils.ClipboardUtil
import com.libiao.mushroom.utils.LogUtil
import kotlinx.android.synthetic.main.collect_activity.*
import java.text.SimpleDateFormat

class CollectActivity : BaseActivity() {

    companion object {
        private const val TAG = "CollectActivity"
    }

    private var mAdapter: CollectInfoAdater? = null

    private var mData: List<CollectShareInfo>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.collect_activity)

        collect_rv?.layoutManager = LinearLayoutManager(this)
        mAdapter = CollectInfoAdater(this)
        collect_rv?.adapter = mAdapter

        initView()
    }

    private fun initView() {

    }

    override fun onResume() {
        super.onResume()
        val data = MineShareDatabase.getInstance()?.getCollectShareDao()?.getCollectShares()
        LogUtil.i(TAG, "data: ${data?.size}")
        data?.also {
            mData = java.util.ArrayList(it)
            mAdapter?.setData(mData!!)
        }
    }

    private fun deleteItem(info: CollectShareInfo?) {
        (mData as java.util.ArrayList).remove(info)
        mAdapter?.setData(mData!!)
        MineShareDatabase.getInstance()?.getCollectShareDao()?.delete(info?.code!!)
    }


    class CollectInfoAdater(val context: Context) : RecyclerView.Adapter<CollectHolder>() {

        private var mSharesInfoList: List<CollectShareInfo> = ArrayList()


        fun setData(sharesInfoList: List<CollectShareInfo>) {
            mSharesInfoList = sharesInfoList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectHolder {
            return CollectHolder(context,
                LayoutInflater.from(context).inflate(
                    R.layout.collect_item,
                    null
                )
            )
        }

        override fun getItemCount(): Int {
            return mSharesInfoList.size
        }

        override fun onBindViewHolder(holder: CollectHolder, position: Int) {
            holder.bindData(position, mSharesInfoList[position])
        }
    }

    class CollectHolder(context: Context, view: View) : RecyclerView.ViewHolder(view) {

        private var mIdTv: TextView? = null
        private var mNameTv: TextView? = null
        private var mCodeTv: TextView? = null
        private var mTimeTv: TextView? = null
        private var mDeleteIv: ImageView? = null

        private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")// HH:mm:ss


        private var collectShareInfo: CollectShareInfo? = null


        init {
            mIdTv = view.findViewById(R.id.collect_item_id)
            mTimeTv = view.findViewById(R.id.collect_item_time)
            mNameTv = view.findViewById(R.id.collect_item_name)
            mCodeTv = view.findViewById(R.id.collect_item_code)
            mDeleteIv = view.findViewById(R.id.collect_item_delete)


            view.setOnClickListener {
                val intent = Intent(context, KLineActivity::class.java)
                intent.putExtra("code", collectShareInfo?.code)
                intent.putExtra("info", collectShareInfo.toString())
                context.startActivity(intent)
            }

            mIdTv?.setOnClickListener {
                ClipboardUtil.clip(context, collectShareInfo?.code)
            }

            mDeleteIv?.setOnClickListener {
                (context as CollectActivity).deleteItem(collectShareInfo)
            }

        }

        fun bindData(position: Int, info: CollectShareInfo) {
            this.collectShareInfo = info
            mIdTv?.text = position.toString()
            mTimeTv?.text = simpleDateFormat.format(info.time)
            mNameTv?.text = info.name
            mCodeTv?.text = info.code?.substring(2)
        }
    }
}