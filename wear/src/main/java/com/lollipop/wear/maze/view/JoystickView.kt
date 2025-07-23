package com.lollipop.wear.maze.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.util.AttributeSet
import android.widget.ImageView

class JoystickView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : ImageView(context, attributeSet) {



    private class JoystickDrawable : MazeBasicDrawable() {

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