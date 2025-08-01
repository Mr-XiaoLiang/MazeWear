package com.lollipop.play.core.view.draw

import android.graphics.Canvas
import android.graphics.Path

interface PathDrawable : MazeDrawable {

    fun draw(canvas: Canvas, path: Path, blockSize: Float)

}