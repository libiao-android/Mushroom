package com.libiao.mushroom.thread

import android.os.Handler
import android.os.Looper
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit


class ThreadPoolManager private constructor() {

    private val mThreadPool = ThreadPoolExecutor(THREAD_COUNTS, THREAD_COUNTS, 10L, TimeUnit.MILLISECONDS, LinkedBlockingQueue<Runnable>())

    val mHandler = Handler(Looper.getMainLooper())

    fun execute(r: Runnable) {
        mThreadPool.execute(r)
    }
    companion object {
        //计算线程数量，cpu核数*2+1
        private val THREAD_COUNTS = threadsCount
        /**
         * 默认线程池大小
         */
        private val DEFAULT_THREAD_COUNT = 3
        /***
         * 最大的线程数大小
         */
        private val MAX_THREAD_COUNT = 12

        val instance = ThreadPoolManager()

        /**
         * 获取默认线程数量
         *
         * @return
         */
        private val threadsCount: Int
            get() {
                try {
                    val count = Runtime.getRuntime().availableProcessors() * 2 + 1
                    if (count <= 0) {
                        return DEFAULT_THREAD_COUNT
                    }
                    if (count > 10) {
                        return MAX_THREAD_COUNT
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                return DEFAULT_THREAD_COUNT
            }
    }

}
