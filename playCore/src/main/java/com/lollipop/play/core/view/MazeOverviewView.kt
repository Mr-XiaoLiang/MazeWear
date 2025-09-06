package com.lollipop.play.core.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.lollipop.maze.Maze
import com.lollipop.maze.MazeMap
import com.lollipop.maze.data.MMap
import com.lollipop.maze.data.MPath
import com.lollipop.maze.data.MPoint
import com.lollipop.maze.data.MTreasure
import com.lollipop.play.core.helper.registerLog
import kotlin.math.max

class MazeOverviewView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : AppCompatImageView(context, attributeSet) {

    private var isDisplayMap = false

    private val pathDrawable = OverviewLineDrawable()
    private val mapDrawable = OverviewMapDrawable()

    private val pathOnlyDrawable: Drawable by lazy {
        pathDrawable
    }

    private val mapPathDrawable: Drawable by lazy {
        LayerDrawable(arrayOf(mapDrawable, pathDrawable))
    }

    private val log = registerLog()

    init {
        bindDrawable()
    }

    fun setMap(treasure: MTreasure) {
        setMap(map = treasure.mazeMap, path = treasure.path, hiPath = treasure.hiPath)
    }

    fun setMap(map: MazeMap, path: MPath, hiPath: MPath?) {
        setMap(
            map = map.map,
            path = path,
            hiPath = hiPath,
            startPoint = map.start,
            endPoint = map.end
        )
    }

    fun setMap(map: MMap, path: MPath, hiPath: MPath?, startPoint: MPoint?, endPoint: MPoint?) {
        mapDrawable.setMap(map)
        pathDrawable.setPath(
            width = map.width,
            height = map.height,
            path = path,
            hiPath = hiPath,
            startPoint = startPoint,
            endPoint = endPoint
        )
    }

    fun setMin(lineWidthMin: Float, extremeRadiusMin: Float) {
        pathDrawable.setMin(lineWidthMin, extremeRadiusMin)
    }

    fun setColor(lineColor: Int, extremeStartColor: Int, extremeEndColor: Int, mapColor: Int) {
        pathDrawable.setColor(lineColor, extremeStartColor, extremeEndColor)
        mapDrawable.color = mapColor
    }

    fun setReverseDisplayMap(reverseDisplay: Boolean) {
        mapDrawable.setReverseDisplay(reverseDisplay)
    }

    fun setDisplayMap(display: Boolean) {
        isDisplayMap = display
        bindDrawable()
    }

    private fun bindDrawable() {
        if (isDisplayMap) {
            setImageDrawable(mapPathDrawable)
        } else {
            setImageDrawable(pathOnlyDrawable)
        }
    }

    fun updatePath() {
        mapDrawable.updatePath()
        pathDrawable.updatePath()
    }

    private class OverviewMapDrawable : MazeBasicDrawable() {

        private val paint = Paint().apply {
            isAntiAlias = true
            isDither = true
            style = Paint.Style.FILL
        }

        private var mapWidth = 0
        private var mapHeight = 0
        private var mMap: MMap? = null
        private var mapPath = Path()

        private var isReverseDisplay = true

        var color: Int
            get() {
                return paint.color
            }
            set(value) {
                paint.color = value
            }

        fun setReverseDisplay(reverseDisplay: Boolean) {
            isReverseDisplay = reverseDisplay
            updatePath()
        }

        fun setMap(map: MMap) {
            this.mapWidth = map.width
            this.mapHeight = map.height
            this.mMap = map
            updatePath()
        }

        override fun onBoundsChange(bounds: Rect) {
            super.onBoundsChange(bounds)
            updatePath()
        }

        fun updatePath() {
            if (bounds.isEmpty) {
                return
            }
            updateGrid(mapWidth, mapHeight, bounds.width(), bounds.height())
            mapPath.reset()
            val map = mMap ?: return
            val displayRoad = !isReverseDisplay
            for (x in 0 until mapWidth) {
                for (y in 0 until mapHeight) {
                    val value = map[x, y]
                    if ((value == Maze.ROAD && displayRoad) || (value == Maze.WALL && !displayRoad)) {
                        mapPath.addRect(
                            getX(x),
                            getY(y),
                            getX(x + 1),
                            getY(y + 1),
                            Path.Direction.CW
                        )
                    }
                }
            }
            invalidateSelf()
        }

