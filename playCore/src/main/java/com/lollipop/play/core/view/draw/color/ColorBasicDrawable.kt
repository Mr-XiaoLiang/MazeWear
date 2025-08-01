package com.lollipop.play.core.view.draw.color

import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import com.lollipop.play.core.view.draw.MazeDrawable

abstract class ColorBasicDrawable : MazeDrawable {

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

    override fun setAlpha(alpha: Int) {
        super.setAlpha(alpha)
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        super.setColorFilter(colorFilter)
        paint.colorFilter = colorFilter
    }

}