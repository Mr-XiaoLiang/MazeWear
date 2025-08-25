package com.lollipop.wear.blocksbuilding.item

import android.view.Gravity

val Int?.Center: Int
    get() {
        return ViewGravity.Center.or(this ?: 0)
    }

val Int?.Top: Int
    get() {
        return ViewGravity.Top.or(this ?: 0)
    }

val Int?.Bottom: Int
    get() {
        return ViewGravity.Bottom.or(this ?: 0)
    }

val Int?.Start: Int
    get() {
        return ViewGravity.Start.or(this ?: 0)
    }

val Int?.End: Int
    get() {
        return ViewGravity.End.or(this ?: 0)
    }

val Int?.Left: Int
    get() {
        return ViewGravity.Left.or(this ?: 0)
    }

val Int?.Right: Int
    get() {
        return ViewGravity.Right.or(this ?: 0)
    }

val Int?.Fill: Int
    get() {
        return ViewGravity.Fill.or(this ?: 0)
    }

val Int?.FillVertical: Int
    get() {
        return ViewGravity.FillVertical.or(this ?: 0)
    }

val Int?.CenterVertical: Int
    get() {
        return ViewGravity.CenterVertical.or(this ?: 0)
    }

val Int?.CenterHorizontal: Int
    get() {
        return ViewGravity.CenterHorizontal.or(this ?: 0)
    }

val Int?.FillHorizontal: Int
    get() {
        return ViewGravity.FillHorizontal.or(this ?: 0)
    }

operator fun Int.plus(other: ViewGravity): Int {
    return other.or(this)
}

sealed class ViewGravity {

    abstract fun or(src: Int): Int

    protected fun getGravity(src: Int, gravity: Int): Int {
        return src or gravity
    }

    val value: Int
        get() {
            return or(0)
        }

    operator fun plus(other: ViewGravity): Int {
        return other.or(or(0))
    }

    object Center : ViewGravity() {
        override fun or(src: Int): Int {
            return getGravity(src, Gravity.CENTER)
        }
    }

    object CenterVertical : ViewGravity() {
        override fun or(src: Int): Int {
            return getGravity(src, Gravity.CENTER_VERTICAL)
        }
    }

    object CenterHorizontal : ViewGravity() {
        override fun or(src: Int): Int {
            return getGravity(src, Gravity.CENTER_HORIZONTAL)
        }
    }

    object Top : ViewGravity() {
        override fun or(src: Int): Int {
            return getGravity(src, Gravity.TOP)
        }
    }

    object Bottom : ViewGravity() {
        override fun or(src: Int): Int {
            return getGravity(src, Gravity.BOTTOM)
        }
    }

    object Left : ViewGravity() {
        override fun or(src: Int): Int {
            return getGravity(src, Gravity.LEFT)
        }
    }

    object Right : ViewGravity() {
        override fun or(src: Int): Int {
            return getGravity(src, Gravity.RIGHT)
        }
    }

    object Start : ViewGravity() {
        override fun or(src: Int): Int {
            return getGravity(src, Gravity.START)
        }
    }

    object End : ViewGravity() {
        override fun or(src: Int): Int {
            return getGravity(src, Gravity.END)
        }
    }

    object Fill : ViewGravity() {
        override fun or(src: Int): Int {
            return getGravity(src, Gravity.FILL)
        }
    }

    object FillVertical : ViewGravity() {
        override fun or(src: Int): Int {
            return getGravity(src, Gravity.FILL_VERTICAL)
        }
    }

    object FillHorizontal : ViewGravity() {
        override fun or(src: Int): Int {
            return getGravity(src, Gravity.FILL_HORIZONTAL)
        }
    }

}