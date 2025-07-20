package com.lollipop.wear.maze.view

import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import kotlin.math.min

abstract class MazeBasicDrawable : Drawable() {

    protected var blockSize = 0F
    protected var offsetX = 0F
    protected var offsetY = 0F

    override fun getOpacity(): Int {
        return PixelFormat.TRANSPARENT
    }

    protected fun updateGrid(mapWidth: Int, mapHeight: Int, viewWidth: Int, viewHeight: Int) {
        blockSize = min(viewWidth * 1F / mapWidth, viewHeight * 1F / mapHeight)
        offsetX = (viewWidth - (mapWidth * blockSize)) / 2
        offsetY = (viewHeight - (mapHeight * blockSize)) / 2
    }

    protected fun getX(x: Int): Float {
        return offsetX + (x * blockSize)
    }

    protected fun getY(y: Int): Float {
        return offsetY + (y * blockSize)
    }

    protected fun getXWithHalfBlock(x: Int): Float {
        return offsetX + (x * blockSize) + (blockSize / 2)
    }

    protected fun getYWithHalfBlock(y: Int): Float {
        return offsetY + (y * blockSize) + (blockSize / 2)
    }

}