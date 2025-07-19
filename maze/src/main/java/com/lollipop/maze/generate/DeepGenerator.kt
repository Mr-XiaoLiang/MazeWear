package com.lollipop.maze.generate

import com.lollipop.maze.data.MMap
import java.util.LinkedList

object DeepGenerator : BasicAverageGenerator() {

    /**
     * 构建迷宫
     * 回溯算法，会从一个随机起点开始
     * 然后随机从四个方向的可用路点中选择一个，作为下一个起点，并且将其改为路面
     * 以此循环，直到四个方向都没有可用路点
     * 然后沿路向后回退，直到四方出现可用路点，再次开启循环，直到所有路都构建完成
     */
    override fun buildByBlueprint(blueprint: MMap) {
        val startBlock = findBuildStart(blueprint)
        val road = LinkedList<Block>()
        val nextBlock = Block(startBlock)
        var roadSignCount = 0
        val signMax = (blueprint.width / 2) * (blueprint.height / 2)
        while (true) {
            val x = nextBlock.x
            val y = nextBlock.y
            val hasUp = findRoadPending(x, y, blueprint, Direction.UP)
            val hasDown = findRoadPending(x, y, blueprint, Direction.DOWN)
            val hasLeft = findRoadPending(x, y, blueprint, Direction.LEFT)
            val hasRight = findRoadPending(x, y, blueprint, Direction.RIGHT)
            // 如果都没有路了，那么往前数一格，然后开启下一个循环
            if (!hasUp && !hasDown && !hasLeft && !hasRight) {
                if (road.isEmpty()) {
                    break
                }
                val backBlock = road.removeLast()
                if (backBlock == null) {
                    break
                }
                nextBlock.set(backBlock.x, backBlock.y)
                continue
            }
            val nextDirection = randomDirection(hasUp, hasDown, hasLeft, hasRight)
            if (nextDirection != null) {
                // 选中当前点的下一个方向，然后告诉我下一个点
                select(map = blueprint, x = x, y = y, direction = nextDirection, out = nextBlock)
                // 记录当前点，作为路径方便回溯
                road.add(Block(x, y))
                // 累加路标计数，方便检查是否存在遗漏
                roadSignCount++
            }
        }
        if (roadSignCount < signMax) {
            println("存在遗漏路标")
            for (x in 0 until blueprint.width) {
                for (y in 0 until blueprint.height) {
                    if (blueprint[x, y] == ROAD_PENDING) {
                        blueprint[x, y] = ROAD
                        println("填充遗漏路标：$x, $y")
                        val nextDirection = randomDirection(
                            blueprint[x, y - 1] == WALL_PENDING,
                            blueprint[x, y + 1] == WALL_PENDING,
                            blueprint[x - 1, y] == WALL_PENDING,
                            blueprint[x + 1, y] == WALL_PENDING
                        )
                        when (nextDirection) {
                            Direction.UP -> {
                                blueprint[x, y - 1] = ROAD
                            }

                            Direction.DOWN -> {
                                blueprint[x, y + 1] = ROAD
                            }

                            Direction.LEFT -> {
                                blueprint[x - 1, y] = ROAD
                            }

                            Direction.RIGHT -> {
                                blueprint[x + 1, y] = ROAD
                            }

                            null -> {}
                        }
                    }
                }
            }
        }
    }

    private fun select(map: MMap, x: Int, y: Int, direction: Direction, out: Block) {
        when (direction) {
            Direction.UP -> {
                map[x, y - 1] = ROAD
                map[x, y - 2] = ROAD
                out.x = x
                out.y = y - 2
            }

            Direction.DOWN -> {
                map[x, y + 1] = ROAD
                map[x, y + 2] = ROAD
                out.x = x
                out.y = y + 2
            }

            Direction.LEFT -> {
                map[x - 1, y] = ROAD
                map[x - 2, y] = ROAD
                out.x = x - 2
                out.y = y
            }

            Direction.RIGHT -> {
                map[x + 1, y] = ROAD
                map[x + 2, y] = ROAD
                out.x = x + 2
                out.y = y
            }
        }
    }


}