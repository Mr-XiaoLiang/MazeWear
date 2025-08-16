package com.lollipop.play.core.controller

import com.lollipop.maze.Maze
import com.lollipop.maze.MazeMap
import com.lollipop.maze.data.APoint
import com.lollipop.maze.data.MBlock
import com.lollipop.maze.data.MPath
import com.lollipop.maze.data.MPoint
import com.lollipop.maze.helper.doAsync
import com.lollipop.play.core.data.DataManager
import com.lollipop.play.core.data.MazeHistory
import com.lollipop.play.core.helper.JoystickDirection
import com.lollipop.play.core.helper.registerLog
import com.lollipop.play.core.helper.tagName

class MazeController(
    lifecycleHelper: LifecycleHelper,
    private val callback: Callback
) {

    private var isDestroy = false

    var currentMaze: MazeMap? = null
        private set

    var currentPath: MPath = MPath()
        private set

    private val focusBlock = MBlock()

    private val previousBlock = MBlock()

    private val signpost = Signpost()

    private val log = registerLog()

    var isRetry: Boolean = false
        private set

    var isComplete: Boolean = false
        private set

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
        log("create: $width")
        doAsyncLoad {
            val mazeSize = if (width % 2 == 0) {
                width + 1
            } else {
                width
            }
            log("create: generate.mazeSize $mazeSize")
            val mazeMap = Maze.generate(mazeSize)
            val path = MPath()
            onMazeChanged(mazeMap, path)
        }
    }

    fun load(id: Int) {
        log("load: $id")
        loadHistory {
            DataManager.findById(id)
        }
    }

    fun load(path: String) {
        log("load: $path")
        loadHistory {
            DataManager.findByFile(path)
        }
    }

    private fun loadHistory(block: () -> MazeHistory?) {
        log("loadHistory")
        doAsyncLoad {
            val cache = block()
            if (cache != null) {
                isComplete = cache.isComplete
                onMazeChanged(cache.maze, cache.path)
            } else {
                postUI {
                    callback.onMazeCacheNotFound()
                }
            }
        }
    }

    private fun doAsyncLoad(block: () -> Unit) {
        callback.onLoadingStart()
        doAsync {
            block()
            postUI {
                callback.onLoadingEnd()
            }
        }
    }

    private fun onMazeChanged(newMaze: MazeMap, newPath: MPath) {
        log("onMazeChanged: ${newMaze.tagName()}")

        currentMaze = newMaze
        currentPath = newPath

        fixPath()
        val lastPoint = newPath.last() ?: newMaze.start
        focusBlock.set(lastPoint)
        if (!isDestroy) {
            log("onMazeChanged.post.onMazeResult")
            postUI {
                callback.onMazeResult(newMaze, newPath, focusBlock)
            }
        }
    }

    fun manipulate(direction: JoystickDirection) {
        log("manipulate: $direction")
        val maze = currentMaze ?: return
        val path = currentPath ?: return
        signpost.fetch(maze, focusBlock)
        if (signpost.isDirectionEnable(direction)) {
            val next = signpost.getPoint(direction)
            val secondLast = path.secondLast()
            if (next.isSame(secondLast)) {
                // 如果下一个等于上一个，那么等于做了回退。需要执行回退逻辑
                path.back()
                // 回退之后如果是空的，那么又加回来
                fixPath()
                // 当前的焦点，是最后一个
                val snapshot = focusBlock.snapshot()
                focusBlock.set(path.last() ?: maze.start)
                previousBlock.set(snapshot)
            } else {
                // 否则就是前进
                previousBlock.set(focus)
                focusBlock.set(next)
                path.put(next)
                log("manipulate: move to $next")
            }
            postUI {
                log("post.callback.onPointChange: $previous -> $focus")
                callback.onPointChange(previous, focus)
            }
            checkComplete()
        }
    }

    fun onResume() {
        val maze = currentMaze ?: return
        val path = currentPath
        fixPath()
        focusBlock.set(path.last() ?: maze.start)
        previousBlock.set(focus)
        postUI {
            callback.onPointChange(previous, focus)
        }
    }

    private fun checkComplete() {
        if (isComplete) {
            log("checkComplete: complete break")
            return
        }
        log("checkComplete: check")
        val mazeMap = currentMaze ?: return
        val end = mazeMap.end
        if (focusBlock.isSame(end)) {
            log("checkComplete: complete now, post event")
            isComplete = true
            postUI {
                callback.onComplete(mazeMap, currentPath)
            }
        }
    }

    fun resetComplete() {
        isComplete = false
    }

    private fun fixPath() {
        val path = currentPath
        if (path.isEmpty()) {
            currentMaze?.start?.let {
                path.put(it)
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

        fun onMazeCacheNotFound()

        fun onPointChange(fromPoint: MPoint, toPoint: MPoint)

        fun onComplete(maze: MazeMap, path: MPath)

    }

    private class Signpost {

        private val log = registerLog()

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
            log("fetch: $point")
            current.set(point)
            left = map[point.x - 1, point.y] == Maze.ROAD
            right = map[point.x + 1, point.y] == Maze.ROAD
            up = map[point.x, point.y - 1] == Maze.ROAD
            down = map[point.x, point.y + 1] == Maze.ROAD
            log("fetch: left = $left, right = $right, up = $up, down = $down")
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