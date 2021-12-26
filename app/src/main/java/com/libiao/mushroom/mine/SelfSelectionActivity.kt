package com.libiao.mushroom.mine

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.libiao.mushroom.R
import com.libiao.mushroom.SharesRecordActivity
import com.libiao.mushroom.analysis.SharesRealTimeInfoActivity
import com.libiao.mushroom.base.BaseActivity
import com.libiao.mushroom.kline.KLineActivity
import com.libiao.mushroom.room.MineShareDatabase
import com.libiao.mushroom.room.MineShareInfo
import com.libiao.mushroom.thread.ThreadPoolUtil
import com.libiao.mushroom.utils.ClipboardUtil
import com.libiao.mushroom.utils.LogUtil
import com.libiao.mushroom.utils.ShareParseUtil
import com.libiao.mushroom.utils.baoLiuXiaoShu
import kotlinx.android.synthetic.main.self_selection_activity.*
import okhttp3.*
import java.io.*
import java.lang.Exception
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList


class SelfSelectionActivity : BaseActivity() {

    companion object {
        private const val TAG = "SelfSelectionActivity"
    }

    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: MyPoolInfoAdater? = null

    private var mRangeStatus = 0
    private var mTodayStatus = 0

    private val file_2021 = File(Environment.getExternalStorageDirectory(), "A_SharesInfo/2021")
    private val client = OkHttpClient()

    private var mData: List<MineShareInfo> = ArrayList()

