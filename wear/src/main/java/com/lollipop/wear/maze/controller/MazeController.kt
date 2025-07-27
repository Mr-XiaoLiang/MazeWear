package com.lollipop.wear.maze.controller

import com.lollipop.maze.MazeMap
import com.lollipop.maze.data.MPath

class MazeController(
    private val callback: Callback
) {

    private var isDestroy = false

    private var currentMaze: MazeMap? = null

    private var currentPath: MPath? = null

    fun create(width: Int) {
        // TODO
    }

    interface Callback {

        fun onLoading()

        fun onMazeResult(maze: MazeMap, path: MPath)

    }

}