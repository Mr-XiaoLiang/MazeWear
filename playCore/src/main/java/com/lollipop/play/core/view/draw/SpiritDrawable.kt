package com.lollipop.play.core.view.draw

import android.graphics.Canvas

interface SpiritDrawable : MazeDrawable {

    fun draw(canvas: Canvas, x: Float, y: Float, blockSize: Float)

    fun drawStart(canvas: Canvas, x: Float, y: Float, blockSize: Float)

    fun drawEnd(canvas: Canvas, x: Float, y: Float, blockSize: Float)

}