package com.lollipop.wear.blocksbuilding.view

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable


sealed class ViewBackground(val shape: ViewShape) : Drawable() {

    protected var shapePath: Path? = null

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        shapePath = shape.path(bounds)
        invalidateSelf()
    }

    protected fun clipByShape(canvas: Canvas) {
        shapePath?.let {
            canvas.clipPath(it)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int {
        return PixelFormat.TRANSPARENT
    }

    override fun setAlpha(alpha: Int) {

    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }

    class ByColor(val color: Int, shape: ViewShape) : ViewBackground(shape) {

        private val paint = Paint().apply {
            style = Paint.Style.FILL
            isDither = true
            isAntiAlias = true
            color = this@ByColor.color
        }

        override fun draw(canvas: Canvas) {
            shapePath?.let {
                canvas.drawPath(it, paint)
            }
        }

        override fun setAlpha(alpha: Int) {
            super.setAlpha(alpha)
            paint.alpha = alpha
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            super.setColorFilter(colorFilter)
            paint.colorFilter = colorFilter
        }

    }

    class ByShader(val shader: Shader, shape: ViewShape) : ViewBackground(shape) {

        private val paint = Paint().apply {
            style = Paint.Style.FILL
            isDither = true
            isAntiAlias = true
            shader = this@ByShader.shader
        }

        override fun draw(canvas: Canvas) {
            shapePath?.let {
                canvas.drawPath(it, paint)
            }
        }

        override fun setAlpha(alpha: Int) {
            super.setAlpha(alpha)
            paint.alpha = alpha
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            super.setColorFilter(colorFilter)
            paint.colorFilter = colorFilter
        }
    }

}

open class DrawableWrapper(
    val shape: ViewShape, vararg drawable: Drawable
) : LayerDrawable(drawable) {

    protected var shapePath: Path? = null

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        shapePath = shape.path(bounds)
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {
        shapePath?.let {
            canvas.clipPath(it)
        }
        super.draw(canvas)
    }

}
