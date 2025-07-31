package com.lollipop.wear.maze.helper

import com.lollipop.maze.helper.ThreadHelper
import com.lollipop.wear.maze.view.JoystickView

class JoystickDelegate(
    private val callback: (Direction) -> Unit
) : JoystickView.OnJoystickTouchListener {

    private var currentDirection: Direction? = null

    private var isTouching = false

    private val touchTask = ThreadHelper.safedTask {
        onKeepTouch()
    }

    override fun onTouch(
        view: JoystickView,
        angle: Float,
        radius: Float,
        centerX: Float,
        centerY: Float,
        touchX: Float,
        touchY: Float
    ) {
        val direction = angleToDirection(angle)
        currentDirection = direction
        if (!isTouching) {
            isTouching = true
            postTouchTask()
            if (direction != null) {
                callback.invoke(direction)
            }
        }
    }

    private fun angleToDirection(angle: Float): Direction? {
        return when {
            angle >= 0 && angle < 45 -> Direction.UP
            angle >= 45 && angle < 135 -> Direction.RIGHT
            angle >= 135 && angle < 225 -> Direction.DOWN
            angle >= 225 && angle < 315 -> Direction.LEFT
            angle >= 315 && angle <= 360 -> Direction.UP
            else -> null
        }
    }

    override fun onTouchUp(view: JoystickView) {
        isTouching = false
        touchTask.remove()
    }

    private fun postTouchTask() {
        touchTask.postDelay(200)
    }

    private fun onKeepTouch() {
        if (isTouching) {
            postTouchTask()
        }
        val direction = currentDirection
        if (direction != null) {
            callback.invoke(direction)
        }
    }


    enum class Direction {
        LEFT,
        RIGHT,
        UP,
        DOWN
    }

}