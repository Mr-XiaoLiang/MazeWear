package com.lollipop.wear.maze

import android.content.Context
import android.os.Bundle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnScope
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.lazy.TransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import com.lollipop.play.core.data.DataManager
import com.lollipop.play.core.data.MazeHistory
import com.lollipop.wear.maze.base.MazeActivityHelper
import com.lollipop.wear.maze.base.WearComponentActivity


class MazeInfoActivity : WearComponentActivity() {

    companion object {
        fun start(context: Context, mazeCache: String) {
            MazeActivityHelper.startWithMaze<MazeInfoActivity>(context, mazeCache)
        }
    }

    private val mazeNameState = mutableStateOf("")
    private val mazeSizeState = mutableStateOf("")
    private val mazeTimeState = mutableStateOf("")
    private val mazeStepsState = mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
    }

    private fun initData() {
        val cache = MazeActivityHelper.findFromIntent(intent)
        DataManager.findByFile(cache)?.let {
            onMazeLoaded(it)
        }
    }

    private fun onMazeLoaded(mazeInfo: MazeHistory) {
        mazeNameState.value = mazeInfo.name.ifEmpty { "???" }
        mazeSizeState.value = mazeInfo.level
        mazeTimeState.value = mazeInfo.timeDisplay
        mazeStepsState.value = mazeInfo.pathLength.toString()
    }

    private fun onDeleteClick() {
        // TODO
    }

    private fun onOpenClick() {
        // TODO
    }

    @Composable
    override fun Content() {
        val mazeName by remember { mazeNameState }
        val mazeSize by remember { mazeSizeState }
        val mazeSteps by remember { mazeStepsState }
        val mazeTime by remember { mazeTimeState }
        ListScaffold { transformationSpec ->
            ListTitle(mazeName, transformationSpec)
            MazeItem(
                iconId = R.drawable.baseline_resize_24,
                label = mazeSize,
                transformationSpec = transformationSpec
            )
            MazeItem(
                iconId = R.drawable.baseline_footprint_24,
                label = mazeSteps,
                transformationSpec = transformationSpec
            )
            MazeItem(
                iconId = R.drawable.baseline_nest_clock_farsight_analog_24,
                label = mazeTime,
                transformationSpec = transformationSpec
            )
            ListButton(
                transformationSpec = transformationSpec,
                onClick = { onOpenClick() },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.VideogameAsset,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                },
                label = {
                    Text(
                        text = stringResource(R.string.label_open_maze),
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
            ListButton(
                transformationSpec = transformationSpec,
                onClick = { onDeleteClick() },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.VideogameAsset,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                },
                label = {
                    Text(
                        text = stringResource(R.string.label_open_maze),
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    }

    private fun TransformingLazyColumnScope.MazeItem(
        iconId: Int,
        label: String,
        transformationSpec: TransformationSpec,
    ) {
        MazeItem(
            icon = { painterResource(iconId) },
            label = label,
            transformationSpec = transformationSpec
        )
    }

    private fun TransformingLazyColumnScope.MazeItem(
        icon: @Composable () -> Painter,
        label: String,
        transformationSpec: TransformationSpec,
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .transformedHeight(this, transformationSpec),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    painter = icon(),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }
    }

}