package com.lollipop.maze.data

class MViewport(val mapWidth: Int, val mapHeight: Int) {

    val map = MMap(width = mapWidth, height = mapHeight)

    private var sourceMap: MMap? = null

    private var viewportWidth = 5
    private var viewportHeight = 5

    fun setViewportSize(width: Int, height: Int) {
        viewportWidth = width
        viewportHeight = height
    }

    fun setSource(sourceMap: MMap) {
        this.sourceMap = sourceMap
        map.reset()
    }

    fun addPort(x: Int, y: Int) {
        val source = sourceMap ?: return
        val startX = (x - (viewportWidth / 2))
        val startY = (y - (viewportHeight / 2))
        for (x in startX until startX + viewportWidth) {
            for (y in startY until startY + viewportHeight) {
                map[x, y] = source[x, y]
            }
        }
    }

}