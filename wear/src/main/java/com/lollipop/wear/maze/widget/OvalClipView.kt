package com.lollipop.wear.maze.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.widget.FrameLayout

class OvalClipView @JvmOverloads constructor(
    context: Context, attributeSet: android.util.AttributeSet? = null
) : FrameLayout(context, attributeSet) {

    private val clipPath = Path()
    private val paint = android.graphics.Paint().apply {
        isAntiAlias = true
        isDither = true
        style = android.graphics.Paint.Style.FILL
    }

    var color: Int
        get() {
            return paint.color
        }
        set(value) {
            paint.color = value
            invalidate()
        }

    private fun buildClipPath() {
        clipPath.reset()
        clipPath.addOval(
            paddingLeft.toFloat(),
            paddingTop.toFloat(),
            width - paddingRight.toFloat(),
            height - paddingBottom.toFloat(),
            Path.Direction.CW
        )
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.drawPath(clipPath, paint)
        canvas.clipPath(clipPath)
        super.dispatchDraw(canvas)
    }

    override fun onDraw(canvas: Canvas) {
        buildClipPath()
        super.draw(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        buildClipPath()
    }

}