package com.lollipop.maze

import com.lollipop.maze.data.MMap
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
    println(
        MazeTest.print(Maze.generate(21))
            .write("maze-${System.currentTimeMillis()}.csv")
    )
    println("------------------------------")
    println(
        MazeTest.print(Maze.generate(21, generator = SpreadGenerator))
            .write("maze-${System.currentTimeMillis()}.csv")
    )
}

object MazeTest {

    class CSV() {

        private val builder = StringBuilder()

        private var isFirst = true

        fun add(value: String) {
            if (!isFirst) {
                builder.append(",")
            }
            builder.append(value)
            isFirst = false
        }

        fun enter() {
            isFirst = true
            builder.append("\r\n")
        }

        fun build(): String {
            return builder.toString()
        }

        fun write(name: String): String {
            val dir = File(System.getProperty("user.home")!!)
            val file = File(dir, name)
            file.writeText(build())
            return file.path
        }

    }

    fun print(
        mazeMap: MazeMap,
        startKey: String = "s",
        endKey: String = "e",
        roadKey: String = "r",
        wallKey: String = "w",
        unknownKey: String = "?"
    ): CSV {
        val map = mazeMap.map
        val csv = CSV()
        for (y in 0 until mazeMap.height) {
            for (x in 0 until mazeMap.width) {
                if (x == mazeMap.start.x && y == mazeMap.start.y) {
                    csv.add(startKey)
                } else if (x == mazeMap.end.x && y == mazeMap.end.y) {
                    csv.add(endKey)
                } else {
                    csv.add(
                        when (map[x, y]) {
                            Maze.ROAD -> {
                                roadKey
                            }

                            Maze.WALL -> {
                                wallKey
                            }

                            else -> {
                                unknownKey
                            }
                        }
                    )
                }
            }
            csv.enter()
        }
        return csv
    }

    fun print(
        map: MMap,
        roadKey: String = "r",
        wallKey: String = "w",
        unknownKey: String = "?"
    ): CSV {
        val csv = CSV()
        for (y in 0 until map.height) {
            for (x in 0 until map.width) {
                csv.add(
                    when (map[x, y]) {
                        Maze.ROAD -> {
                            roadKey
                        }

                        Maze.WALL -> {
                            wallKey
                        }

                        else -> {
                            unknownKey
                        }
                    }
                )
            }
            csv.enter()
        }
        return csv
    }

}