package com.lollipop.play.core.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Path
import android.graphics.Rect
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.withSave
import com.lollipop.maze.data.MBlock
import com.lollipop.maze.data.MMap
import com.lollipop.maze.data.MPath
import com.lollipop.maze.data.MPoint
import com.lollipop.play.core.helper.registerLog
import com.lollipop.play.core.view.draw.PathDrawable
import com.lollipop.play.core.view.draw.SpiritDrawable
import com.lollipop.play.core.view.draw.TileDrawable
import com.lollipop.play.core.view.draw.TileMap
import com.lollipop.play.core.view.draw.color.ColorPathDrawable
import com.lollipop.play.core.view.draw.color.ColorSpiritDrawable
import com.lollipop.play.core.view.draw.color.ColorTileDrawable

class MazePlayView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : AppCompatImageView(context, attributeSet) {

    private val mapDrawable = MapDrawable()

    init {
        setImageDrawable(mapDrawable)
    }

    private fun setTileDrawable(tileDrawable: TileDrawable) {
        mapDrawable.tileDrawable = tileDrawable
        invalidate()
    }

    private fun setPathDrawable(pathDrawable: PathDrawable) {
        mapDrawable.pathDrawable = pathDrawable
        invalidate()
    }

    private fun setSpiritDrawable(spiritDrawable: SpiritDrawable) {
        mapDrawable.spiritDrawable = spiritDrawable
        invalidate()
    }

    private fun setViewportSize(width: Int, height: Int) {
        mapDrawable.setViewportSize(width, height)
    }

    private fun setSource(sourceMap: MMap, path: MPath) {
        mapDrawable.setSource(sourceMap, path)
    }

    private fun setFocus(x: Int, y: Int): Boolean {
        return mapDrawable.setFocus(x, y)
    }

    private fun updateProgress(progress: Float) {
        mapDrawable.updateProgress(progress)
    }

    private fun setFrom(x: Int, y: Int): Boolean {
        return mapDrawable.setFrom(x, y)
    }

    private fun setExtremePoint(start: MPoint?, end: MPoint?) {
        mapDrawable.setExtremePoint(start, end)
    }

    fun update(builder: (ActionBuilder) -> Unit) {
        val actionBuilder = ActionBuilder(this)
        builder(actionBuilder)
        actionBuilder.view = null
        if (actionBuilder.isUpdateSource || actionBuilder.isUpdatePointer) {
            // 如果修改了地图或者坐标点，那么需要重新计算
            mapDrawable.updateViewportMap()
        } else if (actionBuilder.isUpdateDrawable || actionBuilder.isUpdateProgress || actionBuilder.isUpdateExtreme) {
            // 如果只是改了绘制或者移动偏移，那么是不需要整体重新计算的
            mapDrawable.invalidateSelf()
        }
    }

    private class MapDrawable : MazeBasicDrawable() {

        companion object {
            const val MAP_BUFFER = 2
            const val MAP_BUFFER_OFFSET = MAP_BUFFER / 2
        }

        private var viewportWidth = 5
        private var viewportHeight = 5
        private var mapWidth = 0
        private var mapHeight = 0
        private var sourceMap: MMap? = null
        private var pathList: MPath? = null

        private var focusBlock = MBlock(x = -1, y = -1)
        private var fromBlock = MBlock(x = -1, y = -1)

        private var tileMap = TileMap(::updateViewportMap)

        private var animationProgress = 0F

        private var drawMap = MMap(viewportWidth + 2, viewportHeight + 2)
        private var routePath = Path()

        private var drawLeftEdge = 0F
        private var drawTopEdge = 0F

        private var startPoint: MPoint? = null
        private var endPoint: MPoint? = null

        var tileDrawable: TileDrawable = ColorTileDrawable()
        var pathDrawable: PathDrawable = ColorPathDrawable()
        var spiritDrawable: SpiritDrawable = ColorSpiritDrawable()

        private fun getMapBufferSize(size: Int): Int {
            return size + MAP_BUFFER
        }

        fun setExtremePoint(start: MPoint?, end: MPoint?) {
            this.startPoint = start
            this.endPoint = end
        }

        fun setViewportSize(width: Int, height: Int) {
            viewportWidth = width
            viewportHeight = height
            val bufferWidth = getMapBufferSize(width)
            val bufferHeight = getMapBufferSize(height)
            if (drawMap.width != bufferWidth || drawMap.height != bufferHeight) {
                drawMap = MMap(bufferWidth, bufferHeight)
            }
        }

        fun setSource(sourceMap: MMap, path: MPath) {
            this.sourceMap = sourceMap
            this.pathList = path
            mapWidth = sourceMap.width
            mapHeight = sourceMap.height
            tileMap.setSource(sourceMap)
        }

        fun setFocus(x: Int, y: Int): Boolean {
            if (focusBlock.x == x && focusBlock.y == y) {
                return false
            }
            focusBlock.set(x, y)
            return true
        }

        fun updateProgress(progress: Float) {
            animationProgress = progress
        }

        fun setFrom(x: Int, y: Int): Boolean {
            if (fromBlock.x == x && fromBlock.y == y) {
                return false
            }
            fromBlock.set(x, y)
            return true
        }

        override fun onBoundsChange(bounds: Rect) {
            super.onBoundsChange(bounds)
            updateViewportMap()
        }

