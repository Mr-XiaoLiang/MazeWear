package com.lollipop.play.core.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Region
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.withSave
import kotlin.math.min

class MazeFogView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : AppCompatImageView(context, attributeSet) {

    private val fogDrawable = FogDrawable()

    init {
        setImageDrawable(fogDrawable)
    }

    fun setViewportWeight(weight: Float) {
        fogDrawable.setViewportWeight(weight)
    }

    private class FogDrawable : Drawable() {

        private var viewportWeight = 0.7F

        private val paint = Paint().apply {
            isAntiAlias = true
            isDither = true
        }

        private var shaderRadius = 0F
        private var shaderBounds = RectF()

        private val clipPath = Path()

        fun setViewportWeight(weight: Float) {
            viewportWeight = weight
            updateShape()
        }

        override fun onBoundsChange(bounds: Rect) {
            super.onBoundsChange(bounds)
            updateShape()
        }

        private fun updateShape() {
            if (bounds.isEmpty) {
                return
            }
            shaderRadius = min(bounds.width(), bounds.height()) * 0.5F
            val centerX = bounds.exactCenterX()
            val centerY = bounds.exactCenterY()
            shaderBounds.set(
                centerX - shaderRadius,
                centerY - shaderRadius,
                centerX + shaderRadius,
                centerY + shaderRadius
            )
            clipPath.addCircle(centerX, centerY, shaderRadius - 2, Path.Direction.CW)
            paint.shader = RadialGradient(
                centerX,
                centerY,
                shaderRadius,
                intArrayOf(Color.TRANSPARENT, Color.BLACK),
                floatArrayOf(viewportWeight, 1F),
                Shader.TileMode.CLAMP
            )
            invalidateSelf()
        }

        override fun draw(canvas: Canvas) {
            canvas.withSave {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    canvas.clipOutPath(clipPath)
                } else {
                    canvas.clipPath(clipPath, Region.Op.DIFFERENCE)
                }
                canvas.drawColor(Color.BLACK)
            }
            canvas.drawRect(shaderBounds, paint)
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