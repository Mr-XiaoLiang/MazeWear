package com.lollipop.wear.blocksbuilding.item

import android.view.Gravity

val Int?.Center: Int
    get() {
        return ViewGravity.Center.getGravity(this ?: 0)
    }

val Int?.Top: Int
    get() {
        return ViewGravity.Top.getGravity(this ?: 0)
    }

val Int?.Bottom: Int
    get() {
        return ViewGravity.Bottom.getGravity(this ?: 0)
    }

val Int?.Start: Int
    get() {
        return ViewGravity.Start.getGravity(this ?: 0)
    }

val Int?.End: Int
    get() {
        return ViewGravity.End.getGravity(this ?: 0)
    }

val Int?.Left: Int
    get() {
        return ViewGravity.Left.getGravity(this ?: 0)
    }

val Int?.Right: Int
    get() {
        return ViewGravity.Right.getGravity(this ?: 0)
    }

val Int?.Fill: Int
    get() {
        return ViewGravity.Fill.getGravity(this ?: 0)
    }

val Int?.FillVertical: Int
    get() {
        return ViewGravity.FillVertical.getGravity(this ?: 0)
    }

val Int?.CenterVertical: Int
    get() {
        return ViewGravity.CenterVertical.getGravity(this ?: 0)
    }

val Int?.CenterHorizontal: Int
    get() {
        return ViewGravity.CenterHorizontal.getGravity(this ?: 0)
    }

val Int?.FillHorizontal: Int
    get() {
        return ViewGravity.FillHorizontal.getGravity(this ?: 0)
    }

operator fun Int.plus(other: ViewGravity): Int {
    return other.getGravity(this)
}

sealed class ViewGravity {

    abstract fun getGravity(src: Int): Int

    protected fun getGravity(src: Int, gravity: Int): Int {
        return src or gravity
    }

    val value: Int
        get() {
            return getGravity(0)
        }

    operator fun plus(other: ViewGravity): Int {
        return other.getGravity(getGravity(0))
    }

    object Center : ViewGravity() {
        override fun getGravity(src: Int): Int {
            return getGravity(src, Gravity.CENTER)
        }
    }

    object CenterVertical : ViewGravity() {
        override fun getGravity(src: Int): Int {
            return getGravity(src, Gravity.CENTER_VERTICAL)
        }
    }

    object CenterHorizontal : ViewGravity() {
        override fun getGravity(src: Int): Int {
            return getGravity(src, Gravity.CENTER_HORIZONTAL)
        }
    }

    object Top : ViewGravity() {
        override fun getGravity(src: Int): Int {
            return getGravity(src, Gravity.TOP)
        }
    }

    object Bottom : ViewGravity() {
        override fun getGravity(src: Int): Int {
            return getGravity(src, Gravity.BOTTOM)
        }
    }

    object Left : ViewGravity() {
        override fun getGravity(src: Int): Int {
            return getGravity(src, Gravity.LEFT)
        }
    }

    object Right : ViewGravity() {
        override fun getGravity(src: Int): Int {
            return getGravity(src, Gravity.RIGHT)
        }
    }

    object Start : ViewGravity() {
        override fun getGravity(src: Int): Int {
            return getGravity(src, Gravity.START)
        }
    }

    object End : ViewGravity() {
        override fun getGravity(src: Int): Int {
            return getGravity(src, Gravity.END)
        }
    }

    object Fill : ViewGravity() {
        override fun getGravity(src: Int): Int {
            return getGravity(src, Gravity.FILL)
        }
    }

    object FillVertical : ViewGravity() {
        override fun getGravity(src: Int): Int {
            return getGravity(src, Gravity.FILL_VERTICAL)
        }
    }

    object FillHorizontal : ViewGravity() {
        override fun getGravity(src: Int): Int {
            return getGravity(src, Gravity.FILL_HORIZONTAL)
        }
    }

}