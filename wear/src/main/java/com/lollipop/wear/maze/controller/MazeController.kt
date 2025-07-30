package com.lollipop.wear.maze.controller

import com.lollipop.maze.Maze
import com.lollipop.maze.MazeMap
import com.lollipop.maze.data.MPath
import com.lollipop.maze.helper.doAsync
import com.lollipop.maze.helper.onUI

class MazeController(
    private val callback: Callback
) {

    private var isDestroy = false

    private var currentMaze: MazeMap? = null

    private var currentPath: MPath? = null

    fun create(width: Int) {
        callback.onLoading()
        doAsync {
            val mazeSize = if (width % 2 == 0) {
                width + 1
            } else {
                width
            }
            val mazeMap = Maze.generate(mazeSize)
            val path = MPath()
            currentPath = path
            currentMaze = mazeMap
            if (!isDestroy) {
                onUI {
                    callback.onMazeResult(mazeMap, path)
                }
            }
        }
    }

    fun load(id: Int) {
        // TODO
    }

    fun destroy() {
        isDestroy = true
    }

    interface Callback {

        fun onLoading()

        fun onMazeResult(maze: MazeMap, path: MPath)

    }

}