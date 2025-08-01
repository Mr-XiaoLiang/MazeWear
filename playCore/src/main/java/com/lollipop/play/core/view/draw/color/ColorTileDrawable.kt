package com.lollipop.play.core.view.draw.color

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.lollipop.maze.Maze
import com.lollipop.play.core.view.draw.TileDrawable
import com.lollipop.play.core.view.draw.TileMap

class ColorTileDrawable : ColorBasicDrawable(), TileDrawable {

    init {
        paint.style = Paint.Style.FILL
    }

    private val tileBounds = RectF()

    override fun draw(
        canvas: Canvas,
        x: Float,
        y: Float,
        tileSize: Float,
        tile: Int
    ) {
        if (tile != TileMap.TILE_EMPTY && tile != Maze.EMPTY) {
            val radius = tileSize * 0.125F
            val tileWidth = tileSize * 0.95F
            val offset = (tileSize - tileWidth) / 2
            tileBounds.set(0F, 0F, tileWidth, tileWidth)
            tileBounds.offset(offset, offset)
            tileBounds.offset(x, y)
            canvas.drawRoundRect(tileBounds, radius, radius, paint)
        }
    }

}