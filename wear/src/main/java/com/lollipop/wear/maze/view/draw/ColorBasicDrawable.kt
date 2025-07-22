package com.lollipop.wear.maze.view.draw

import android.graphics.Color
import android.graphics.Paint

abstract class ColorBasicDrawable {

    protected val paint = Paint().apply {
        isAntiAlias = true
        isDither = true
        color = Color.GRAY
    }

    var color: Int
        get() {
            return paint.color
        }
        set(value) {
            paint.color = value
        }

}