        fun updateViewportMap() {
            if (bounds.isEmpty) {
                return
            }
            updateGrid(viewportWidth, viewportHeight, bounds.width(), bounds.height())
            tileMap.getFragment(focusBlock.x, focusBlock.y, drawMap)
            val halfBlock = blockSize / 2

            drawLeftEdge = (bounds.width() / 2 - halfBlock)
            drawLeftEdge -= (viewportWidth / 2) * blockSize

            drawTopEdge = (bounds.height() / 2 - halfBlock)
            drawTopEdge -= (viewportHeight / 2) * blockSize

            routePath.reset()

            var isFirst = true
            pathList?.pointList?.forEach { point ->
                val pointX = (point.x * blockSize) + halfBlock
                val pointY = (point.y * blockSize) + halfBlock
                if (isFirst) {
                    routePath.moveTo(pointX, pointY)
                    isFirst = false
                } else {
                    routePath.lineTo(pointX, pointY)
                }
            }
            invalidateSelf()
        }

        private val log = registerLog()

        override fun draw(canvas: Canvas) {
            sourceMap ?: return
            val p = (1 - animationProgress)
            val offsetX = (fromBlock.x - focusBlock.x) * p
            val offsetY = (fromBlock.y - focusBlock.y) * p
//            log("draw progess = $p, offsetX = $offsetX, offsetY = $offsetY")
            canvas.withSave {
                canvas.translate(offsetX, offsetY)
                for (x in 0 until drawMap.width) {
                    for (y in 0 until drawMap.height) {
                        val tileX = x + MAP_BUFFER_OFFSET
                        val tileY = y + MAP_BUFFER_OFFSET
                        val tile = drawMap[tileX, tileY]
                        drawTile(
                            canvas,
                            (drawLeftEdge + (x * blockSize)),
                            (drawTopEdge + (y * blockSize)),
                            tile
                        )
                    }
                }
                drawPath(canvas)
                drawStart(canvas)
                drawEnd(canvas)
                drawSpiriter(canvas)
            }
        }

        private fun drawTile(canvas: Canvas, x: Float, y: Float, tile: Int) {
            tileDrawable.draw(canvas, x, y, blockSize, tile)
        }

        private fun drawPath(canvas: Canvas) {
            canvas.withSave {
                val x = (focusBlock.x - (viewportWidth / 2)) * blockSize
                val y = (focusBlock.y - (viewportHeight / 2)) * blockSize
                canvas.translate(x * -1, y * -1)
                pathDrawable.draw(canvas, routePath, blockSize)
            }
        }

        private fun drawSpiriter(canvas: Canvas) {
            spiritDrawable.draw(canvas, bounds.exactCenterX(), bounds.exactCenterY(), blockSize)
        }

        private fun drawStart(canvas: Canvas) {
            val point = startPoint ?: return
            drawExtremePoint(canvas, point, true)
        }

        private fun drawEnd(canvas: Canvas) {
            val point = endPoint ?: return
            drawExtremePoint(canvas, point, false)
        }

        private fun drawExtremePoint(canvas: Canvas, point: MPoint, isStart: Boolean) {
            if (point.x < 0 || point.y < 0) {
                return
            }
            val px = point.x
            val drawBlockRadiusX = viewportWidth / 2
            if (px < (focusBlock.x - drawBlockRadiusX) || px > (focusBlock.x + drawBlockRadiusX)) {
                return
            }
            val py = point.y
            val drawBlockRadiusY = viewportHeight / 2
            if (py < (focusBlock.y - drawBlockRadiusY) || py > (focusBlock.y + drawBlockRadiusY)) {
                return
            }
            val blockOffsetX = ((px - focusBlock.x) * blockSize) + bounds.exactCenterX()
            val blockOffsetY = ((py - focusBlock.y) * blockSize) + bounds.exactCenterY()
            if (isStart) {
                spiritDrawable.drawStart(canvas, blockOffsetX, blockOffsetY, blockSize)
            } else {
                spiritDrawable.drawEnd(canvas, blockOffsetX, blockOffsetY, blockSize)
            }
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

    class ActionBuilder(internal var view: MazePlayView?) {

        var isUpdateSource = false
            private set

        var isUpdatePointer = false
            private set

        var isUpdateDrawable = false
            private set

        var isUpdateProgress = false
            private set

        var isUpdateExtreme = false
            private set

        fun setTileDrawable(tileDrawable: TileDrawable) {
            isUpdateDrawable = true
            view?.setTileDrawable(tileDrawable)
        }

        fun setPathDrawable(pathDrawable: PathDrawable) {
            isUpdateDrawable = true
            view?.setPathDrawable(pathDrawable)
        }

        fun setSpiritDrawable(spiritDrawable: SpiritDrawable) {
            isUpdateDrawable = true
            view?.setSpiritDrawable(spiritDrawable)
        }

        fun setViewportSize(width: Int, height: Int) {
            isUpdateSource = true
            view?.setViewportSize(width, height)
        }

        fun setSource(sourceMap: MMap, path: MPath) {
            isUpdateSource = true
            view?.setSource(sourceMap, path)
        }

        fun setFocus(x: Int, y: Int) {
            val result = view?.setFocus(x, y) ?: false
            if (result) {
                isUpdatePointer = true
            }
        }

        fun updateProgress(progress: Float) {
            isUpdateProgress = true
            view?.updateProgress(progress)
        }

        fun setFrom(x: Int, y: Int) {
            val result = view?.setFrom(x, y) ?: false
            if (result) {
                isUpdatePointer = true
            }
        }

        fun setExtremePoint(start: MPoint?, end: MPoint?) {
            isUpdateExtreme = true
            view?.setExtremePoint(start, end)
        }
    }

}