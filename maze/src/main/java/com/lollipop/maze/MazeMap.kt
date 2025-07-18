package com.lollipop.maze

import com.lollipop.maze.data.MMap

class MazeMap(
    val startX: Int,
    val startY: Int,
    val endX: Int,
    val endY: Int,
    val map: MMap,
) {

    val width = map.width
    val height = map.height

    operator fun get(x: Int, y: Int): Int {
        return map[x, y]
    }

    operator fun set(x: Int, y: Int, value: Int) {
        map[x, y] = value
    }

    fun get(centerX: Int, centerY: Int, horizon: Horizon) {
        val xRadius = horizon.width / 2
        val yRadius = horizon.height / 2
        var xStart = centerX - xRadius
        var xEnd = centerX + xRadius
        var yStart = centerY - yRadius
        var yEnd = centerY + yRadius
        var xOffset = 0
        var yOffset = 0
        if (xStart < 0) {
            xOffset = -xStart
            xStart = 0
        }
        if (xEnd >= width) {
            xEnd = width - 1
        }
        if (yStart < 0) {
            yOffset = -yStart
            yStart = 0
        }
        if (yEnd >= height) {
            yEnd = height - 1
        }
        for (y in yStart..yEnd) {
            for (x in xStart..xEnd) {
                horizon[x - xStart + xOffset, y - yStart + yOffset] = get(x, y)
            }
        }
    }

}