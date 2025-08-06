package com.lollipop.play.core.view.joystick

import com.lollipop.play.core.view.JoystickView
import kotlin.math.max
import kotlin.math.min

class JoystickRectRestrictedZone(
    var left: Edge,
    var top: Edge,
    var right: Edge,
    var bottom: Edge
) : JoystickView.RestrictedZone {

    companion object {
        val START_EDGE = Edge.Relative(0F)
        val END_EDGE = Edge.Relative(1F)
        val PARENT_CENTER = Edge.Relative(0.5F)
    }

    override fun isTouchOnRestricted(
        viewWidth: Int,
        viewHeight: Int,
        x: Float,
        y: Float
    ): Boolean {
        val l = getRestrictedPosition(viewWidth, viewHeight, left, true)
        val t = getRestrictedPosition(viewWidth, viewHeight, top, false)
        val r = getRestrictedPosition(viewWidth, viewHeight, right, true)
        val b = getRestrictedPosition(viewWidth, viewHeight, bottom, false)
        return x >= l && x <= r && y >= t && y <= b
    }

    private fun getRestrictedPosition(
        viewWidth: Int,
        viewHeight: Int,
        value: Edge,
        isHorizontal: Boolean
    ): Int {
        when (value) {
            is Edge.Absolute -> {
                return value.edge
            }

            is Edge.Relative -> {
                val weight = value.edge
                return if (isHorizontal) {
                    (weight * viewWidth).toInt()
                } else {
                    (weight * viewHeight).toInt()
                }
            }
        }
    }

    sealed class Edge {

        class Absolute(val edge: Int) : Edge()

        class Relative(val edge: Float) : Edge()

    }

}


/**
 * 圆，特定的角度
 */
class JoystickCircleRestrictedZone(
    val radiusFrom: Int,
    val radiusTo: Int,
) : JoystickView.RestrictedZone {

    override fun isTouchOnRestricted(
        viewWidth: Int,
        viewHeight: Int,
        x: Float,
        y: Float
    ): Boolean {
        val centerX = viewWidth * 0.5F
        val centerY = viewHeight * 0.5F
        val radius = JoystickView.getLength(centerX, centerY, x, y)
        return radius >= radiusFrom && radius <= radiusTo
    }

}

/**
 * 圆环
 * @param outerEdgeWeight 外侧边缘的权重，范围为0~1，当设置为0时候，表示位置在圆心，当设置为1时，表示位置在边缘
 * @param innerEdgeWeight 内侧边缘的权重，范围为0~1，当设置为0时候，表示位置在圆心，当设置为1时，表示位置在边缘
 * @param insideMode 内侧模式，当设置为true时，表示以短边为直径，否则为长边为直径
 */
class JoystickRingRestrictedZone(
    var outerEdgeWeight: Float,
    var innerEdgeWeight: Float,
    val insideMode: Boolean,
) : JoystickView.RestrictedZone {

    override fun isTouchOnRestricted(
        viewWidth: Int,
        viewHeight: Int,
        x: Float,
        y: Float
    ): Boolean {
        val maxRadius = if (insideMode) {
            min(viewWidth, viewHeight) / 2
        } else {
            max(viewWidth, viewHeight) / 2
        }
        val radiusTo = maxRadius * outerEdgeWeight
        val radiusFrom = radiusTo * innerEdgeWeight
        val centerX = viewWidth * 0.5F
        val centerY = viewHeight * 0.5F
        val radius = JoystickView.getLength(centerX, centerY, x, y)
        return !(radius >= radiusFrom && radius <= radiusTo)
    }
}