package com.lollipop.wear.maze.base

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.CurvedTextStyle
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
import androidx.wear.compose.material3.TimeText
import androidx.wear.compose.material3.TimeTextDefaults
import androidx.wear.compose.material3.lazy.TransformationSpec
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import androidx.wear.compose.material3.timeTextCurvedText
import com.google.android.horologist.compose.layout.ColumnItemType
import com.google.android.horologist.compose.layout.rememberResponsiveColumnPadding
import com.lollipop.play.core.helper.DeviceHelper
import com.lollipop.play.core.helper.registerLog
import com.lollipop.wear.maze.composable.ProgressButtonController
import com.lollipop.wear.maze.composable.ProgressButtonState

abstract class WearComponentActivity : ComponentActivity() {

    protected val isScreenRound by lazy {
        DeviceHelper.isScreenRound(this)
    }

    protected val log = registerLog()

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
        log("ListScaffold")
        MaterialTheme {
            log("MaterialTheme.Content")
            ScreenScaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                scrollState = columnState,
                contentPadding = contentPadding,
//                timeText = {
//                    log("TimeText")
//                    TimeText { time ->
//                        log("TimeText.Content $time")
//                        timeTextCurvedText(
//                            time = time,
//                            style = CurvedTextStyle(
//                                color = Color.White
//                            )
//                        )
//                    }
//                }
            ) { contentPadding ->
                log("ScreenScaffold.Content")
                TransformingLazyColumn(
                    state = columnState,
                    contentPadding = contentPadding,
                ) {
                    content(transformationSpec)
                }
                TimeText(
                    timeSource = TimeTextDefaults.rememberTimeSource("HH:mm")
                ) { time ->
                    log("TimeText.Content $time")
                    timeTextCurvedText(
                        time = time,
                        style = CurvedTextStyle(
                            color = Color.White,
                            fontSize = 12.sp,
                        )
                    )
                }
            }
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

    @Composable
    abstract fun Content()

}
