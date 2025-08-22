package com.lollipop.wear.maze.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.lollipop.maze.MazeMap
import com.lollipop.maze.data.MMap
import com.lollipop.maze.data.MPath
import com.lollipop.maze.data.MPoint
import com.lollipop.play.core.view.MazeOverviewView

@Composable
fun MazeOverview(
    modifier: Modifier,
    state: MazeOverviewState
) {
    val updateMode by remember { state.updateMode }
    val map by remember { state.mapState }
    val path by remember { state.pathState }
    AndroidView(
        modifier = modifier,
        factory = { context ->
            MazeOverviewView(context)
        },
        update = { view ->
            with(updateMode) {
                view.setMap(map, path)
            }
        },
    )

}

class MazeOverviewState() {

    companion object {
        val EMPTY_MAP = MazeMap(MPoint(0, 0), MPoint(0, 0), MMap(0, 0))
        val EMPTY_PATH = MPath()
    }

    val mapState = mutableStateOf(EMPTY_MAP)
    val pathState = mutableStateOf(EMPTY_PATH)

    val updateMode = mutableIntStateOf(0)

    fun contentUpdate() {
        updateMode.intValue = updateMode.intValue + 1
    }

    fun update(map: MazeMap) {
        mapState.value = map
    }

    fun update(path: MPath) {
        pathState.value = path
    }

}
