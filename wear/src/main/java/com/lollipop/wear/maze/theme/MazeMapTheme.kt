package com.lollipop.wear.maze.theme

import android.graphics.Color
import com.lollipop.play.core.view.MazeOverviewView

object MazeMapTheme {

    fun updateMaze(view: MazeOverviewView) {
        view.setColor(
            lineColor = Color.WHITE,
            extremeStartColor = Color.RED,
            extremeEndColor = Color.GREEN,
            mapColor = Color.GRAY
        )
    }

}