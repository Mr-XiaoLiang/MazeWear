package com.lollipop.wear.maze

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.lollipop.wear.maze.base.MazeActivityHelper
import com.lollipop.wear.maze.base.WearComponentActivity


class MazeDeleteActivity : WearComponentActivity() {

    companion object {
        fun start(context: Context, mazeCache: String) {
            MazeActivityHelper.startWithMaze<MazeDeleteActivity>(context, mazeCache)
        }
    }

    @Composable
    override fun Content() {
        ListScaffold { transformationSpec ->
            ListTitle("删除迷宫", transformationSpec)
            items(10) {
                Text("Item - $it", modifier = Modifier.fillMaxWidth())
            }
            ListButton(
                transformationSpec = transformationSpec,
                icon = {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                },
                onClick = {},
                label = {
                    Text("删除")
                }
            )
        }
    }
}