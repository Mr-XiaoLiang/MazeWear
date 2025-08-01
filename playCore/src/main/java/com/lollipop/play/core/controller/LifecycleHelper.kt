package com.lollipop.play.core.controller

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.Runnable
import java.util.LinkedList

sealed class LifecycleHelper {

    companion object {

        fun auto(owner: LifecycleOwner): LifecycleHelper {
            return Auto().apply {
                bind(owner.lifecycle)
            }
        }

        fun manual(): Manual {
            return Manual()
        }
    }

    private val handler = MessageHandler(Handler(Looper.getMainLooper()))

    private val mainQueue by lazy {
        newQueue()
    }
    private val queueList = LinkedList<Queue>()

    var isActive = false
        private set

    protected fun onResume() {
        updateState(true)
    }

    protected fun onPause() {
        updateState(false)
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

    private fun updateState(isActive: Boolean) {
        this.isActive = isActive
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

    }

    class Auto : LifecycleHelper() {

        private val lifecycleEventObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    onResume()
                }

                Lifecycle.Event.ON_PAUSE -> {
                    onPause()
                }

                else -> {}
            }
        }

        fun bind(lifecycle: Lifecycle) {
            lifecycle.addObserver(lifecycleEventObserver)
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                onResume()
            }
        }

    }

    class Manual : LifecycleHelper() {

        fun resume() {
            onResume()
        }

        fun pause() {
            onPause()
        }

    }

    internal class MessageHandler(private val uiHandler: Handler) {
        fun run(task: Runnable) {
            uiHandler.post(task)
        }
    }

}