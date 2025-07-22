package com.lollipop.wear.maze.view.draw

import android.graphics.Canvas

interface SpiritDrawable: MazeDrawable {

    fun draw(canvas: Canvas, x: Float, y: Float, blockSize: Float)

}