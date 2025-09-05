package com.lollipop.wear.maze.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max

class CircularScrollIndicator @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : AppCompatImageView(context, attributeSet) {

    private val scrollListener = ScrollListener(::onTargetScroll)

    private val scrollBarDrawable = ScrollBarDrawable()

    var thumbBackground: Int
        set(value) {
            scrollBarDrawable.backgroundColor = value
        }
        get() {
            return scrollBarDrawable.backgroundColor
        }
    var thumbColor: Int
        set(value) {
            scrollBarDrawable.thumbColor = value
        }
        get() {
            return scrollBarDrawable.thumbColor
        }

    var thumbWidth: Float
        set(value) {
            scrollBarDrawable.thumbWidth = value
        }
        get() {
            return scrollBarDrawable.thumbWidth
        }

    var backgroundWidth: Float
        set(value) {
            scrollBarDrawable.backgroundWidth = value
        }
        get() {
            return scrollBarDrawable.backgroundWidth
        }

    var startAngle: Float
        set(value) {
            scrollBarDrawable.startAngle = value
        }
        get() {
            return scrollBarDrawable.startAngle
        }
    var sweepAngle: Float
        set(value) {
            scrollBarDrawable.sweepAngle = value
        }
        get() {
            return scrollBarDrawable.sweepAngle
        }

    init {
        setImageDrawable(scrollBarDrawable)
    }

    fun attachTo(scrollView: RecyclerView) {
        scrollView.addOnScrollListener(scrollListener)
    }

    private fun onTargetScroll(scrollView: RecyclerView) {
        val scrollOffset = scrollView.computeVerticalScrollOffset()
        val scrollRange = scrollView.computeVerticalScrollRange()
        val scrollExtent = scrollView.computeVerticalScrollExtent()
        val offset = scrollOffset.toFloat() / (scrollRange - scrollExtent)
        onScrollChanged(offset, scrollExtent.toFloat() / scrollRange)
    }

    private fun onScrollChanged(offset: Float, extent: Float) {
        scrollBarDrawable.thumbWeight = extent
        scrollBarDrawable.thumbPosition = offset
        invalidate()
    }

    private class ScrollListener(
        private val onScroll: (RecyclerView) -> Unit
    ) : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            onViewScroll(recyclerView)
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            onViewScroll(recyclerView)
        }

        private fun onViewScroll(scrollView: RecyclerView) {
            onScroll(scrollView)
        }
    }

    private class ScrollBarDrawable : Drawable() {

        private val paint = Paint().apply {
            isAntiAlias = true
            isDither = true
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }

        var backgroundColor: Int = Color.TRANSPARENT
        var thumbColor: Int = Color.WHITE

        var thumbWidth: Float = 0F
            set(value) {
                field = value
                updateArcBounds()
            }
        var backgroundWidth: Float = 0F
            set(value) {
                field = value
                updateArcBounds()
            }

        var startAngle: Float = 0F
        var sweepAngle: Float = 90F

        var thumbWeight: Float = 0.5F
        var thumbPosition: Float = 0F

        private val arcBounds = RectF()

        override fun onBoundsChange(bounds: Rect) {
            super.onBoundsChange(bounds)
            updateArcBounds()
        }

        private fun updateArcBounds() {
            if (bounds.isEmpty) {
                return
            }
            val halfWidth = max(backgroundWidth, thumbWidth) * 0.5F
            arcBounds.set(
                bounds.left + halfWidth,
                bounds.top + halfWidth,
                bounds.right - halfWidth,
                bounds.bottom - halfWidth
            )
        }

        override fun draw(canvas: Canvas) {
            if (backgroundColor != Color.TRANSPARENT) {
                paint.color = backgroundColor
                paint.strokeWidth = backgroundWidth
                canvas.drawArc(arcBounds, startAngle, sweepAngle, false, paint)
            }
            if (thumbColor != Color.TRANSPARENT) {
                paint.color = thumbColor
                paint.strokeWidth = thumbWidth
                val sweep = sweepAngle * thumbWeight
                val start = startAngle + ((sweepAngle - sweep) * thumbPosition)
                canvas.drawArc(arcBounds, start, sweep, false, paint)
            }
        }

        override fun getOpacity(): Int {
            return PixelFormat.TRANSPARENT
        }

        override fun setAlpha(alpha: Int) {
            paint.alpha = alpha
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            paint.colorFilter = colorFilter
        }

    }

}