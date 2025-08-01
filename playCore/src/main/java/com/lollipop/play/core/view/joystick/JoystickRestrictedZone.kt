package com.lollipop.play.core.view.joystick

import com.lollipop.play.core.view.JoystickView
import kotlin.math.max
import kotlin.math.min

class JoystickRectRestrictedZone(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int
) : JoystickView.RestrictedZone {

    companion object {
        const val POSITION_PARENT_LEFT = -1
        const val POSITION_PARENT_TOP = -2
        const val POSITION_PARENT_RIGHT = -3
        const val POSITION_PARENT_BOTTOM = -4
        const val POSITION_PARENT_CENTER = -5

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
        value: Int,
        isHorizontal: Boolean
    ): Int {
        when (value) {
            POSITION_PARENT_LEFT -> {
                return 0
            }

            POSITION_PARENT_TOP -> {
                return 0
            }

            POSITION_PARENT_RIGHT -> {
                return viewWidth
            }

            POSITION_PARENT_BOTTOM -> {
                return viewHeight
            }

            POSITION_PARENT_CENTER -> {
                return if (isHorizontal) {
                    viewWidth / 2
                } else {
                    viewHeight / 2
                }
            }

            else -> {
                return value
            }
        }
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
    val outerEdgeWeight: Float,
    val innerEdgeWeight: Float,
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