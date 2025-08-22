package com.lollipop.wear.maze.composable

import android.animation.ValueAnimator
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.animation.addListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.lollipop.play.core.helper.registerLog
import com.lollipop.wear.maze.base.WearComponentActivity

class ProgressButtonController(
    delay: Long,
    val onTimeEnd: () -> Unit
) {

    companion object {
        private const val MAX_VALUE = 1F
        private const val MIN_VALUE = 0F

        private const val MIN_THRESHOLD = 0.001F

        private fun isEnd(value: Float): Boolean {
            if (value <= MIN_THRESHOLD) {
                return true
            }
            return false
        }
    }

    private val valueAnimator = ValueAnimator.ofFloat(MAX_VALUE, MIN_VALUE)
    val progressState = mutableFloatStateOf(0F)
    val runningState = mutableStateOf(ProgressButtonState.Idle)

    private val log = registerLog()

    private val lifecycleObserver = object : LifecycleEventObserver {
        override fun onStateChanged(
            source: LifecycleOwner,
            event: Lifecycle.Event
        ) {
            when (event) {
                Lifecycle.Event.ON_DESTROY -> {
                    reset()
                }

                else -> {}
            }
        }
    }

    init {
        valueAnimator.duration = delay
        valueAnimator.addUpdateListener {
            val value = it.animatedValue
            if (value is Float) {
                onProgressChanged(value)
            }
        }
        valueAnimator.addListener(
            onEnd = {
                onProgressEnd()
            }
        )
    }

    fun bind(lifecycleOwner: LifecycleOwner): ProgressButtonController {
        lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        return this
    }

    private fun onProgressChanged(progress: Float) {
        progressState.floatValue = progress
    }

    private fun onProgressEnd() {
        val floatValue = progressState.floatValue
        val state = runningState.value
        log("onProgressEnd: state = $state, progress = $floatValue")
        if (state == ProgressButtonState.Pending && isEnd(floatValue)) {
            onTimeEnd()
        }
        runningState.value = ProgressButtonState.Done
    }

    private fun start() {
        log("start: ${runningState.value}")
        valueAnimator.cancel()
        valueAnimator.start()
        runningState.value = ProgressButtonState.Pending
    }

    private fun cancel() {
        log("cancel: ${runningState.value}")
        valueAnimator.cancel()
        progressState.floatValue = 0f
        runningState.value = ProgressButtonState.Idle
    }

    fun reset() {
        log("reset: ${runningState.value}")
        cancel()
    }

    fun toggle() {
        log("toggle: ${runningState.value}")
        when (runningState.value) {
            ProgressButtonState.Idle -> {
                start()
            }

            ProgressButtonState.Pending -> {
                cancel()
            }

            ProgressButtonState.Done -> {
                reset()
            }
        }
    }

}

enum class ProgressButtonState {
    Idle,
    Pending,
    Done
}