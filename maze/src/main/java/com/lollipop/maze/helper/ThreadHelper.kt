package com.lollipop.maze.helper

import java.util.concurrent.Executors

object ThreadHelper {
    private val threadPool = Executors.newCachedThreadPool()
    private val mainHandler = android.os.Handler(android.os.Looper.getMainLooper())

    fun postThreadPool(runnable: Runnable) {
        threadPool.execute(runnable)
    }

    fun postMain(runnable: Runnable) {
        mainHandler.post(runnable)
    }

    class SafedRunnable(
        val onError: (Throwable) -> Unit = { it.printStackTrace() },
        val runnable: Runnable
    ) : Runnable {
        override fun run() {
            try {
                runnable.run()
            } catch (e: Throwable) {
                onError(e)
            }
        }
    }

}

inline fun <reified T> T.doAsync(
    noinline onError: (Throwable) -> Unit = { it.printStackTrace() },
    crossinline block: T.() -> Unit
) {
    ThreadHelper.postThreadPool(
        ThreadHelper.SafedRunnable(onError) {
            block()
        }
    )
}

inline fun <reified T> T.onUI(
    noinline onError: (Throwable) -> Unit = { it.printStackTrace() },
    crossinline block: T.() -> Unit
) {
    ThreadHelper.postMain(
        ThreadHelper.SafedRunnable(onError) {
            block()
        }
    )
}
