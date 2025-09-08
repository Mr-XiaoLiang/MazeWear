package com.lollipop.play.core.view.draw.color

import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import com.lollipop.play.core.helper.registerLog
import com.lollipop.play.core.view.draw.MazeDrawable

abstract class ColorBasicDrawable : MazeDrawable {

    protected val paint = Paint().apply {
        isAntiAlias = true
        isDither = true
        color = Color.GRAY
    }

    private val log = registerLog()

    var color: Int
        get() {
            return paint.color
        }
        set(value) {
            log("setColor: ${value.toUInt().toString(16).uppercase()}")
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