        override fun draw(canvas: Canvas) {
            canvas.drawPath(mapPath, paint)
        }

        override fun setAlpha(alpha: Int) {
            paint.alpha = alpha
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            paint.colorFilter = colorFilter
        }
    }

    private class OverviewLineDrawable : MazeBasicDrawable() {

        private var extremeRadiusMin = 1F
        private var lineWidthMin = 1F

        private val paint = Paint().apply {
            isAntiAlias = true
            isDither = true
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }

        private var mapWidth = 0
        private var mapHeight = 0
        private var mPath = MPath()
        private var hiPath: MPath? = null
        private var startPoint: MPoint? = null
        private var endPoint: MPoint? = null

        private var extremeStartColor = Color.RED
        private var extremeEndColor = Color.GREEN
        private var lineColor = Color.WHITE

        private var startX = -1F
        private var startY = -1F
        private var endX = -1F
        private var endY = -1F

        private var lineWidth = 1F
        private var extremeRadius = 1F

        private val routePath = Path()

        private val log = registerLog()

        fun setMin(lineWidthMin: Float, extremeRadiusMin: Float) {
            this.lineWidthMin = lineWidthMin
            this.extremeRadiusMin = extremeRadiusMin
            updateSize()
        }

        fun setPath(
            width: Int,
            height: Int,
            path: MPath,
            hiPath: MPath?,
            startPoint: MPoint?,
            endPoint: MPoint?
        ) {
            this.mapWidth = width
            this.mapHeight = height
            this.mPath = path
            this.hiPath = hiPath
            this.startPoint = startPoint
            this.endPoint = endPoint
            updateSize()
            updatePath()
        }

        fun setColor(lineColor: Int, extremeStartColor: Int, extremeEndColor: Int) {
            this.lineColor = lineColor
            this.extremeStartColor = extremeStartColor
            this.extremeEndColor = extremeEndColor
            invalidateSelf()
        }

        override fun onBoundsChange(bounds: Rect) {
            super.onBoundsChange(bounds)
            updatePath()
            updateSize()
        }

        private fun updateSize() {
            if (bounds.isEmpty) {
                return
            }
            if (mapWidth < 1 || mapHeight < 1) {
                return
            }
            updateGrid(mapWidth, mapHeight, bounds.width(), bounds.height())
            lineWidth = max(lineWidthMin, (blockSize / 3))
            extremeRadius = max(extremeRadiusMin, blockSize * 0.5F)
            invalidateSelf()
        }

        fun updatePath() {
            if (bounds.isEmpty) {
                return
            }
            updateGrid(mapWidth, mapHeight, bounds.width(), bounds.height())

            val startP = startPoint
            if (startP != null) {
                startX = getXWithHalfBlock(startP.x)
                startY = getYWithHalfBlock(startP.y)
            } else {
                startX = -1F
                startY = -1F
            }

            val endP = endPoint
            if (endP != null) {
                endX = getXWithHalfBlock(endP.x)
                endY = getYWithHalfBlock(endP.y)
            } else {
                endX = -1F
                endY = -1F
            }
            routePath.reset()
            val pointList = mPath.pointList
            for (i in pointList.indices) {
                val point = pointList[i]
                val x = getXWithHalfBlock(point.x)
                val y = getXWithHalfBlock(point.y)
                if (i == 0) {
                    routePath.moveTo(x, y)
                } else {
                    routePath.lineTo(x, y)
                }
            }
            invalidateSelf()
        }

        override fun draw(canvas: Canvas) {
            drawPath(canvas)
            drawPoint(canvas)
        }

        private fun drawPath(canvas: Canvas) {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = lineWidth
            paint.color = lineColor
            canvas.drawPath(routePath, paint)
        }

        private fun drawPoint(canvas: Canvas) {
            if (startX >= 0 && startY >= 0) {
                paint.style = Paint.Style.FILL
                paint.color = extremeStartColor
                canvas.drawCircle(startX, startY, extremeRadius, paint)
            }
            if (endX >= 0 && endY >= 0) {
                paint.style = Paint.Style.FILL
                paint.color = extremeEndColor
                canvas.drawCircle(endX, endY, extremeRadius, paint)
            }
        }

        override fun setAlpha(alpha: Int) {
            paint.alpha = alpha
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            paint.colorFilter = colorFilter
        }

    }

}