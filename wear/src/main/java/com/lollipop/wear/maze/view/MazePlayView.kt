package com.lollipop.wear.maze.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Rect
import android.util.AttributeSet
import android.widget.ImageView
import com.lollipop.maze.Maze
import com.lollipop.maze.data.MBlock
import com.lollipop.maze.data.MMap
import com.lollipop.maze.helper.doAsync
import com.lollipop.maze.helper.onUI

class MazePlayView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : ImageView(context, attributeSet) {


    private class MapDrawable : MazeBasicDrawable() {

        private var viewportWidth = 5
        private var viewportHeight = 5
        private var mapWidth = 0
        private var mapHeight = 0
        private var sourceMap: MMap? = null

        private var focusBlock = MBlock(x = -1, y = -1)
        private var nextBlock = MBlock(x = -1, y = -1)

        private var tileMap = TileMap(::updateViewportMap)

        private var animationProgress = 0F

        fun setViewportSize(width: Int, height: Int) {
            viewportWidth = width
            viewportHeight = height
            updateViewportMap()
        }

        fun setSource(sourceMap: MMap) {
            this.sourceMap = sourceMap
            mapWidth = sourceMap.width
            mapHeight = sourceMap.height
            tileMap.setSource(sourceMap)
        }

        fun setFocus(x: Int, y: Int) {
            focusBlock.set(x, y)
            updateViewportMap()
        }

        fun setNext(x: Int, y: Int) {
            nextBlock.set(x, y)
            updateViewportMap()
        }

        override fun onBoundsChange(bounds: Rect) {
            super.onBoundsChange(bounds)
            updateViewportMap()
        }

        private fun updateViewportMap() {
            if (bounds.isEmpty) {
                return
            }
            // TODO
        }

        override fun draw(canvas: Canvas) {
            TODO("Not yet implemented")
        }

        override fun setAlpha(alpha: Int) {
            TODO("Not yet implemented")
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            TODO("Not yet implemented")
        }
    }

    private class TileMap(
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
                        val left = source.isRoad(x - 1, y)
                        val top = source.isRoad(x, y - 1)
                        val right = source.isRoad(x + 1, y)
                        val bottom = source.isRoad(x, y + 1)
                        val tile = getTile(left, top, right, bottom)
                        map[x, y] = tile
                    }
                }
                onUI {
                    this.tileMap = map
                    onMapChanged()
                }
            }

        }

        private fun MMap.isRoad(x: Int, y: Int): Boolean {
            return this[x, y] == Maze.ROAD
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

}