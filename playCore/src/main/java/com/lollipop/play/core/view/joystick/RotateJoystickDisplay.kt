package com.lollipop.play.core.view.joystick

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.net.Uri
import android.view.View
import com.lollipop.play.core.view.JoystickView

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

        fun create(view: View): RotateJoystickDisplay {
            return ViewMode(view)
        }

        private const val DURATION_ANIMATION = 200L
    }

    private var lastView: View? = null

    override fun onBindJoystick(view: JoystickView) {
        view.alpha = 0F
    }

    abstract fun onBindView(view: JoystickView)

    override fun onTouchDown(view: JoystickView) {
        showView(view)
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
        rotateView(view, angle)
    }

    override fun onTouchUp(view: JoystickView) {
        hideView(view)
    }

    protected fun rotateView(view: View, angle: Float) {
        view.rotation = angle
    }

    protected fun showView(view: View) {
        if (lastView != view) {
            lastView?.animate()?.cancel()
        }
        lastView = view
        val duration = ((1F - view.alpha) * DURATION_ANIMATION).toLong()
        view.animate().let { animator ->
            animator.cancel()
            animator.alpha(1F)
            animator.setDuration(duration)
            animator.start()
        }
    }

    protected fun hideView(view: View) {
        if (lastView != view) {
            lastView?.animate()?.cancel()
        }
        lastView = view
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

    class ViewMode(private val target: View) : RotateJoystickDisplay(),
        GenericMotionJoystickDisplayHelper.Callback {

        private val motionDisplayHelper = GenericMotionJoystickDisplayHelper(this)

        override fun onBindView(view: JoystickView) {
        }

        override fun onTouchDown(view: JoystickView) {
            showView(target)
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
                motionDisplayHelper.onGamepadEvent()
            }
            rotateView(target, angle)
        }

        override fun onTouchUp(view: JoystickView) {
            hideView(target)
        }

        override fun postDelayed(task: Runnable, delay: Long) {
            target.postDelayed(task, delay)
        }

        override fun onShowDisplay() {
            showView(target)
        }

        override fun onHideDisplay() {
            hideView(target)
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