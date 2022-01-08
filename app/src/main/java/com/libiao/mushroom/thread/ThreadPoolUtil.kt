package com.libiao.mushroom.thread


object ThreadPoolUtil {


    private val HANDER_THREAD_CORE = ThreadPoolManager.instance

    /**
     * 磁盘IO操作
     * @param runnable the runnable
     * 执行线程Execute.
     */
    fun execute(runnable: Runnable) {
        HANDER_THREAD_CORE.execute(runnable)

    }

    fun execute(run: ()->Unit) {
        val runnable = Runnable {
            run()
        }
        HANDER_THREAD_CORE.execute(runnable)
    }

    fun executeUI(runnable: Runnable) {
        HANDER_THREAD_CORE.mHandler.post {
            runnable.run()
        }
    }

    fun executeUI(run: () -> Unit) {
        HANDER_THREAD_CORE.mHandler.post {
            run()
        }
    }
}
