package com.lollipop.wear.maze.view.draw

import android.graphics.Canvas
import android.graphics.Path

interface PathDrawable : MazeDrawable {

    fun draw(canvas: Canvas, path: Path, blockSize: Float)

}