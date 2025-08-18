package com.lollipop.wear.maze.theme

import android.graphics.Color
import com.lollipop.play.core.helper.dp2px
import com.lollipop.play.core.view.MazeOverviewView

object MazeMapTheme {

    fun updateMaze(view: MazeOverviewView) {
        view.setColor(
            lineColor = Color.WHITE,
            extremeStartColor = Color.RED,
            extremeEndColor = Color.GREEN,
            mapColor = Color.GRAY
        )
        view.setMin(1F.dp2px(view.context), 3F.dp2px(view.context))
    }

}