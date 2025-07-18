package com.lollipop.maze

class Horizon(
    val width: Int,
    val height: Int,
) {

    /**
     * 视野中的地图
     * 更喜欢按行来计算，所以列优先
     */
    val map = Array(height) { IntArray(width) }

    operator fun get(x: Int, y: Int): Int {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return 0
        }
        return map[y][x]
    }

    operator fun set(x: Int, y: Int, value: Int) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return
        }
        map[y][x] = value
    }

}