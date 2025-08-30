package com.lollipop.play.core.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.withRotation

class CircularProgressView @JvmOverloads constructor(
    context: Context, attributeSet: AttributeSet? = null
) : AppCompatImageView(context, attributeSet) {

    private val progressDrawable = CircularProgressDrawable()

    init {
        setImageDrawable(progressDrawable)
    }

    var progress: Float
        set(value) {
            progressDrawable.progress = value
        }
        get() {
            return progressDrawable.progress
        }

    var color: Int
        get() {
            return progressDrawable.color
        }
        set(value) {
            progressDrawable.color = value
        }

    var strokeWidth: Float
        get() {
            return progressDrawable.strokeWidth
        }
        set(value) {
            progressDrawable.strokeWidth = value
        }

    private class CircularProgressDrawable : Drawable() {

        private val paint = Paint().apply {
            style = Paint.Style.STROKE
            isDither = true
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
        }

        var progress: Float = 0F
            set(value) {
                field = value
                invalidateSelf()
            }

        var color: Int
            get() {
                return paint.color
            }
            set(value) {
                paint.color = value
            }

        var strokeWidth: Float
            get() {
                return paint.strokeWidth
            }
            set(value) {
                paint.strokeWidth = value
            }

        private val progressRect = RectF()

        override fun onBoundsChange(bounds: Rect) {
            super.onBoundsChange(bounds)
            val padding = strokeWidth * 0.5F
            progressRect.set(
                bounds.left + padding,
                bounds.top + padding,
                bounds.right - padding,
                bounds.bottom - padding
            )
            invalidateSelf()
        }

        override fun draw(canvas: Canvas) {
            canvas.withRotation(-90F, progressRect.centerX(), progressRect.centerY()) {
                canvas.drawArc(progressRect, 0F, progress * 360F, false, paint)
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