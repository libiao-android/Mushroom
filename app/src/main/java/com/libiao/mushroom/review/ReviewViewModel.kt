package com.libiao.mushroom.review

import com.airbnb.mvrx.MavericksViewModel
import com.libiao.mushroom.room.review.ReviewInfoDatabase
import com.libiao.mushroom.utils.LogUtil

class ReviewViewModel(initial: ReviewState): MavericksViewModel<ReviewState>(initial) {

    companion object {
        private const val TAG = "ReviewViewModel"
    }


    fun fetchInfo(month: Int) {
        withState {
            val data = ReviewInfoDatabase.getInstance()?.getReviewDao()?.getShares()
            LogUtil.i(TAG, "fetchInfo: ${data?.size}")
        }
    }


    private fun isFit(month: Int, time: String): Boolean {
        when(month) {
            1 -> {
                return time.startsWith("2022-1") || time.startsWith("2022-01")
            }
            2 -> {
                return time.startsWith("2022-2") || time.startsWith("2022-02")
            }
            3 -> {
                return time.startsWith("2022-3") || time.startsWith("2022-03")
            }
            4 -> {
                return time.startsWith("2022-4") || time.startsWith("2022-04")
            }
            5 -> {
                return time.startsWith("2022-5") || time.startsWith("2022-05")
            }
            6 -> {
                return time.startsWith("2023-6") || time.startsWith("2023-06")
            }
            7 -> {
                return time.startsWith("2022-7") || time.startsWith("2022-07")
            }
            8 -> {
                return time.startsWith("2022-8") || time.startsWith("2022-08")
            }
            9 -> {
                return time.startsWith("2022-9") || time.startsWith("2022-09")
            }
            10 -> {
                return time.startsWith("2022-10")
            }
            11 -> {
                return time.startsWith("2022-11")
            }
            12 -> {
                return time.startsWith("2022-12")
            }
        }
        return false
    }
}