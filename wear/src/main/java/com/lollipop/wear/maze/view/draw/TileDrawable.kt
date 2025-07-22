package com.lollipop.wear.maze.view.draw

import android.graphics.Canvas

interface TileDrawable: MazeDrawable {

    fun draw(canvas: Canvas, x: Float, y: Float, tileSize: Float, tile: Int)

}