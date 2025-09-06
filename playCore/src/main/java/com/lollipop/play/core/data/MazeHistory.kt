package com.lollipop.play.core.data

import com.lollipop.maze.MazeMap
import com.lollipop.maze.data.MPath
import com.lollipop.maze.data.MTreasure
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MazeHistory(
    val id: Int,
    val name: String,
    val cacheFile: File,
    val lastTime: Long,
    val isComplete: Boolean,
    val treasure: MTreasure,
) {

    companion object {
        private val timeFormat by lazy {
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        }
    }

    val maze: MazeMap
        get() {
            return treasure.mazeMap
        }
    val path: MPath
        get() {
            return treasure.path
        }
    val hiPath: MPath?
        get() {
            return treasure.hiPath
        }

    val level: String by lazy {
        "${maze.width}x${maze.height}"
    }

    val pathLength: Int
        get() {
            return path.size
        }

    val hiPathLength: Int
        get() {
            return hiPath?.size ?: 0
        }

    val cachePath: String by lazy {
        cacheFile.path
    }

    val timeDisplay: String by lazy {
        timeFormat.format(Date(lastTime))
    }

}