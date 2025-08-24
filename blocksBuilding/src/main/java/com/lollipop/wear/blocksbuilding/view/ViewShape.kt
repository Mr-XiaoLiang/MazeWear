package com.lollipop.wear.blocksbuilding.view

import android.graphics.Path
import android.graphics.Rect
import com.lollipop.wear.blocksbuilding.item.ViewTypedValue


interface ViewShape {
    fun path(bounds: Rect): Path
}

class RoundRectShape(val radius: ViewTypedValue) : ViewShape {
    override fun path(bounds: Rect): Path {
        val path = Path()
        val r = radius.getValue(bounds).toFloat()
        path.addRoundRect(
            bounds.left.toFloat(),
            bounds.top.toFloat(),
            bounds.right.toFloat(),
            bounds.bottom.toFloat(),
            r,
            r,
            Path.Direction.CW
        )
        return path
    }
}

class OvalShape : ViewShape {
    override fun path(bounds: Rect): Path {
        val path = Path()
        path.addOval(
            bounds.left.toFloat(),
            bounds.top.toFloat(),
            bounds.right.toFloat(),
            bounds.bottom.toFloat(),
            Path.Direction.CW
        )
        return path
    }
}
