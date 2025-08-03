package com.lollipop.play.core.view.draw.color

import android.graphics.Canvas
import android.graphics.Paint
import com.lollipop.play.core.helper.registerLog
import com.lollipop.play.core.view.draw.SpiritDrawable

class ColorSpiritDrawable : ColorBasicDrawable(), SpiritDrawable {

    private var widthWeight = 0.8F

    private var startPaint: Paint? = null
    private var endPaint: Paint? = null

    private val log = registerLog()

    init {
        paint.style = Paint.Style.FILL
    }

    fun setStartColor(color: Int) {
        startPaint = Paint().apply {
            this.color = color
            isAntiAlias = true
            isDither = true
            style = Paint.Style.FILL
        }
    }

    fun setEndColor(color: Int) {
        endPaint = Paint().apply {
            this.color = color
            isAntiAlias = true
            isDither = true
            style = Paint.Style.FILL
        }
    }

    fun updateWidthWeight(weight: Float) {
        widthWeight = weight
    }

    override fun draw(
        canvas: Canvas,
        x: Float,
        y: Float,
        blockSize: Float
    ) {
        drawPoint(canvas, x, y, blockSize, paint)
    }

    override fun drawStart(
        canvas: Canvas,
        x: Float,
        y: Float,
        blockSize: Float
    ) {
        drawPoint(canvas, x, y, blockSize, startPaint ?: paint)
    }

    override fun drawEnd(
        canvas: Canvas,
        x: Float,
        y: Float,
        blockSize: Float
    ) {
        drawPoint(canvas, x, y, blockSize, endPaint ?: paint)
    }

    private fun drawPoint(canvas: Canvas, x: Float, y: Float, blockSize: Float, p: Paint) {
        canvas.drawCircle(x, y, blockSize * 0.5F * widthWeight, p)
    }

}