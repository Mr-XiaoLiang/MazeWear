package com.lollipop.wear.blocksbuilding.item

import android.content.Context
import android.util.TypedValue
import android.view.ViewGroup

sealed class ItemSize {

    abstract fun update(src: Int, context: Context): Int

    object None : ItemSize() {
        override fun update(src: Int, context: Context): Int {
            return src
        }
    }

    object Match : ItemSize() {
        override fun update(src: Int, context: Context): Int {
            return ViewGroup.LayoutParams.MATCH_PARENT
        }
    }

    object Wrap : ItemSize() {
        override fun update(src: Int, context: Context): Int {
            return ViewGroup.LayoutParams.WRAP_CONTENT
        }
    }

    data class Dp(val size: Int) : ItemSize() {
        override fun update(src: Int, context: Context): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                size.toFloat(),
                context.resources.displayMetrics
            ).toInt()
        }
    }

    data class Px(val size: Int) : ItemSize() {
        override fun update(src: Int, context: Context): Int {
            return size
        }
    }

}