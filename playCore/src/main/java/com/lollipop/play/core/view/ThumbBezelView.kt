package com.lollipop.play.core.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.core.graphics.withRotation
import com.lollipop.play.core.helper.DeviceHelper
import kotlin.math.max
import kotlin.math.min

class ThumbBezelView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : JoystickView(context, attributeSet) {

    companion object {
        private const val DEFAULT_SCALES_NUMBER = 12
        private const val DEFAULT_BEZEL_WIDTH_WEIGHT = 0.15F
    }

    private val bezelDrawable = BezelDrawable()

    private val thumbTouchDelegate = ThumbTouchDelegate()

    var scalesNumber: Int
        get() {
            return bezelDrawable.scalesNumber
        }
        set(value) {
            bezelDrawable.scalesNumber = value
            thumbTouchDelegate.scaleNumber = value
        }

    var stripeNumber: Int
        get() {
            return bezelDrawable.stripeNumber
        }
        set(value) {
            bezelDrawable.stripeNumber = value
        }

    var bezelWidthWeight: Float
        get() {
            return bezelDrawable.bezelWidthWeight
        }
        set(value) {
            bezelDrawable.bezelWidthWeight = value
            invalidate()
        }

    var bezelColor: Int
        set(value) {
            bezelDrawable.bezelColor = value
        }
        get() {
            return bezelDrawable.bezelColor
        }

    var lineWidthWeight: Float
        get() {
            return bezelDrawable.lineWidthWeight
        }
        set(value) {
            bezelDrawable.lineWidthWeight = value
        }

    init {
        setImageDrawable(bezelDrawable)
        setJoystickDisplay(ThumbBezelDisplay(bezelDrawable))
        setJoystickTouchListener(thumbTouchDelegate)
    }

    fun setThumbCallback(callback: OnThumbCallback) {
        thumbTouchDelegate.onThumbCallback = callback
    }

    private class ThumbTouchDelegate : OnJoystickTouchListener {

        private var currentScale = 0

        var scaleNumber = DEFAULT_SCALES_NUMBER

        private var isFirstDrag = true

        var onThumbCallback: OnThumbCallback? = null

        override fun onTouch(
            view: JoystickView,
            angle: Float,
            radius: Float,
            centerX: Float,
            centerY: Float,
            touchX: Float,
            touchY: Float
        ) {
            if (isFirstDrag) {
                currentScale = getScaleIndex(angle)
            } else {
                val scaleMax = scaleNumber - 1
                val scaleIndex = getScaleIndex(angle)
                if (scaleIndex == 0 && currentScale == scaleMax) {
                    // 如果是从最后一个，滑动到了0，那么是向后
                    onThumbCallback?.onThumbMove(ThumbState.NEXT)
                } else if (scaleIndex == scaleMax && currentScale == 0) {
                    // 从0，滑动到了最后一格，那么是向前
                    onThumbCallback?.onThumbMove(ThumbState.PREVIOUS)
                } else if (scaleIndex < currentScale) {
                    onThumbCallback?.onThumbMove(ThumbState.PREVIOUS)
                } else if (scaleIndex > currentScale) {
                    onThumbCallback?.onThumbMove(ThumbState.NEXT)
                }
                currentScale = scaleIndex
            }
        }

        private fun getScaleIndex(angle: Float): Int {
            val step = 360F / scaleNumber
            return (angle / step).toInt()
        }

        override fun onTouchUp(view: JoystickView) {
            isFirstDrag = true
        }
    }

    private class ThumbBezelDisplay(
        private val drawable: BezelDrawable
    ) : JoystickDisplay {

        private var displayAngle = 0F
        private var touchDownAngle = 0F
        private var isFirstDrag = true

        override fun onBindJoystick(view: JoystickView) {
        }

        override fun onTouchDown(view: JoystickView) {
            isFirstDrag = true
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
            if (isFirstDrag) {
                touchDownAngle = angle
                isFirstDrag = false
            }
            val angleOffset = angle - touchDownAngle
            displayAngle += angleOffset
            drawable.angle = displayAngle
        }

        override fun onTouchUp(view: JoystickView) {
        }
    }

