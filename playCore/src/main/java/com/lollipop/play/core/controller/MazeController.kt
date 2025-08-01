package com.lollipop.play.core.controller

import com.lollipop.maze.Maze
import com.lollipop.maze.MazeMap
import com.lollipop.maze.data.MBlock
import com.lollipop.maze.data.MPath
import com.lollipop.maze.data.MPoint
import com.lollipop.maze.helper.doAsync
import com.lollipop.play.core.helper.JoystickDirection

class MazeController(
    lifecycleHelper: LifecycleHelper,
    private val callback: Callback
) {

    private var isDestroy = false

    private var currentMaze: MazeMap? = null

    private var currentPath: MPath? = null

    private val focusBlock = MBlock()

    val focus: MPoint
        get() {
            return focusBlock
        }

    private val uiQueue = lifecycleHelper.newQueue(instantOnly = false)

    private fun postUI(task: Runnable) {
        uiQueue.post(task)
    }

    fun create(width: Int) {
        callback.onLoadingStart()
        doAsync {
            val mazeSize = if (width % 2 == 0) {
                width + 1
            } else {
                width
            }
            val mazeMap = Maze.generate(mazeSize)
            val path = MPath()
            onMazeChanged(mazeMap, path)
            postUI {
                callback.onLoadingEnd()
            }
        }
    }

    fun load(id: Int) {
        // TODO
        // onMazeChanged()
    }

    private fun onMazeChanged(newMaze: MazeMap, newPath: MPath) {
        currentMaze = newMaze
        currentPath = newPath
        if (newPath.isEmpty()) {
            newPath.add(newMaze.start)
        }
        val lastPoint = newPath.last() ?: newMaze.start
        focusBlock.set(lastPoint)
        if (!isDestroy) {
            postUI {
                callback.onMazeResult(newMaze, newPath, focusBlock)
            }
        }
    }

    fun manipulate(direction: JoystickDirection) {
        val maze = currentMaze ?: return
        val path = currentPath ?: return

    }

    fun destroy() {
        isDestroy = true
    }

    interface Callback {

        fun onLoadingStart()

        fun onLoadingEnd()

        fun onMazeResult(maze: MazeMap, path: MPath, focus: MBlock)

    }

}