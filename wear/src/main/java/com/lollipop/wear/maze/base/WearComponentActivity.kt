package com.lollipop.wear.maze.base

import android.animation.ValueAnimator
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.animation.addListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnScope
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.CircularProgressIndicator
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ProgressIndicatorDefaults
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.TransformationSpec
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import com.google.android.horologist.compose.layout.ColumnItemType
import com.google.android.horologist.compose.layout.rememberResponsiveColumnPadding

abstract class WearComponentActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Content()
        }
    }

    @Composable
    protected fun ListScaffold(
        first: ColumnItemType = ColumnItemType.ListHeader,
        last: ColumnItemType = ColumnItemType.Button,
        content: TransformingLazyColumnScope.(TransformationSpec) -> Unit
    ) {
        val columnState = rememberTransformingLazyColumnState()
        val contentPadding = rememberResponsiveColumnPadding(first = first, last = last)
        val transformationSpec = rememberTransformationSpec()

        MaterialTheme {
            ScreenScaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                scrollState = columnState,
                contentPadding = contentPadding,
            ) { contentPadding ->
                TransformingLazyColumn(
                    state = columnState,
                    contentPadding = contentPadding,
                ) {
                    content(transformationSpec)
                }
            }
//            TimeText(
//                modifier = Modifier.fillMaxSize(),
//            ) { time ->
//                timeTextCurvedText(
//                    time = time,
//                    style = CurvedTextStyle(
//                        color = Color.White
//                    )
//                )
//            }
        }
    }

    protected fun TransformingLazyColumnScope.ListTitle(
        title: String,
        transformationSpec: TransformationSpec
    ) {
        item {
            ListHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .transformedHeight(this, transformationSpec),
                transformation = SurfaceTransformation(transformationSpec)
            ) {
                Text(text = title)
            }
        }
    }

    protected fun TransformingLazyColumnScope.ListButton(
        transformationSpec: TransformationSpec,
        onClick: () -> Unit,
        icon: @Composable BoxScope.() -> Unit,
        label: @Composable RowScope.() -> Unit
    ) {
        item {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .transformedHeight(this, transformationSpec),
                transformation = SurfaceTransformation(transformationSpec),
                onClick = onClick,
                icon = icon,
                label = label
            )
        }
    }

    @Composable
    protected fun ProgressButtonPendingIndicator(
        progress: () -> Float,
        size: Dp = 20.dp,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(size),
            progress = progress,
            colors = ProgressIndicatorDefaults.colors().copy(
                trackColor = Color.Transparent,
                indicatorColor = MaterialTheme.colorScheme.onSecondary,
            ),
            allowProgressOverflow = true,
            strokeWidth = 2.dp,
            gapSize = 0.dp
        )
    }

    protected fun TransformingLazyColumnScope.ProgressButton(
        transformationSpec: TransformationSpec,
        controller: ProgressButtonController,
        icon: @Composable BoxScope.(ProgressButtonState) -> Unit,
        label: @Composable RowScope.(ProgressButtonState) -> Unit,
    ) {
        item {
            val state by remember { controller.runningState }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .transformedHeight(this, transformationSpec)
                    .animateContentSize(),
                transformation = SurfaceTransformation(transformationSpec),
                onClick = {
                    controller.toggle()
                },
                icon = {
                    icon(state)
                },
                label = {
                    label(state)
                }
            )
        }
    }

    protected class ProgressButtonController(
        delay: Long,
        val onTimeEnd: () -> Unit
    ) {

        private val valueAnimator = ValueAnimator.ofFloat(1F, 0F)
        val progressState = mutableFloatStateOf(0F)
        val runningState = mutableStateOf(ProgressButtonState.Idle)

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
            onTimeEnd()
            runningState.value = ProgressButtonState.Done
        }

        private fun start() {
            valueAnimator.cancel()
            valueAnimator.start()
            runningState.value = ProgressButtonState.Pending
        }

        private fun cancel() {
            valueAnimator.cancel()
            progressState.floatValue = 0f
            runningState.value = ProgressButtonState.Idle
        }

        fun reset() {
            cancel()
        }

        fun toggle() {
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

    protected enum class ProgressButtonState {
        Idle,
        Pending,
        Done
    }

    protected fun TransformingLazyColumnScope.ListSpacer(
        transformationSpec: TransformationSpec,
        height: Dp
    ) {
        item {
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .transformedHeight(this, transformationSpec)
                    .height(height)
            )
        }
    }

    protected fun TransformingLazyColumnScope.ListLongClickButton(
        keepTime: Long,
        transformationSpec: TransformationSpec,
        onClick: () -> Unit,
        icon: @Composable BoxScope.() -> Unit,
        label: @Composable RowScope.() -> Unit
    ) {
        item {
            Button(
                modifier = Modifier
                    .transformedHeight(this, transformationSpec),
                transformation = SurfaceTransformation(transformationSpec),
                onClick = onClick,
                icon = icon,
                label = label
            )
        }
    }

    protected fun TransformingLazyColumnScope.SplitButton(
        transformationSpec: TransformationSpec
    ) {
//        item {
//            SplitButtonLayout(
//                leadingButton = {
//                    SplitButtonDefaults.LeadingButton(onClick = { /* Do Nothing */ }) {
//                        Icon(
//                            Icons.Filled.Edit,
//                            modifier = Modifier.size(SplitButtonDefaults.LeadingIconSize),
//                            contentDescription = "Localized description",
//                        )
//                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
//                        Text("My Button")
//                    }
//                },
//                trailingButton = {
//                    SplitButtonDefaults.TrailingButton(
//                        checked = checked,
//                        onCheckedChange = { checked = it },
//                        modifier =
//                            Modifier.semantics {
//                                stateDescription = if (checked) "Expanded" else "Collapsed"
//                                contentDescription = "Toggle Button"
//                            },
//                    ) {
//                        val rotation: Float by
//                        animateFloatAsState(
//                            targetValue = if (checked) 180f else 0f,
//                            label = "Trailing Icon Rotation",
//                        )
//                        Icon(
//                            Icons.Filled.KeyboardArrowDown,
//                            modifier =
//                                Modifier.size(SplitButtonDefaults.TrailingIconSize).graphicsLayer {
//                                    this.rotationZ = rotation
//                                },
//                            contentDescription = "Localized description",
//                        )
//                    }
//                },
//            )
//        }
    }

    @Composable
    abstract fun Content()

}
