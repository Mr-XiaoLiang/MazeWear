package com.lollipop.wear.maze.view.draw.color

import android.graphics.Canvas
import android.graphics.Paint
import com.lollipop.wear.maze.view.draw.TileDrawable

class ColorTileDrawable : ColorBasicDrawable(), TileDrawable {

    init {
        paint.style = Paint.Style.FILL
    }

    override fun draw(
        canvas: Canvas,
        x: Float,
        y: Float,
        tileSize: Float,
        tile: Int
    ) {
        canvas.drawRect(x, y, x + tileSize, y + tileSize, paint)
    }

}