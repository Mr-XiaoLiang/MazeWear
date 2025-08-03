package com.lollipop.play.core

import kotlin.math.min

object MazePlayConfig {

    private var moveAnimationDurationValue: Long = 200L
    private var moveJoystickDurationValue: Long = 200L

    fun setMoveAnimationDuration(duration: Long) {
        moveAnimationDurationValue = duration
    }

    fun setMoveJoystickDuration(duration: Long) {
        moveJoystickDurationValue = duration
    }

    val moveAnimationDuration: Long
        get() {
            return min(moveAnimationDurationValue, moveJoystickDurationValue)
        }

    val moveJoystickDuration: Long
        get() {
            return moveJoystickDurationValue
        }

}