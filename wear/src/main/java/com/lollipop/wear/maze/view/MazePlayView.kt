package com.lollipop.wear.maze.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Path
import android.graphics.Rect
import android.util.AttributeSet
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.withSave
import com.lollipop.maze.data.MBlock
import com.lollipop.maze.data.MMap
import com.lollipop.maze.data.MPath
import com.lollipop.wear.maze.view.draw.PathDrawable
import com.lollipop.wear.maze.view.draw.SpiritDrawable
import com.lollipop.wear.maze.view.draw.TileDrawable
import com.lollipop.wear.maze.view.draw.TileMap
import com.lollipop.wear.maze.view.draw.color.ColorPathDrawable
import com.lollipop.wear.maze.view.draw.color.ColorSpiritDrawable
import com.lollipop.wear.maze.view.draw.color.ColorTileDrawable

class MazePlayView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : AppCompatImageView(context, attributeSet) {

    private val mapDrawable = MapDrawable()

    init {
        setImageDrawable(mapDrawable)
    }

    fun setTileDrawable(tileDrawable: TileDrawable) {
        mapDrawable.tileDrawable = tileDrawable
        invalidate()
    }

    fun setPathDrawable(pathDrawable: PathDrawable) {
        mapDrawable.pathDrawable = pathDrawable
        invalidate()
    }

    fun setSpiritDrawable(spiritDrawable: SpiritDrawable) {
        mapDrawable.spiritDrawable = spiritDrawable
        invalidate()
    }

    fun setViewportSize(width: Int, height: Int) {
        mapDrawable.setViewportSize(width, height)
    }

    fun setSource(sourceMap: MMap, path: MPath) {
        mapDrawable.setSource(sourceMap, path)
    }

    fun setFocus(x: Int, y: Int) {
        mapDrawable.setFocus(x, y)
    }

    fun updateProgress(progress: Float) {
        mapDrawable.updateProgress(progress)
    }

    fun setNext(x: Int, y: Int) {
        mapDrawable.setNext(x, y)
    }

    private class MapDrawable : MazeBasicDrawable() {

        companion object {
            const val MAP_BUFFER = 2
        }

        private var viewportWidth = 5
        private var viewportHeight = 5
        private var mapWidth = 0
        private var mapHeight = 0
        private var sourceMap: MMap? = null
        private var pathList: MPath? = null

        private var focusBlock = MBlock(x = -1, y = -1)
        private var nextBlock = MBlock(x = -1, y = -1)

        private var tileMap = TileMap(::updateViewportMap)

        private var animationProgress = 0F

        private var drawMap = MMap(viewportWidth + 2, viewportHeight + 2)
        private var routePath = Path()

        private var drawLeftEdge = 0F
        private var drawTopEdge = 0F


        var tileDrawable: TileDrawable = ColorTileDrawable()
        var pathDrawable: PathDrawable = ColorPathDrawable()
        var spiritDrawable: SpiritDrawable = ColorSpiritDrawable()

        private fun getMapBufferSize(size: Int): Int {
            return size + MAP_BUFFER
        }

        fun setViewportSize(width: Int, height: Int) {
            viewportWidth = width
            viewportHeight = height
            val bufferWidth = getMapBufferSize(width)
            val bufferHeight = getMapBufferSize(height)
            if (drawMap.width != bufferWidth || drawMap.height != bufferHeight) {
                drawMap = MMap(bufferWidth, bufferHeight)
            }
            updateViewportMap()
        }

        fun setSource(sourceMap: MMap, path: MPath) {
            this.sourceMap = sourceMap
            this.pathList = path
            mapWidth = sourceMap.width
            mapHeight = sourceMap.height
            tileMap.setSource(sourceMap)
        }

        fun setFocus(x: Int, y: Int) {
            focusBlock.set(x, y)
            updateViewportMap()
        }

        fun updateProgress(progress: Float) {
            animationProgress = progress
            invalidateSelf()
        }

        fun setNext(x: Int, y: Int) {
            nextBlock.set(x, y)
            animationProgress = 0F
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
            updateGrid(mapWidth, mapHeight, bounds.width(), bounds.height())
            tileMap.getFragment(focusBlock.x, focusBlock.y, drawMap)
            val halfBlock = blockSize / 2

            drawLeftEdge = (bounds.width() / 2 - halfBlock)
            drawLeftEdge -= (viewportWidth / 2) * blockSize

            drawTopEdge = (bounds.height() / 2 - halfBlock)
            drawTopEdge -= (viewportHeight / 2) * blockSize

            val fragmentTop = drawTopEdge.toInt()
            val fragmentBottom = (fragmentTop + (blockSize * drawMap.height)).toInt()
            val fragmentLeft = drawLeftEdge.toInt()
            val fragmentRight = (fragmentLeft + (blockSize * drawMap.width)).toInt()

            routePath.reset()

            var isInFragment = false
            pathList?.pointList?.forEach { point ->
                val oldFlag = isInFragment
                if (!isInFragment) {
                    if (point.x >= fragmentLeft && point.x <= fragmentRight && point.y >= fragmentTop && point.y <= fragmentBottom) {
                        isInFragment = true
                    }
                }
                if (isInFragment) {
                    val pointX = (point.x * blockSize) + halfBlock
                    val pointY = (point.y * blockSize) + halfBlock
                    if (!oldFlag) {
                        routePath.moveTo(pointX, pointY)
                    } else {
                        routePath.lineTo(pointX, pointY)
                    }
                }
            }
        }

        override fun draw(canvas: Canvas) {
            sourceMap ?: return
            val offsetX = (nextBlock.x - focusBlock.x) * animationProgress
            val offsetY = (nextBlock.y - focusBlock.y) * animationProgress
            canvas.withSave {
                canvas.translate(offsetX * -1, offsetY * -1)
                for (x in 0 until drawMap.width) {
                    for (y in 0 until drawMap.height) {
                        val tile = drawMap[x, y]
                        drawTile(
                            canvas,
                            (drawLeftEdge + (x * blockSize)),
                            (drawTopEdge + (y * blockSize)),
                            tile
                        )
                    }
                }
                drawPath(canvas)
                drawSpiriter(canvas)
            }
        }

        private fun drawTile(canvas: Canvas, x: Float, y: Float, tile: Int) {
            tileDrawable.draw(canvas, x, y, blockSize, tile)
        }

        private fun drawPath(canvas: Canvas) {
            pathDrawable.draw(canvas, routePath, blockSize)
        }

        private fun drawSpiriter(canvas: Canvas) {
            spiritDrawable.draw(canvas, bounds.exactCenterX(), bounds.exactCenterY(), blockSize)
        }

        override fun setAlpha(alpha: Int) {
            tileDrawable.setAlpha(alpha)
            pathDrawable.setAlpha(alpha)
            spiritDrawable.setAlpha(alpha)
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            tileDrawable.setColorFilter(colorFilter)
            pathDrawable.setColorFilter(colorFilter)
            spiritDrawable.setColorFilter(colorFilter)
        }
    }

}