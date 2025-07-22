package com.lollipop.wear.maze.view.draw

import android.graphics.Canvas
import android.graphics.Paint

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