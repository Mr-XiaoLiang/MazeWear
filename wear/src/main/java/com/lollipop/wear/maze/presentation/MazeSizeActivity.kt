package com.lollipop.wear.maze.presentation

import com.lollipop.play.core.data.PreferencesHelper
import com.lollipop.play.core.data.mazeSettings
import com.lollipop.play.core.page.AdjustNumberActivity
import com.lollipop.wear.maze.R


class MazeSizeActivity : AdjustNumberActivity() {

    override val maxNumber: Int = PreferencesHelper.MAZE_WIDTH_MAX
    override val minNumber: Int = PreferencesHelper.MAZE_WIDTH_MIN
    override val offsetStep: Int = 2

    val settings by mazeSettings()

    override fun hintText(): String {
        return getString(R.string.hint_maze_width)
    }

    override fun onNumberChanged(number: Int, isFromUser: Boolean) {
        if (isFromUser) {
            settings.mazeWidth = number
        }
    }

    override fun getDefaultNumber(): Int {
        return settings.mazeWidth
    }

}