    private class BezelDrawable : Drawable() {

        private val paint = Paint().apply {
            isDither = true
            isAntiAlias = true
            style = Paint.Style.STROKE
            color = Color.WHITE
        }

        var scalesNumber = DEFAULT_SCALES_NUMBER
            set(value) {
                field = value
                buildScalePath()
            }

        var stripeNumber = 4
            set(value) {
                field = value
                buildScalePath()
            }

        var bezelWidthWeight = DEFAULT_BEZEL_WIDTH_WEIGHT
            set(value) {
                field = value
                buildScalePath()
            }

        var bezelColor: Int
            set(value) {
                paint.color = value
                invalidateSelf()
            }
            get() {
                return paint.color
            }

        private var lineWidth: Float
            get() {
                return paint.strokeWidth
            }
            set(value) {
                paint.strokeWidth = value
                invalidateSelf()
            }

        var lineWidthWeight: Float = 0.16F
            set(value) {
                field = value
                buildScalePath()
            }

        private val scalePath = Path()

        var angle = 0F
            set(value) {
                field = value
                invalidateSelf()
            }

        override fun onBoundsChange(bounds: Rect) {
            super.onBoundsChange(bounds)
            buildScalePath()
        }

        private fun buildScalePath() {
            scalePath.reset()
            if (bounds.isEmpty) {
                return
            }

            val d = min(bounds.width(), bounds.height())
            val radius = d * 0.5F
            val ox = bounds.exactCenterX()
            val oy = bounds.exactCenterY()

            val bezelWidth = bezelWidthWeight * radius
            lineWidth = max(1F, bezelWidth * lineWidthWeight)
            val edgeInner = radius - bezelWidth
            val scaleEndS = edgeInner + (bezelWidth * 0.4F)
            val scaleEndL = edgeInner + (bezelWidth * 0.8F)

            val scaleStep = 360F / scalesNumber / (stripeNumber + 1)
            // 计算的起始角度在3点钟位置，我们这里的绘制开始是在12点钟位置，所以向后转90度
            var thisAngle = 0F - 90F
            for (scale in 0 until scalesNumber) {
                val startScalePoint = DeviceHelper.calculatePointByAngle(
                    ox, oy,
                    edgeInner,
                    thisAngle
                )
                val endScalePoint = DeviceHelper.calculatePointByAngle(
                    ox, oy,
                    scaleEndL,
                    thisAngle
                )
                scalePath.moveTo(startScalePoint.x, startScalePoint.y)
                scalePath.lineTo(endScalePoint.x, endScalePoint.y)
                thisAngle += scaleStep
                for (stripe in 0 until stripeNumber) {
                    val startStripePoint = DeviceHelper.calculatePointByAngle(
                        ox, oy,
                        edgeInner,
                        thisAngle
                    )
                    val endStripePoint = DeviceHelper.calculatePointByAngle(
                        ox, oy,
                        scaleEndS,
                        thisAngle
                    )
                    scalePath.moveTo(startStripePoint.x, startStripePoint.y)
                    scalePath.lineTo(endStripePoint.x, endStripePoint.y)
                    thisAngle += scaleStep
                }
            }
            invalidateSelf()
        }

        override fun draw(canvas: Canvas) {
            canvas.withRotation(
                angle * -1,
                bounds.exactCenterX(),
                bounds.exactCenterY()
            ) {
                canvas.drawPath(scalePath, paint)
            }
        }

        override fun getOpacity(): Int {
            return PixelFormat.TRANSPARENT
        }

        override fun setAlpha(alpha: Int) {
            paint.alpha = alpha
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            paint.colorFilter = colorFilter
        }

    }

    fun interface OnThumbCallback {

        fun onThumbMove(state: ThumbState)

    }

    enum class ThumbState {
        NEXT,
        PREVIOUS
    }

}