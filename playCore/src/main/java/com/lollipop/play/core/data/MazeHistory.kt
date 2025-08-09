package com.lollipop.play.core.data

import com.lollipop.maze.MazeMap
import com.lollipop.maze.data.MPath
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MazeHistory(
    val id: Int,
    val cacheFile: File,
    val lastTime: Long,
    val isComplete: Boolean,
    val maze: MazeMap,
    val path: MPath,
) {

    companion object {
        private val timeFormat by lazy {
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        }
    }

    private var timeLong = 0L

    val level: String by lazy {
        "${maze.width}x${maze.height}}"
    }

    val pathLength: Int
        get() {
            return path.size
        }

    val cachePath: String by lazy {
        cacheFile.path
    }

    val timeDisplay: String by lazy {
        timeFormat.format(Date(timeLong))
    }

}