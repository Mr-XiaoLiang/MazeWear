package com.lollipop.play.core.controller

import com.lollipop.maze.Maze
import com.lollipop.maze.MazeMap
import com.lollipop.maze.data.APoint
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

    private val previousBlock = MBlock()

    private val signpost = Signpost()

    val focus: MPoint
        get() {
            return focusBlock.snapshot()
        }

    val previous: MPoint
        get() {
            return previousBlock.snapshot()
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
            newPath.put(newMaze.start)
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
        signpost.fetch(maze, focusBlock)
        if (signpost.isDirectionEnable(direction)) {
            val next = signpost.getPoint(direction)
            if (next.isSame(previousBlock)) {
                // 如果下一个等于上一个，那么等于做了回退。需要执行回退逻辑
                path.back()
                // 回退之后如果是空的，那么又加回来
                if (path.isEmpty()) {
                    path.put(maze.start)
                }
                // 当前的焦点，是最后一个
                focusBlock.set(path.last() ?: maze.start)
                previousBlock.set(path.secondLast() ?: focus)
            } else {
                // 否则就是前进
                previousBlock.set(focus)
                focusBlock.set(next)
                path.put(next)
            }
            postUI {
                callback.onPointChange(previousBlock.snapshot(), focusBlock.snapshot())
            }
        }
    }

    fun destroy() {
        isDestroy = true
    }

    interface Callback {

        fun onLoadingStart()

        fun onLoadingEnd()

        fun onMazeResult(maze: MazeMap, path: MPath, focus: MBlock)

        fun onPointChange(fromPoint: MPoint, toPoint: MPoint)

    }

    private class Signpost {

        var left = false
            private set
        var right = false
            private set
        var up = false
            private set
        var down = false
            private set

        private val current = MBlock()

        fun fetch(map: MazeMap, point: APoint) {
            current.set(point)
            left = map[point.x - 1, point.y] == Maze.ROAD
            right = map[point.x + 1, point.y] == Maze.ROAD
            up = map[point.x, point.y - 1] == Maze.ROAD
            down = map[point.x, point.y + 1] == Maze.ROAD
        }

        fun isDirectionEnable(direction: JoystickDirection): Boolean {
            return when (direction) {
                JoystickDirection.LEFT -> left
                JoystickDirection.UP -> up
                JoystickDirection.RIGHT -> right
                JoystickDirection.DOWN -> down
            }
        }

        fun getPoint(direction: JoystickDirection): MPoint {
            return when (direction) {
                JoystickDirection.LEFT -> current.leftPoint()
                JoystickDirection.UP -> current.upPoint()
                JoystickDirection.RIGHT -> current.rightPoint()
                JoystickDirection.DOWN -> current.downPoint()
            }
        }

    }

}