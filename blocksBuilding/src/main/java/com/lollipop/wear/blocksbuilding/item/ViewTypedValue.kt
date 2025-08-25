package com.lollipop.wear.blocksbuilding.item

import android.graphics.Rect
import android.util.TypedValue
import com.lollipop.wear.blocksbuilding.dsl.BBDSL
import kotlin.math.max
import kotlin.math.min

val MetricsValue.typedValue: ViewTypedValue.Absolute
    get() {
        return ViewTypedValue.Absolute(this)
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
            BBDSL.resources.displayMetrics
        ).toInt()
    }

    class Absolute(val value: MetricsValue) : ViewTypedValue() {
        override fun getValue(bounds: Rect): Int {
            return value.px
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