package com.lollipop.wear.maze.view.draw.color

import android.graphics.Canvas
import android.graphics.Paint
import com.lollipop.wear.maze.view.draw.SpiritDrawable

class ColorSpiritDrawable : ColorBasicDrawable(), SpiritDrawable {

    private var widthWeight = 0.8F

    init {
        paint.style = Paint.Style.FILL
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
        canvas.drawCircle(x, y, blockSize * 0.5F * widthWeight, paint)
    }

}