package com.lollipop.play.core.helper

import com.lollipop.maze.helper.ThreadHelper
import com.lollipop.play.core.MazePlayConfig
import com.lollipop.play.core.view.JoystickView
import kotlin.math.abs

class JoystickDelegate(
    private val callback: (JoystickDirection) -> Unit
) : JoystickView.OnJoystickTouchListener {

    private var lastMoveTime = 0L
    private var delayTime = 0L

    private var currentDirection: JoystickDirection? = null

    private var isTouching = false

    private val touchTask = ThreadHelper.safedTask {
        onKeepTouch()
    }

    fun bind(view: JoystickView) {
        view.setJoystickTouchListener(this)
    }

    private fun now(): Long {
        return System.currentTimeMillis()
    }

    override fun onMove(
        view: JoystickView,
        angle: Float,
        radius: Float,
        centerX: Float,
        centerY: Float,
        touchX: Float,
        touchY: Float,
        isTouchMode: Boolean
    ) {
        if (!isTouchMode) {
            if (abs(touchX) < 0.5 && abs(touchY) < 0.5) {
                return
            }
        }
        lastMoveTime = now()
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
        val delay = MazePlayConfig.moveJoystickDuration
        delayTime = delay
        touchTask.postDelay(delay)
    }

    private fun onKeepTouch() {
        val now = now()
        if (now - lastMoveTime < delayTime) {
            postTouchTask()
        } else {
            isTouching = false
        }
        val direction = currentDirection
        if (direction != null) {
            callback.invoke(direction)
        }
    }

}