package com.lollipop.maze

import com.lollipop.maze.generate.DeepGenerator
import com.lollipop.maze.generate.MazeGenerator

object Maze {

    /**
     * 道路
     */
    const val ROAD = 0

    /**
     * 墙
     */
    const val WALL = 1

    /**
     * 空白
     */
    const val EMPTY = -1

    var defaultGenerator = DeepGenerator

    /**
     * 生成迷宫
     */
    fun generate(
        width: Int,
        height: Int = width,
        generator: MazeGenerator = defaultGenerator
    ): MazeMap {
        return generator.generate(width, height)
    }

}

fun main() {
    MazeTest.print(Maze.generate(21, 21))
}

object MazeTest {

    fun print(mazeMap: MazeMap) {
        val map = mazeMap.map.map
        for (x in 0 until mazeMap.width) {
            for (y in 0 until mazeMap.height) {
                if (x == mazeMap.startX && y == mazeMap.startY) {
                    print("S")
                } else if (x == mazeMap.endX && y == mazeMap.endY) {
                    print("E")
                } else {
                    print(
                        when (map[x][y]) {
                            Maze.ROAD -> {
                                "□"
                            }

                            Maze.WALL -> {
                                "■"
                            }

                            else -> {
                                "?"
                            }
                        }
                    )
                }
            }
            println()
        }
    }

}