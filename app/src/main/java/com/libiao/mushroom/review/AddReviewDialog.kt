package com.libiao.mushroom.review

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import com.libiao.mushroom.R

class AddReviewDialog(context: Context): Dialog(context, R.style.baseDialog) {

    init {
        setContentView(R.layout.add_review_dialog)
        val attr = window?.attributes
        attr?.width = context.resources?.displayMetrics?.widthPixels
        attr?.gravity = Gravity.BOTTOM
        window?.attributes = attr
    }
}