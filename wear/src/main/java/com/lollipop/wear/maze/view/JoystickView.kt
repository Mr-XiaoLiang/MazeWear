package com.lollipop.wear.maze.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.appcompat.widget.AppCompatImageView
import com.lollipop.wear.maze.helper.DeviceHelper

class JoystickView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : AppCompatImageView(context, attributeSet) {


    companion object {
        fun getLength(
            x1: Float,
            y1: Float,
            x2: Float,
            y2: Float
        ): Float {
            return kotlin.math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1))
        }

    }

    private val restrictedZoneList = ArrayList<RestrictedZone>()
    private val touchCircleCenter = PointF()
    private var joystickTouchListener: OnJoystickTouchListener? = null
    private var joystickDisplay: JoystickDisplay? = null

    private var touchSlop = 0
    private val touchDownPoint = PointF()
    private var touchMode = TouchMode.NONE

    fun addRestrictedZone(zone: RestrictedZone) {
        restrictedZoneList.add(zone)
    }

    fun clearRestrictedZone() {
        restrictedZoneList.clear()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val contentLeft = paddingLeft
        val contentRight = width - paddingRight
        val contentTop = paddingTop
        val contentBottom = height - paddingBottom
        touchCircleCenter.set(
            (contentRight + contentLeft) * 0.5F,
            (contentBottom + contentTop) * 0.5F
        )
    }

    private fun onTouchDown(event: MotionEvent) {
        touchSlop = ViewConfiguration.get(context).scaledTouchSlop
        touchDownPoint.set(event.x, event.y)
        touchMode = TouchMode.NONE
    }

//    private fun log(value: String) {
//        Log.d("ArcTouchLayout", value)
//    }

    private fun cancelTouch() {
//        log("cancelTouch, requestDisallowInterceptTouchEvent = false")
        if (touchMode == TouchMode.HOLD) {
            onTouchUp()
        }
        touchMode = TouchMode.CANCELED
        parent?.requestDisallowInterceptTouchEvent(false)
    }

    private fun requestTouch() {
//        log("requestTouch, requestDisallowInterceptTouchEvent = true")
        parent?.requestDisallowInterceptTouchEvent(true)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        log("onTouchEvent.${event?.actionMasked}")
        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
//                log("onInterceptTouchEvent.ACTION_DOWN")
                onTouchDown(event)
                if (needIntercept(event.x, event.y)) {
                    requestTouch()
                    return true
                }
            }

            MotionEvent.ACTION_MOVE -> {
//                log("onTouchEvent.ACTION_MOVE")
                if (needIntercept(event.x, event.y)) {
                    onTouchMove(event)
                    return true
                }
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
//                log("onTouchEvent.ACTION_POINTER_DOWN")
                // 超过一个手指之后，放弃吧
                cancelTouch()
            }

            MotionEvent.ACTION_UP -> {
//                log("onTouchEvent.ACTION_UP")
                cancelTouch()
            }

            MotionEvent.ACTION_CANCEL -> {
//                log("onTouchEvent.ACTION_CANCEL")
                cancelTouch()
            }
        }
        if (touchMode == TouchMode.HOLD) {
//            log("onTouchEvent.THIS.result = true")
            return true
        }
        val result = super.onTouchEvent(event)
//        log("onTouchEvent.SUPER.result = $result")
        return result
    }

    private fun onTouchMove(event: MotionEvent) {
        val touchX = event.x
        val touchY = event.y
        val radius = getTouchPointRadius(touchX, touchY)
        val angle = getTouchPointAngle(touchX, touchY)
        onTouchMove(
            angle,
            radius,
            touchCircleCenter.x,
            touchCircleCenter.y,
            touchX,
            touchY
        )
    }

    private fun onTouchHold() {
        joystickDisplay?.onTouchDown(this)
    }

    private fun onTouchMove(
        angle: Float,
        radius: Float,
        centerX: Float,
        centerY: Float,
        touchX: Float,
        touchY: Float
    ) {
        joystickTouchListener?.onTouch(this, angle, radius, centerX, centerY, touchX, touchY)
        joystickDisplay?.onTouchMove(this, angle, radius, centerX, centerY, touchX, touchY)
    }

    private fun onTouchUp() {
        joystickTouchListener?.onTouchUp(this)
        joystickDisplay?.onTouchUp(this)
    }

    private fun needIntercept(x: Float, y: Float): Boolean {
//        log("needIntercept.[$x, $y]")
        when (touchMode) {
            TouchMode.NONE -> {
                val viewWidth = width
                val viewHeight = height
                for (zone in restrictedZoneList) {
                    if (zone.isTouchOnRestricted(viewWidth, viewHeight, x, y)) {
                        cancelTouch()
//                        log("needIntercept.CANCELED")
                        return false
                    }
                }
//                log("needIntercept.PENDING")
                touchMode = TouchMode.HOLD
                onTouchHold()
                return false
            }

            TouchMode.CANCELED -> {
                return false
            }

            TouchMode.HOLD -> {
                return true
            }
        }
    }

    private fun getTouchPointRadius(touchX: Float, touchY: Float): Float {
        val centerX = touchCircleCenter.x
        val centerY = touchCircleCenter.y
        return getLength(touchX, touchY, centerX, centerY)
    }

    private fun getTouchPointAngle(touchX: Float, touchY: Float): Float {
        val centerX = touchCircleCenter.x
        val centerY = touchCircleCenter.y
        return DeviceHelper.calculateCentralAngle(
            centerX, centerY, // 圆心
            centerX, 0F, // 锚点是View12点钟的点
            touchX, touchY
        )
    }

    fun setJoystickTouchListener(listener: OnJoystickTouchListener) {
        this.joystickTouchListener = listener
    }

    fun setJoystickDisplay(display: JoystickDisplay) {
        this.joystickDisplay = display
        display.onBindJoystick(this)
    }

    interface OnJoystickTouchListener {
        fun onTouch(
            view: JoystickView,
            angle: Float,
            radius: Float,
            centerX: Float,
            centerY: Float,
            touchX: Float,
            touchY: Float
        )

        fun onTouchUp(view: JoystickView)
    }

    /**
     * 禁止触摸的区域
     */
    fun interface RestrictedZone {
        fun isTouchOnRestricted(
            viewWidth: Int,
            viewHeight: Int,
            x: Float,
            y: Float
        ): Boolean
    }


    private enum class TouchMode {
        /**
         * 初始状态，表示还没有开始处理，所以可以继续处理
         * 每次 down 都会重新计算一次
         */
        NONE,

        /**
         * 不适合拦截，因此放弃处理
         */
        CANCELED,

        /**
         * 拦截手势，表示自己在处理了
         */
        HOLD,

    }

    interface JoystickDisplay {

        fun onBindJoystick(view: JoystickView)

        fun onTouchDown(view: JoystickView)

        fun onTouchMove(
            view: JoystickView,
            angle: Float,
            radius: Float,
            centerX: Float,
            centerY: Float,
            touchX: Float,
            touchY: Float
        )

        fun onTouchUp(view: JoystickView)

    }

}