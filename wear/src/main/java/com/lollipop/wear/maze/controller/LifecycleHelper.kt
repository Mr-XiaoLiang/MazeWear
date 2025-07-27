package com.lollipop.wear.maze.controller

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

    private val pendingTask = LinkedList<Runnable>()

    private var isActive = false

    protected fun onResume() {
        isActive = true
        while (pendingTask.isNotEmpty()) {
            pendingTask.removeFirst().run()
        }
    }

    protected fun onPause() {
        isActive = false
    }

    fun post(task: Runnable) {
        if (isActive) {
            task.run()
        } else {
            pendingTask.add(task)
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

}