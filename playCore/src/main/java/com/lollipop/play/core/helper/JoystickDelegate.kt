package com.lollipop.play.core.helper

import com.lollipop.maze.helper.ThreadHelper
import com.lollipop.play.core.MazePlayConfig
import com.lollipop.play.core.view.JoystickView

class JoystickDelegate(
    private val callback: (JoystickDirection) -> Unit
) : JoystickView.OnJoystickTouchListener {

    private var currentDirection: JoystickDirection? = null

    private var isTouching = false

    private val touchTask = ThreadHelper.safedTask {
        onKeepTouch()
    }

    fun bind(view: JoystickView) {
        view.setJoystickTouchListener(this)
    }

    override fun onMove(
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

    private fun angleToDirection(angle: Float): JoystickDirection? {
        return when {
            angle >= 0 && angle < 45 -> JoystickDirection.UP
            angle >= 45 && angle < 135 -> JoystickDirection.RIGHT
            angle >= 135 && angle < 225 -> JoystickDirection.DOWN
            angle >= 225 && angle < 315 -> JoystickDirection.LEFT
            angle >= 315 && angle <= 360 -> JoystickDirection.UP
            else -> null
        }
    }

    override fun onTouchUp(view: JoystickView) {
        isTouching = false
        touchTask.remove()
    }

    private fun postTouchTask() {
        touchTask.postDelay(MazePlayConfig.moveJoystickDuration)
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

}