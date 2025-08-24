package com.lollipop.wear.blocksbuilding.item

import android.content.res.Resources
import android.graphics.Rect
import android.util.TypedValue
import kotlin.math.max
import kotlin.math.min

val Int.DP: ViewTypedValue.DP
    get() {
        return ViewTypedValue.DP(this.toFloat())
    }

val Float.DP: ViewTypedValue.DP
    get() {
        return ViewTypedValue.DP(this)
    }

val Int.PX: ViewTypedValue.PX
    get() {
        return ViewTypedValue.PX(this)
    }

val Int.SP: ViewTypedValue.SP
    get() {
        return ViewTypedValue.SP(this)
    }

val Float.SP: ViewTypedValue.SP
    get() {
        return ViewTypedValue.SP(this.toInt())
    }

val Float.PercentWidth: ViewTypedValue.PercentWidth
    get() {
        return ViewTypedValue.PercentWidth(this)
    }

val Float.PercentHeight: ViewTypedValue.PercentHeight
    get() {
        return ViewTypedValue.PercentHeight(this)
    }

val Float.PercentMin: ViewTypedValue.PercentMin
    get() {
        return ViewTypedValue.PercentMin(this)
    }

val Float.PercentMax: ViewTypedValue.PercentMax
    get() {
        return ViewTypedValue.PercentMax(this)
    }

sealed class ViewTypedValue {

    abstract fun getValue(bounds: Rect): Int

    protected fun getTypedValue(unit: Int, value: Float): Int {
        return TypedValue.applyDimension(
            unit,
            value,
            Resources.getSystem().displayMetrics
        ).toInt()
    }

    class DP(val value: Float) : ViewTypedValue() {
        override fun getValue(bounds: Rect): Int {
            return getTypedValue(
                TypedValue.COMPLEX_UNIT_DIP,
                value,
            )
        }
    }

    class PX(val value: Int) : ViewTypedValue() {
        override fun getValue(bounds: Rect): Int {
            return value
        }
    }

    class SP(val value: Int) : ViewTypedValue() {
        override fun getValue(bounds: Rect): Int {
            return getTypedValue(
                TypedValue.COMPLEX_UNIT_SP,
                value.toFloat(),
            )
        }
    }

    class PercentWidth(val value: Float) : ViewTypedValue() {
        override fun getValue(bounds: Rect): Int {
            return (bounds.width() * value).toInt()
        }
    }

    class PercentHeight(val value: Float) : ViewTypedValue() {
        override fun getValue(bounds: Rect): Int {
            return (bounds.height() * value).toInt()
        }
    }

    class PercentMin(val value: Float) : ViewTypedValue() {
        override fun getValue(bounds: Rect): Int {
            return ((min(bounds.width(), bounds.height())) * value).toInt()
        }
    }

    class PercentMax(val value: Float) : ViewTypedValue() {
        override fun getValue(bounds: Rect): Int {
            return ((max(bounds.width(), bounds.height())) * value).toInt()
        }
    }

}