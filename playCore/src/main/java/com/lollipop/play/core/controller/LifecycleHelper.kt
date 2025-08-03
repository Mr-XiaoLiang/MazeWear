package com.lollipop.play.core.controller

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.Runnable
import java.util.LinkedList

sealed class LifecycleHelper : BasicLifecycleDelegate() {

    companion object {

        fun auto(owner: LifecycleOwner): AutoMode {
            return AutoMode(owner)
        }

        fun manual(): ManualMode {
            return ManualMode()
        }
    }

    private val handler = MessageHandler(Handler(Looper.getMainLooper()))

    private val mainQueue by lazy {
        newQueue()
    }
    private val queueList = LinkedList<Queue>()

    override fun onResumeInvoke() {
        handler.onResume()
    }

    override fun onPauseInvoke() {
    }

    fun post(task: Runnable) {
        mainQueue.post(task)
    }

    fun newQueue(instantOnly: Boolean = false): Queue {
        val queue = Queue(handler)
        queueList.add(queue)
        queue.instantOnly = instantOnly
        return queue
    }

    override fun onStateChanged(isActive: Boolean) {
        queueList.forEach { it.updateState(isActive) }
    }

    class Queue internal constructor(private val handler: MessageHandler) {
        private val pendingTask = LinkedList<Runnable>()

        private var isActive = false

        var instantOnly = false

        internal fun updateState(isActive: Boolean) {
            this.isActive = isActive
            if (isActive) {
                while (pendingTask.isNotEmpty()) {
                    pendingTask.removeFirstOrNull()?.let {
                        handler.run(it)
                    }
                }
            }
        }

        fun post(task: Runnable, instant: Boolean = instantOnly) {
            if (isActive) {
                handler.run(task)
            } else if (!instant) {
                pendingTask.add(task)
            }
        }

        fun onUI(task: Runnable, instant: Boolean = instantOnly) {
            if (handler.isUiThread()) {
                task.run()
            } else {
                post(task, instant)
            }
        }

    }

    internal class MessageHandler(
        private val uiHandler: Handler
    ) {

        var uiThread: Thread? = null
            private set

        fun onResume() {
            uiThread = Thread.currentThread()
        }

        fun isUiThread(): Boolean {
            return uiThread === Thread.currentThread()
        }

        fun run(task: Runnable) {
            uiHandler.post(task)
        }
    }

    class AutoMode(source: LifecycleOwner) : LifecycleHelper() {

        val controller: LifecycleController = controllerByAuto(source)

    }

    class ManualMode : LifecycleHelper() {

        val controller: LifecycleController = controllerByManual()

    }


}