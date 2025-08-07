package com.lollipop.play.core.view.joystick

class GenericMotionJoystickDisplayHelper(
    private val callback: Callback
) {

    companion object {
        private const val KEEP_DURATION_GAMEPAD = 50L
    }

    private var lastGamepadEventTime = 0L

    private val delayHideWithGamepad = Runnable {
        if (isKeepGamepad(now())) {
            postToHideWithGamepad()
        } else {
            hideWithGamepad()
        }
    }

    private fun now(): Long {
        return System.currentTimeMillis()
    }

    private fun isKeepGamepad(time: Long): Boolean {
        return time - lastGamepadEventTime < KEEP_DURATION_GAMEPAD
    }

    private fun postToHideWithGamepad() {
        callback.postDelayed(delayHideWithGamepad, KEEP_DURATION_GAMEPAD)
    }

    private fun showWithGamepad() {
        callback.onShowDisplay()
    }

    private fun hideWithGamepad() {
        callback.onHideDisplay()
    }

    fun onGamepadEvent() {
        val time = now()
        if (!isKeepGamepad(time)) {
            showWithGamepad()
            postToHideWithGamepad()
        }
        lastGamepadEventTime = time
    }

    interface Callback {

        fun postDelayed(task: Runnable, delay: Long)

        fun onShowDisplay()

        fun onHideDisplay()

    }

}