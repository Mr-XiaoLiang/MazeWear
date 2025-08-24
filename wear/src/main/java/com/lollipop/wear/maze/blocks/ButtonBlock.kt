package com.lollipop.wear.maze.blocks

import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.View
import androidx.core.animation.addListener
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.lollipop.play.core.helper.registerLog
import com.lollipop.wear.blocksbuilding.BlocksOwner
import com.lollipop.wear.blocksbuilding.dsl.layoutParams
import com.lollipop.wear.blocksbuilding.item.ItemSize
import com.lollipop.wear.maze.databinding.ItemBlocksBottomBinding

sealed class BottomBlockHolder(
    private val blocksOwner: BlocksOwner,
) {

    protected val binding =
        ItemBlocksBottomBinding.inflate(LayoutInflater.from(blocksOwner.context))

    val view: View = binding.root

    init {
        view.layoutParams(ItemSize.Match, ItemSize.Wrap)
    }

    protected fun setIcon(resId: Int) {
        if (resId == 0) {
            binding.iconView.isVisible = false
        } else {
            binding.iconView.isVisible = true
            binding.iconView.setImageResource(resId)
        }
    }

    protected fun setText(text: String) {
        binding.textView.text = text
    }

    class IconButton(
        blocksOwner: BlocksOwner,
        val iconRes: Int,
        val text: String,
        val onClick: () -> Unit
    ) : BottomBlockHolder(blocksOwner) {
        init {
            setIcon(iconRes)
            setText(text)
            binding.button.setOnClickListener {
                onClick()
            }
            binding.progressView.isVisible = false
        }
    }

    class DelayButton(
        blocksOwner: BlocksOwner,
        val delay: Long,
        val defIconRes: Int,
        val doneIconRes: Int,
        val defText: String,
        val pendingText: String,
        val doneText: String,
        val onClickDone: () -> Unit,
    ) : BottomBlockHolder(blocksOwner) {

        companion object {
            private const val PROGRESS_MAX = 360
        }

        private val controller = ProgressButtonController(
            delay = delay,
            onProgressUpdate = ::onProgressUpdate,
            onStateUpdate = ::onStateUpdate,
            onTimeEnd = onClickDone
        )

        init {
            binding.progressView.max = PROGRESS_MAX
            binding.progressView.progress = 0
            controller.reset()
        }

        private fun onProgressUpdate(progress: Float) {
            if (binding.progressView.isVisible) {
                binding.progressView.progress = (progress * PROGRESS_MAX).toInt()
            }
        }

        private fun onStateUpdate(state: ProgressButtonState) {
            binding.apply {
                when (state) {
                    ProgressButtonState.Idle -> {
                        progressView.isVisible = false
                        iconView.isVisible = true
                        textView.text = defText
                        setIcon(defIconRes)
                    }

                    ProgressButtonState.Pending -> {
                        progressView.isVisible = true
                        iconView.isVisible = false
                        textView.text = pendingText
                        onProgressUpdate(controller.progressState)
                    }

                    ProgressButtonState.Done -> {
                        progressView.isVisible = false
                        iconView.isVisible = true
                        textView.text = doneText
                        setIcon(doneIconRes)
                    }
                }
            }
        }

    }


    private class ProgressButtonController(
        delay: Long,
        val onProgressUpdate: (Float) -> Unit,
        val onStateUpdate: (ProgressButtonState) -> Unit,
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
        var progressState = MAX_VALUE
            private set
        var runningState = ProgressButtonState.Idle
            private set

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
            progressState = progress
            onProgressUpdate(progress)
        }

        private fun onStateChanged(state: ProgressButtonState) {
            runningState = state
            onStateUpdate(state)
        }

        private fun onProgressEnd() {
            val floatValue = progressState
            val state = runningState
            log("onProgressEnd: state = $state, progress = $floatValue")
            if (state == ProgressButtonState.Pending && isEnd(floatValue)) {
                onTimeEnd()
            }
            onStateChanged(ProgressButtonState.Done)
        }

        private fun start() {
            log("start: $runningState")
            valueAnimator.cancel()
            valueAnimator.start()
            onStateChanged(ProgressButtonState.Pending)
        }

        private fun cancel() {
            log("cancel: $runningState")
            valueAnimator.cancel()
            progressState = 0f
            onStateChanged(ProgressButtonState.Idle)
        }

        fun reset() {
            log("reset: $runningState")
            cancel()
        }

        fun toggle() {
            log("toggle: $runningState")
            when (this.runningState) {
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

}

fun BlocksOwner.bottom(
    iconRes: Int = 0,
    text: String,
    onClick: () -> Unit
): View {
    return BottomBlockHolder.IconButton(
        blocksOwner = this,
        iconRes = iconRes,
        text = text,
        onClick = onClick
    ).view
}

fun BlocksOwner.delayBottom(
    delay: Long = 2000L,
    defIconRes: Int = 0,
    doneIconRes: Int = 0,
    defText: String,
    pendingText: String,
    doneText: String,
    onClickDone: () -> Unit,
): View {
    return BottomBlockHolder.DelayButton(
        blocksOwner = this,
        delay = delay,
        defIconRes = defIconRes,
        doneIconRes = doneIconRes,
        defText = defText,
        pendingText = pendingText,
        doneText = doneText,
        onClickDone = onClickDone
    ).view
}
