package com.lollipop.maze

import com.lollipop.maze.generate.DeepGenerator
import com.lollipop.maze.generate.MazeGenerator
import com.lollipop.maze.generate.SpreadGenerator
import java.io.File

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
    MazeTest.print(Maze.generate(21))
    println("------------------------------")
    MazeTest.print(Maze.generate(21, generator = SpreadGenerator))
}

object MazeTest {

    class CSV() {

        private val builder = StringBuilder()

        fun add(value: String) {
            builder.append(value).append(",")
        }

        fun enter() {
            builder.append("\r\n")
        }

        fun build(): String {
            return builder.toString()
        }

        fun write(name: String) {
            val dir = File(System.getProperty("user.home")!!)
            File(dir, name).writeText(build())
        }

    }

    fun print(mazeMap: MazeMap) {
        val map = mazeMap.map.map
        val csv = CSV()
        for (x in 0 until mazeMap.width) {
            for (y in 0 until mazeMap.height) {
                if (x == mazeMap.start.x && y == mazeMap.start.y) {
                    csv.add("Start")
                } else if (x == mazeMap.end.x && y == mazeMap.end.y) {
                    csv.add("End")
                } else {
                    csv.add(
                        when (map[x][y]) {
                            Maze.ROAD -> {
                                ""
                            }

                            Maze.WALL -> {
                                "WALL"
                            }

                            else -> {
                                "?"
                            }
                        }
                    )
                }
            }
            csv.enter()
        }
        csv.write("maze-${System.currentTimeMillis()}.csv")
    }

}