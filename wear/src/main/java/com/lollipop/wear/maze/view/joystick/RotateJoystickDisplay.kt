package com.lollipop.wear.maze.view.joystick

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.net.Uri
import com.lollipop.wear.maze.view.JoystickView

sealed class RotateJoystickDisplay : JoystickView.JoystickDisplay {

    companion object {
        fun create(drawable: Drawable): RotateJoystickDisplay {
            return DrawableMode(drawable)
        }

        fun create(bitmap: Bitmap): RotateJoystickDisplay {
            return BitmapMode(bitmap)
        }

        fun create(icon: Icon): RotateJoystickDisplay {
            return IconMode(icon)
        }

        fun create(uri: Uri): RotateJoystickDisplay {
            return UriMode(uri)
        }

        fun create(resourceId: Int): RotateJoystickDisplay {
            return ResourceMode(resourceId)
        }

        private const val DURATION_ANIMATION = 200L
    }

    private var joystickView: JoystickView? = null

    override fun onBindJoystick(view: JoystickView) {
        view.alpha = 0F
    }

    abstract fun onBindView(view: JoystickView)

    override fun onTouchDown(view: JoystickView) {
        if (joystickView != view) {
            joystickView?.animate()?.cancel()
        }
        joystickView = view
        val duration = ((1F - view.alpha) * DURATION_ANIMATION).toLong()
        view.animate().let { animator ->
            animator.cancel()
            animator.alpha(1F)
            animator.setDuration(duration)
            animator.start()
        }
    }

    override fun onTouchMove(
        view: JoystickView,
        angle: Float,
        radius: Float,
        centerX: Float,
        centerY: Float,
        touchX: Float,
        touchY: Float
    ) {
        view.rotation = angle + 90
    }

    override fun onTouchUp(view: JoystickView) {
        if (joystickView != view) {
            joystickView?.animate()?.cancel()
        }
        joystickView = view
        val duration = ((view.alpha) * DURATION_ANIMATION).toLong()
        view.animate().let { animator ->
            animator.cancel()
            animator.alpha(0F)
            animator.setDuration(duration)
            animator.start()
        }
    }

    class ResourceMode(private val resourceId: Int) : RotateJoystickDisplay() {
        override fun onBindView(view: JoystickView) {
            view.setImageResource(resourceId)
        }
    }

    class DrawableMode(private val drawable: Drawable) :
        RotateJoystickDisplay() {
        override fun onBindView(view: JoystickView) {
            view.setImageDrawable(drawable)
        }
    }

    class BitmapMode(private val bitmap: Bitmap) :
        RotateJoystickDisplay() {
        override fun onBindView(view: JoystickView) {
            view.setImageBitmap(bitmap)
        }
    }

    class IconMode(private val icon: Icon) :
        RotateJoystickDisplay() {
        override fun onBindView(view: JoystickView) {
            view.setImageIcon(icon)
        }
    }

    class UriMode(private val uri: Uri) :
        RotateJoystickDisplay() {
        override fun onBindView(view: JoystickView) {
            view.setImageURI(uri)
        }
    }

}