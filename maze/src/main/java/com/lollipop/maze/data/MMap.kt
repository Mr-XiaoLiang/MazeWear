package com.lollipop.maze.data

import com.lollipop.maze.Maze

class MMap(val width: Int, val height: Int, val initValue: Int = Maze.EMPTY) {

    val size = width * height

    /**
     * 更喜欢按行来计算，所以列优先
     */
    val map = Array(height) { IntArray(width) { initValue } }

    operator fun get(x: Int, y: Int): Int {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return Maze.EMPTY
        }
        return map[y][x]
    }

    operator fun set(x: Int, y: Int, value: Int) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return
        }
        map[y][x] = value
    }

    fun reset() {
        for (line in map) {
            for (i in line.indices) {
                line[i] = initValue
            }
        }
    }

    fun road(x: Int, y: Int) {
        this[x, y] = Maze.ROAD
    }

    fun wall(x: Int, y: Int) {
        this[x, y] = Maze.WALL
    }

    fun empty(x: Int, y: Int) {
        this[x, y] = Maze.EMPTY
    }

}