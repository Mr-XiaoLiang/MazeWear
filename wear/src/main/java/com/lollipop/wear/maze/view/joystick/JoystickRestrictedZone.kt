package com.lollipop.wear.maze.view.joystick

import com.lollipop.wear.maze.view.JoystickView
import com.lollipop.wear.maze.view.JoystickView.RestrictedZone
import kotlin.math.max
import kotlin.math.min

sealed class JoystickRestrictedZone: RestrictedZone {

    companion object {
        const val POSITION_PARENT_LEFT = -1
        const val POSITION_PARENT_TOP = -2
        const val POSITION_PARENT_RIGHT = -3
        const val POSITION_PARENT_BOTTOM = -4
        const val POSITION_PARENT_CENTER = -5

    }

    /**
     * 矩形
     */
    class Rect(
        val left: Int,
        val top: Int,
        val right: Int,
        val bottom: Int
    ) : JoystickRestrictedZone() {

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
    class Circle(
        val radiusFrom: Int,
        val radiusTo: Int,
    ) : JoystickRestrictedZone() {

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
     */
    class Annulus(
        val width: Int,
        val offset: Int,
        val insideMode: Boolean,
    ) : JoystickRestrictedZone() {

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
            val radiusTo = maxRadius - offset
            val radiusFrom = radiusTo - width
            val centerX = viewWidth * 0.5F
            val centerY = viewHeight * 0.5F
            val radius = JoystickView.getLength(centerX, centerY, x, y)
            return radius >= radiusFrom && radius <= radiusTo
        }

    }
}