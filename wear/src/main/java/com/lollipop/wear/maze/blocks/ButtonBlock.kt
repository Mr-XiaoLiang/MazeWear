package com.lollipop.wear.maze.blocks

import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.widget.ProgressBar
import androidx.core.animation.addListener
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.lollipop.play.core.helper.registerLog
import com.lollipop.wear.blocksbuilding.BuilderScope
import com.lollipop.wear.blocksbuilding.data.DataProvider
import com.lollipop.wear.blocksbuilding.data.mutableData
import com.lollipop.wear.blocksbuilding.data.staticData
import com.lollipop.wear.blocksbuilding.dsl.ViewLayoutParams
import com.lollipop.wear.blocksbuilding.dsl.layoutParams
import com.lollipop.wear.blocksbuilding.item.DP
import com.lollipop.wear.blocksbuilding.item.ItemSize
import com.lollipop.wear.blocksbuilding.item.SP
import com.lollipop.wear.blocksbuilding.item.ViewGravity
import com.lollipop.wear.blocksbuilding.view.Image
import com.lollipop.wear.blocksbuilding.view.ItemView
import com.lollipop.wear.blocksbuilding.view.Row
import com.lollipop.wear.blocksbuilding.view.Text
import com.lollipop.wear.blocksbuilding.view.TextStyle
import com.lollipop.wear.maze.R

enum class ProgressButtonState {
    Idle,
    Pending,
    Done
}

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
    private val progressStateImpl = mutableData(MAX_VALUE)
    private val runningStateImpl = mutableData(ProgressButtonState.Idle)

    val progressState: DataProvider<Float>
        get() = progressStateImpl
    val runningState: DataProvider<ProgressButtonState>
        get() = runningStateImpl

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
        progressStateImpl.value = progress
    }

    private fun onStateChanged(state: ProgressButtonState) {
        runningStateImpl.value = state
    }

    private fun onProgressEnd() {
        val floatValue = progressState.value
        val state = runningState.value
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
        progressStateImpl.value = 0f
        onStateChanged(ProgressButtonState.Idle)
    }

    fun reset() {
        log("reset: $runningState")
        cancel()
    }

    fun toggle() {
        val state = runningState.value
        log("toggle: $state")
        when (state) {
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

class ProgressButtonStyle(
    val defIconRes: Int = 0,
    val doneIconRes: Int = 0,
    val defLabel: String,
    val pendingLabel: String = defLabel,
    val doneLabel: String = defLabel,
) {

    fun bindState(state: DataProvider<ProgressButtonState>) {
        state.remember {
            iconState.value = when (it) {
                ProgressButtonState.Idle -> defIconRes
                ProgressButtonState.Pending -> 0
                ProgressButtonState.Done -> doneIconRes
            }
            labelState.value = when (it) {
                ProgressButtonState.Idle -> defLabel
                ProgressButtonState.Pending -> pendingLabel
                ProgressButtonState.Done -> doneLabel
            }
        }
    }

    val iconState = mutableData(defIconRes)
    val labelState = mutableData(defLabel)

}

private fun BuilderScope.BasicButton(
    iconRes: DataProvider<Int> = staticData(0),
    label: DataProvider<String>,
    buttonState: DataProvider<ProgressButtonState>,
    pendingProgress: DataProvider<Float>,
    onClick: () -> Unit,
) {
    val log = registerLog()
    ItemView {
        content.layoutParams(ItemSize.Match, ItemSize.Wrap)
        Row(
            layoutParams = ViewLayoutParams(ItemSize.Match, ItemSize.Wrap)
                .margin(horizontal = 12.DP, vertical = 8.DP)
        ) {
            onClick(onClick)
            background(R.drawable.block_bg_buttom)
            padding(horizontal = 16.DP, vertical = 12.DP)
            add(
                ProgressBar(context).apply {
                    buttonState.remember {
                        val show = it == ProgressButtonState.Pending
                        isVisible = show
                    }
                    isIndeterminate = false
                    max = 100
                    min = 0
                    pendingProgress.remember {
                        val value = (it * 100).toInt()
                        progress = value
                        log("Button.ProgressBar.progress = $value, isIndeterminate = $isIndeterminate")
                    }
                    indeterminateTintList = ColorStateList.valueOf(Color.WHITE)
                },
                layoutParams = ViewLayoutParams(16.DP)
                    .margin(end = 10.DP)
                    .gravity(ViewGravity.CenterVertical, ViewGravity.Start)
            )
            Image(
                layoutParams = ViewLayoutParams(16.DP)
                    .margin(end = 10.DP)
                    .gravity(ViewGravity.CenterVertical, ViewGravity.Start)
            ) {
                iconRes.remember {
                    src(it)
                    isVisible = it != 0
                }
                tint(Color.WHITE)
            }
            Text(
                layoutParams = ViewLayoutParams(ItemSize.Match, ItemSize.Wrap)
                    .gravity(ViewGravity.CenterVertical)
            ) {
                label.remember {
                    text = it
                }
                color(Color.WHITE)
                fontSize(14.SP)
                fontStyle(TextStyle.Bold)
            }
        }
    }
}

fun BuilderScope.Button(
    iconRes: DataProvider<Int> = staticData(0),
    label: DataProvider<String>,
    onClick: () -> Unit
) {
    BasicButton(
        iconRes = iconRes,
        label = label,
        onClick = onClick,
        buttonState = staticData(ProgressButtonState.Idle),
        pendingProgress = staticData(0F),
    )
}

fun BuilderScope.DelayButton(
    delay: Long = 2000L,
    style: ProgressButtonStyle,
    stateUpdate: (ProgressButtonState) -> Unit = {},
    onClickDone: () -> Unit,
) {
    val controller = ProgressButtonController(
        delay = delay,
        onTimeEnd = onClickDone
    )
    controller.bind(lifecycleOwner = blocksOwner.lifecycleOwner)
    controller.runningState.remember(stateUpdate)
    style.bindState(controller.runningState)
    BasicButton(
        iconRes = style.iconState,
        label = style.labelState,
        onClick = {
            controller.toggle()
        },
        buttonState = controller.runningState,
        pendingProgress = controller.progressState,
    )
}
