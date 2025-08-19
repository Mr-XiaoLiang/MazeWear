package com.lollipop.wear.maze.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnScope
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ListHeader
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
        ScreenScaffold(
            scrollState = columnState,
            contentPadding = contentPadding
        ) { contentPadding ->
            TransformingLazyColumn(
                state = columnState,
                contentPadding = contentPadding,
            ) {
                content(transformationSpec)
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
        icon: @Composable BoxScope.() -> Unit,
        onClick: () -> Unit,
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
    abstract fun Content()

}
