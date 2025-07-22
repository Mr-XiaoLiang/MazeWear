package com.lollipop.wear.maze.view.draw

import android.graphics.Canvas
import android.graphics.Path

interface PathDrawable {

    fun draw(canvas: Canvas, path: Path, blockSize: Float)

}