    private var mRefreshCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.self_selection_activity)

        mRecyclerView = findViewById(R.id.self_selection_rv)
        mRecyclerView?.layoutManager = LinearLayoutManager(this)
        mAdapter = MyPoolInfoAdater(this)
        mRecyclerView?.adapter = mAdapter

        initView()

        val data = MineShareDatabase.getInstance()?.getMineShareDao()?.getMineShares()
        refreshLocalData(data)

    }

    private fun refreshLocalData(data: List<MineShareInfo>?) {
        ThreadPoolUtil.execute(Runnable {
            data?.forEach {
                val f = File(file_2021, it.code)
                if(f.exists()) {
                    val stream = FileInputStream(f)
                    val reader = BufferedReader(InputStreamReader(stream, Charset.defaultCharset()))
                    val lines = reader.readLines()

                    val info = lines.get(lines.size - 1)
                    val info_pre = lines.get(lines.size - 2)
                    val share = SharesRecordActivity.ShareInfo(info)
                    val share_pre = SharesRecordActivity.ShareInfo(info_pre)
                    updateCurrentData(it, share, share_pre)

                    //LogUtil.i(TAG, "info: $info")
                    //LogUtil.i(TAG, "info_pre: $info_pre")
                }
            }
            ThreadPoolUtil.executeUI(Runnable {
                data?.also {
                    mData = it
                    mAdapter?.setData(it)
                    tv_count.text = "count: ${mData.size}"
                }
            })
        })
    }

    private fun updateCurrentData(
        it: MineShareInfo,
        share: SharesRecordActivity.ShareInfo,
        sharePre: SharesRecordActivity.ShareInfo
    ) {
        if(it.price > 0) {
            it.totalRange = (share.nowPrice - it.price) / it.price * 100
            it.todayRange = share.range
            it.fangLiang = share.totalPrice > sharePre.totalPrice
            if(share.nowPrice > share.beginPrice) {
                it.redLine = true
            } else if(share.nowPrice == share.beginPrice) {
                it.redLine = share.range >= 0
            } else {
                it.redLine = false
            }
            if(it.redLine && it.fangLiang && it.todayRange > 0) {
                if(it.todayRange <= 6) {
                    it.priority = 4
                } else {
                    it.priority = 3
                }
            } else if(it.redLine) {
                it.priority = 2
            } else {
                it.priority = 1
            }
            val liangBi = baoLiuXiaoShu(share.totalPrice / sharePre.totalPrice)
            it.moreInfo = "${share.rangeBegin},  ${share.rangeMin},  ${share.rangeMax},  ${liangBi}"
        }
    }

    private fun initView() {

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")// HH:mm:ss
        val time = simpleDateFormat.format(Date())
        tv_current_time.text = "$time"

        cb_network.setOnCheckedChangeListener { buttonView, isChecked ->
            btn_refresh.isEnabled = isChecked
            if(!isChecked) {
                refreshLocalData(mData)
                resetSortUI()
            }
        }

        btn_refresh.setOnClickListener {
            self_loading.visibility = View.VISIBLE
            tv_counting.visibility = View.VISIBLE
            btn_refresh.isEnabled = false
            cb_network.isEnabled = false
            mRefreshCount = 0
            mData.forEach {
                shiShiQuery(it)
            }
        }


        view_self_range.setOnClickListener {
            when(mRangeStatus) {
                0 -> {
                    iv_self_range.setImageResource(R.mipmap.sort_descending)
                    mRangeStatus = 1
                    //高-低
                    val temp = mData.sortedByDescending { it.totalRange }
                    mAdapter?.setData(temp)
                }
                1 -> {
                    iv_self_range.setImageResource(R.mipmap.sort_ascending)
                    mRangeStatus = 2
                    //低-高
                    val temp = mData.sortedBy { it.totalRange }
                    mAdapter?.setData(temp)
                }
                2 -> {
                    iv_self_range.setImageResource(R.mipmap.sort_descending)
                    mRangeStatus = 1
                    //高-低
                    val temp = mData.sortedByDescending { it.totalRange }
                    mAdapter?.setData(temp)
                }
            }
            mTodayStatus = 0
            iv_self_today.setImageResource(R.mipmap.sort_normal)
        }

        view_self_today.setOnClickListener {
            when(mTodayStatus) {
                0 -> {
                    iv_self_today.setImageResource(R.mipmap.sort_descending)
                    mTodayStatus = 1
                    //高-低
                    //val temp = mData.sortedByDescending { it.todayRange }

                    val temp = mData.sortedWith(Comparator<MineShareInfo> { o1, o2 ->
                        if(o1.priority > o2.priority) {
                            -1
                        } else if(o1.priority < o2.priority) {
                            1
                        } else {
                            if(o1.todayRange > o2.todayRange) {
                                -1
                            } else {
                                1
                            }
                        }
                    })
                    mAdapter?.setData(temp)
                }
                1 -> {
                    iv_self_today.setImageResource(R.mipmap.sort_ascending)
                    mTodayStatus = 2
                    //低-高
                    //val temp = mData.sortedBy { it.todayRange }
                    val temp = mData.sortedWith(Comparator<MineShareInfo> { o1, o2 ->
                        if(o1.priority > o2.priority) {
                            1
                        } else if(o1.priority < o2.priority) {
                            -1
                        } else {
                            if(o1.todayRange > o2.todayRange) {
                                1
                            } else {
                                -1
                            }
                        }
                    })
                    mAdapter?.setData(temp)

                }
                2 -> {
                    iv_self_today.setImageResource(R.mipmap.sort_descending)
                    mTodayStatus = 1
                    //高-低
                    //val temp = mData.sortedByDescending { it.todayRange }
                    val temp = mData.sortedWith(Comparator<MineShareInfo> { o1, o2 ->
                        if(o1.priority > o2.priority) {
                            -1
                        } else if(o1.priority < o2.priority) {
                            1
                        } else {
                            if(o1.todayRange > o2.todayRange) {
                                -1
                            } else {
                                1
                            }
                        }
                    })
                    mAdapter?.setData(temp)
                }
            }
            mRangeStatus = 0
            iv_self_range.setImageResource(R.mipmap.sort_normal)
        }
    }



    private fun shiShiQuery(info: MineShareInfo) {
        val code = info.code
        //http://qt.gtimg.cn/q=s_sz000858
        val request = Request.Builder()
            .url("https://qt.gtimg.cn/q=$code")
            .build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                LogUtil.i(TAG, "onFailure: ${e}, $code")
            }

            override fun onResponse(call: Call?, response: Response?) {
                val value = response?.body()?.string()
                //Log.i("libiao_A", "response: ${value}")
                try {
                    if (value != null) {
                        //LogUtil.i(TAG, "response: ${value}")
                        val strs = value.split("~")
                        if(strs.size > 45) {
                            val share = ShareParseUtil.parseFromNetwork(code!!, strs)
                            LogUtil.i(TAG, "share: $share")
                            val f = File(file_2021, code)
                            if(f.exists()) {
                                val stream = FileInputStream(f)
                                val reader = BufferedReader(InputStreamReader(stream, Charset.defaultCharset()))
                                val lines = reader.readLines()
                                val share_pre = SharesRecordActivity.ShareInfo(lines.last())
                                updateCurrentData(info, share, share_pre)
                            }
                        }
                    } else {
                        LogUtil.i(
                            TAG,
                            "exception value: $value, $code"
                        )
                    }
                } catch (e: Exception) {
                    LogUtil.i(
                        TAG,
                        "parseSharesInfo exception: ${e.message}, $code, $value"
                    )
                }
                ThreadPoolUtil.executeUI(Runnable {
                    mRefreshCount ++
                    tv_counting.text = "$mRefreshCount"
                    LogUtil.i(TAG, "mRefreshCount: $mRefreshCount")
                    if(mRefreshCount == mData.size) {
                        mAdapter?.setData(mData)
                        self_loading.visibility = View.GONE
                        btn_refresh.isEnabled = true
                        cb_network.isEnabled = true
                        tv_counting.visibility = View.GONE
                        resetSortUI()
                    }
                })
            }
        })
    }

    private fun resetSortUI() {
        mTodayStatus = 0
        mRangeStatus = 0
        iv_self_range.setImageResource(R.mipmap.sort_normal)
        iv_self_today.setImageResource(R.mipmap.sort_normal)
    }

    class MyPoolInfoAdater(val context: Context) : RecyclerView.Adapter<MyPoolHolder>() {

        private var mSharesInfoList: List<MineShareInfo> = ArrayList()


        fun setData(sharesInfoList: List<MineShareInfo>) {
            mSharesInfoList = sharesInfoList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPoolHolder {
            return MyPoolHolder(context,
                LayoutInflater.from(context).inflate(
                    R.layout.self_selection_item,
                    null
                )
            )
        }

        override fun getItemCount(): Int {
            return mSharesInfoList.size
        }

        override fun onBindViewHolder(holder: MyPoolHolder, position: Int) {
            holder.bindData(position, mSharesInfoList[position])
        }
    }

    class MyPoolHolder(context: Context, view: View) : RecyclerView.ViewHolder(view) {

        private var mIdTv: TextView? = null
        private var mNameTv: TextView? = null
        private var mCodeTv: TextView? = null
        private var mTimeTv: TextView? = null
        private var mRangeTv: TextView? = null
        private var mTodayTv: TextView? = null

        private var moreInfoTv: TextView? = null

        private var mineShareInfo: MineShareInfo? = null


        init {
            mIdTv = view.findViewById(R.id.self_item_id)
            mTimeTv = view.findViewById(R.id.self_item_time)
            mNameTv = view.findViewById(R.id.self_item_name)
            mCodeTv = view.findViewById(R.id.self_item_code)
            mRangeTv = view.findViewById(R.id.self_item_range)
            mTodayTv = view.findViewById(R.id.self_item_today)

            moreInfoTv = view.findViewById(R.id.tv_more_info)

            view.setOnClickListener {
                val intent = Intent(context, KLineActivity::class.java)
                intent.putExtra("code", mineShareInfo?.code)
                intent.putExtra("info", mineShareInfo.toString())
                context.startActivity(intent)
            }

            mCodeTv?.setOnClickListener {
                ClipboardUtil.clip(context, mineShareInfo?.code)
            }

        }

        fun bindData(position: Int, info: MineShareInfo) {
            this.mineShareInfo = info
            mIdTv?.text = position.toString()
            mTimeTv?.text = info.time?.substring(5)
            mNameTv?.text = info.name
            mCodeTv?.text = info.code

            mRangeTv?.text = baoLiuXiaoShu(info.totalRange)
            if(info.totalRange >= 0) {
                mRangeTv?.setTextColor(Color.parseColor("#f15b6c"))
            } else {
                mRangeTv?.setTextColor(Color.parseColor("#7fb80e"))
            }
            mTodayTv?.text = info.todayRange.toString()

            if(info.priority >= 3) {
                mTodayTv?.setTextColor(Color.parseColor("#ed1941"))
            } else if(info.priority == 2) {
                mTodayTv?.setTextColor(Color.parseColor("#59ed1941"))
            } else {
                mTodayTv?.setTextColor(Color.parseColor("#7fb80e"))
            }
            moreInfoTv?.text = info.moreInfo
        }
    }
}
