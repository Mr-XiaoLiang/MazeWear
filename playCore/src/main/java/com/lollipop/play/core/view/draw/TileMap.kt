package com.lollipop.play.core.view.draw

import com.lollipop.maze.Maze
import com.lollipop.maze.data.MMap
import com.lollipop.maze.helper.doAsync
import com.lollipop.maze.helper.onUI

class TileMap(
    private val onMapChanged: () -> Unit
) {

    companion object {
        const val TILE_EMPTY = 0
        const val TILE_TOP_ONLY = 0b0001
        const val TILE_RIGHT_ONLY = 0b0010
        const val TILE_BOTTOM_ONLY = 0b0100
        const val TILE_LEFT_ONLY = 0b1000
        const val TILE_LEFT_TOP = TILE_LEFT_ONLY or TILE_TOP_ONLY
        const val TILE_LEFT_BOTTOM = TILE_LEFT_ONLY or TILE_BOTTOM_ONLY
        const val TILE_LEFT_BOTTOM_TOP = TILE_LEFT_ONLY or TILE_BOTTOM_ONLY or TILE_TOP_ONLY
        const val TILE_LEFT_RIGHT = TILE_LEFT_ONLY or TILE_RIGHT_ONLY
        const val TILE_LEFT_RIGHT_BOTTOM = TILE_LEFT_ONLY or TILE_RIGHT_ONLY or TILE_BOTTOM_ONLY
        const val TILE_LEFT_RIGHT_TOP = TILE_LEFT_ONLY or TILE_RIGHT_ONLY or TILE_TOP_ONLY

        const val TILE_RIGHT_TOP = TILE_RIGHT_ONLY or TILE_TOP_ONLY
        const val TILE_RIGHT_BOTTOM = TILE_RIGHT_ONLY or TILE_BOTTOM_ONLY
        const val TILE_RIGHT_BOTTOM_TOP = TILE_RIGHT_ONLY or TILE_BOTTOM_ONLY or TILE_TOP_ONLY

        const val TILE_TOP_BOTTOM = TILE_TOP_ONLY or TILE_BOTTOM_ONLY

        const val TILE_LEFT_RIGHT_BOTTOM_TOP =
            TILE_LEFT_ONLY or TILE_RIGHT_ONLY or TILE_BOTTOM_ONLY or TILE_TOP_ONLY
    }

    private var tileMap: MMap? = null

    fun setSource(source: MMap) {
        doAsync {
            val map = MMap(source.width, source.height)
            for (x in 0 until source.width) {
                for (y in 0 until source.height) {
                    if (source.isWall(x, y)) {
                        val left = source.isWall(x - 1, y)
                        val top = source.isWall(x, y - 1)
                        val right = source.isWall(x + 1, y)
                        val bottom = source.isWall(x, y + 1)
                        val tile = getTile(left, top, right, bottom)
                        map[x, y] = tile
                    } else {
                        map[x, y] = TILE_EMPTY
                    }
                }
            }
            onUI {
                this.tileMap = map
                onMapChanged()
            }
        }

    }

    fun getFragment(centerX: Int, centerY: Int, out: MMap) {
        val sourceMap = tileMap ?: return
        val startX = centerX - (out.width / 2)
        val startY = centerY - (out.height / 2)
        for (x in 0 until out.width) {
            for (y in 0 until out.height) {
                out[x, y] = sourceMap[startX + x, startY + y]
            }
        }
    }

    private fun MMap.isRoad(x: Int, y: Int): Boolean {
        return this[x, y] == Maze.ROAD
    }

    private fun MMap.isWall(x: Int, y: Int): Boolean {
        return this[x, y] == Maze.WALL
    }

    private fun getTile(left: Boolean, top: Boolean, right: Boolean, bottom: Boolean): Int {
        var tile = 0
        if (left) {
            tile = tile or TILE_LEFT_ONLY
        }
        if (top) {
            tile = tile or TILE_TOP_ONLY
        }
        if (right) {
            tile = tile or TILE_RIGHT_ONLY
        }
        if (bottom) {
            tile = tile or TILE_BOTTOM_ONLY
        }
        return tile
    }

}