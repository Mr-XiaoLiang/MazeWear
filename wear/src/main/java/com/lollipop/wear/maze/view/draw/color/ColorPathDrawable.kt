package com.lollipop.wear.maze.view.draw.color

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.lollipop.wear.maze.view.draw.PathDrawable

class ColorPathDrawable : ColorBasicDrawable(), PathDrawable {

    private var widthWeight = 0.3F

    init {
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeJoin = Paint.Join.ROUND
    }

    fun updateWidthWeight(weight: Float) {
        widthWeight = weight
    }

    override fun draw(
        canvas: Canvas,
        path: Path,
        blockSize: Float
    ) {
        paint.strokeWidth = widthWeight * blockSize
        canvas.drawPath(path, paint)
    }

}