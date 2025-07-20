package com.lollipop.wear.maze.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.util.AttributeSet
import android.widget.ImageView
import com.lollipop.maze.data.MBlock
import com.lollipop.maze.data.MMap

class MazePlayView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : ImageView(context, attributeSet) {


    private class MapDrawable : MazeBasicDrawable() {

        private var viewportWidth = 5
        private var viewportHeight = 5
        private var mapWidth = 0
        private var mapHeight = 0
        private var sourceMap: MMap? = null

        private var focusBlock = MBlock(x = -1, y = -1)
        private var nextBlock = MBlock(x = -1, y = -1)

        fun setViewportSize(width: Int, height: Int) {
            viewportWidth = width
            viewportHeight = height
            updateViewportMap()
        }

        fun setSource(sourceMap: MMap) {
            this.sourceMap = sourceMap
            mapWidth = sourceMap.width
            mapHeight = sourceMap.height
            updateViewportMap()
        }

        fun setFocus(x: Int, y: Int) {
            focusBlock.set(x, y)
            updateViewportMap()
        }

        fun setNext(x: Int, y: Int) {
            nextBlock.set(x, y)
            updateViewportMap()
        }

        private fun updateViewportMap() {
            // TODO
        }

        override fun draw(canvas: Canvas) {
            TODO("Not yet implemented")
        }

        override fun setAlpha(alpha: Int) {
            TODO("Not yet implemented")
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            TODO("Not yet implemented")
        }
    }

}