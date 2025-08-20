package com.lollipop.wear.maze.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.foundation.CurvedTextStyle
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnScope
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TimeText
import androidx.wear.compose.material3.lazy.TransformationSpec
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import androidx.wear.compose.material3.timeTextCurvedText
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
                    .transformedHeight(this, transformationSpec),
                transformation = SurfaceTransformation(transformationSpec),
                onClick = onClick,
                icon = icon,
                label = label
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
