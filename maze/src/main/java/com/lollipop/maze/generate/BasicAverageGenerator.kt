package com.lollipop.maze.generate

import com.lollipop.maze.Maze
import com.lollipop.maze.MazeMap
import com.lollipop.maze.data.MBlock
import com.lollipop.maze.data.MMap
import com.lollipop.maze.data.MPoint
import kotlin.random.Random

abstract class BasicAverageGenerator : MazeGenerator {

    companion object {
        const val ROAD = Maze.ROAD
        const val WALL = Maze.WALL

        const val ROAD_PENDING = (ROAD + 1) * 10
        const val WALL_PENDING = (WALL + 1) * 10

    }

    override fun generate(width: Int, height: Int): MazeMap {
        val blueprint = blueprint(width, height)
        buildByBlueprint(blueprint)
        fillPendingWall(blueprint)
        val start = findStart(blueprint)
        val end = findEnd(blueprint, start)
        return MazeMap(
            start = MPoint(x = start.x, y = start.y),
            end = MPoint(x = end.x, y = end.y),
            map = blueprint
        )
    }

    protected abstract fun buildByBlueprint(blueprint: MMap)

    protected fun fillPendingWall(blueprint: MMap) {
        for (x in 0 until blueprint.width) {
            for (y in 0 until blueprint.height) {
                if (blueprint[x, y] == WALL_PENDING) {
                    blueprint[x, y] = WALL
                }
            }
        }
    }

    /**
     * 创建迷宫蓝图
     * 这里的蓝图，会是一个矩阵，而矩阵中，出了最外面的一圈以外，内部会按照间距为1的规则，均匀分布等待连接的路
     */
    protected fun blueprint(width: Int, height: Int): MMap {
        val map = MMap(width = width, height = height, initValue = WALL_PENDING)
        // 四边先封墙， 这是横向的
        for (x in 0 until map.width) {
            map[x, 0] = WALL
            map[x, map.height - 1] = WALL
        }
        // 四边先封墙， 这是纵向的
        for (y in 0 until map.height) {
            map[0, y] = WALL
            map[map.width - 1, y] = WALL
        }
        // 中间填充基础路点
        for (y in 1 until height step 2) {
            for (x in 1 until width step 2) {
                map[x, y] = ROAD_PENDING
            }
        }
        return map
    }

    protected fun randomDirection(
        up: Boolean,
        down: Boolean,
        left: Boolean,
        right: Boolean
    ): Direction? {
        var randomCount = 0
        if (up) {
            randomCount++
        }
        if (down) {
            randomCount++
        }
        if (left) {
            randomCount++
        }
        if (right) {
            randomCount++
        }
        if (randomCount == 0) {
            return null
        }
        var direction = if (randomCount < 2) {
            0
        } else {
            Random.nextInt(randomCount)
        }
        var nextDirection: Direction? = null
        if (up) {
            if (direction == 0) {
                nextDirection = Direction.UP
            }
            direction--
        }
        if (down) {
            if (direction == 0) {
                nextDirection = Direction.DOWN
            }
            direction--
        }
        if (left) {
            if (direction == 0) {
                nextDirection = Direction.LEFT
            }
            direction--
        }
        if (right) {
            if (direction == 0) {
                nextDirection = Direction.RIGHT
            }
            direction--
        }
        return nextDirection
    }

    protected fun MMap.isRoadPending(x: Int, y: Int): Boolean {
        return this[x, y] == ROAD_PENDING
    }

    protected fun MMap.isWallPending(x: Int, y: Int): Boolean {
        return this[x, y] == WALL_PENDING
    }

    protected fun MMap.isWallPending(block: MBlock): Boolean {
        return isWallPending(block.x, block.y)
    }

    protected fun MMap.isRoadPending(block: MBlock): Boolean {
        return isRoadPending(block.x, block.y)
    }

    protected fun findStart(map: MMap): MBlock {
        val hEdge = Random.nextBoolean()
        if (hEdge) {
            val left = Random.nextBoolean()
            val y = Random.nextInt(map.height - 2) + 1
            val x = if (left) {
                1
            } else {
                map.width - 2
            }
            return MBlock(x, y)
        } else {
            val top = Random.nextBoolean()
            val x = Random.nextInt(map.width - 2) + 1
            val y = if (top) {
                1
            } else {
                map.height - 2
            }
            return MBlock(x, y)
        }
    }

    protected fun findEnd(map: MMap, start: MBlock): MBlock {
        var x = -1
        var y = -1
        // 如果起点靠左，那么我们的终点就得靠右
        if (start.x == 1) {
            x = map.width - 2
        } else if (start.x == map.width - 2) {
            x = 1
        }
        if (start.y == 1) {
            y = map.height - 2
        } else if (start.y == map.height - 2) {
            y = 1
        }
        if (x < 0) {
            x = Random.nextInt(map.width - 1) + 1
        }
        if (y < 0) {
            y = Random.nextInt(map.height - 1) + 1
        }
        return MBlock(x, y)
    }

    protected fun findBuildStart(map: MMap, deep: Int = 0): MBlock {
        val randomX = Random.nextInt(map.width)
        val randomY = Random.nextInt(map.height)
        if (map[randomX, randomY] == ROAD_PENDING) {
            return MBlock(randomX, randomY)
        }
        if (map[randomX, randomY - 1] == ROAD_PENDING) {
            return MBlock(randomX, randomY - 1)
        }
        if (map[randomX, randomY + 1] == ROAD_PENDING) {
            return MBlock(randomX, randomY + 1)
        }
        if (map[randomX - 1, randomY] == ROAD_PENDING) {
            return MBlock(randomX - 1, randomY)
        }
        if (map[randomX + 1, randomY] == ROAD_PENDING) {
            return MBlock(randomX + 1, randomY)
        }
        if (deep > 10) {
            throw RuntimeException("无法找到起始点")
        }
        // 再随机一次吧
        return findBuildStart(map, deep + 1)
    }

    protected fun findRoadPending(x: Int, y: Int, map: MMap, direction: Direction): Boolean {
        when (direction) {
            Direction.UP -> {
                return map.isRoadPending(x, y - 2)
            }

            Direction.DOWN -> {
                return map.isRoadPending(x, y + 2)
            }

            Direction.LEFT -> {
                return map.isRoadPending(x - 2, y)
            }

            Direction.RIGHT -> {
                return map.isRoadPending(x + 2, y)
            }
        }
    }

    protected fun next(x: Int, y: Int, direction: Direction, out: MBlock) {
        when (direction) {
            Direction.UP -> {
                return out.set(x, y - 2)
            }

            Direction.DOWN -> {
                return out.set(x, y + 2)
            }

            Direction.LEFT -> {
                return out.set(x - 2, y)
            }

            Direction.RIGHT -> {
                return out.set(x + 2, y)
            }
        }
    }

    protected enum class Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

}


