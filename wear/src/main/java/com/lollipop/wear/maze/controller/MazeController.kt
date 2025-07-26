package com.lollipop.wear.maze.controller

import com.lollipop.maze.MazeMap

class MazeController(
    private val callback: Callback
) {

    private var isDestroy = false

    private var currentMaze: MazeMap? = null

    fun create(width: Int) {
        // TODO
    }

    interface Callback {

        fun onCreating()

        fun onMazeResult(maze: MazeMap)

    }

}