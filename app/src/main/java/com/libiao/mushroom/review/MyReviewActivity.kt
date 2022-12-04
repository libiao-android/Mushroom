package com.libiao.mushroom.review

import android.os.Bundle
import com.airbnb.epoxy.TypedEpoxyController
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.UniqueOnly
import com.airbnb.mvrx.viewModel
import com.libiao.mushroom.R
import com.libiao.mushroom.base.BaseActivity
import com.libiao.mushroom.utils.LogUtil
import kotlinx.android.synthetic.main.my_review_activity.*
import java.util.*

class MyReviewActivity: BaseActivity(), MavericksView {
    companion object {
        const val TAG = "MyReviewActivity"
    }

    private val controller = ReviewController()
    private val reviewViewModel: ReviewViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_review_activity)
        initView()
    }

    private fun initView() {
        epoxy_view_review.setController(controller)
        reviewViewModel.onEach(deliveryMode = UniqueOnly(UUID.randomUUID().toString())) {
            controller.setData(it)
        }


        iv_add_review.setOnClickListener {
            val dialog = AddReviewDialog(this)
            dialog.show()
        }
    }

    override fun invalidate() {

    }


    inner class ReviewController: TypedEpoxyController<ReviewState>() {
        override fun buildModels(data: ReviewState?) {
            LogUtil.i(TAG, "buildModels: ${data?.infoList?.size}")
        }
    }

}