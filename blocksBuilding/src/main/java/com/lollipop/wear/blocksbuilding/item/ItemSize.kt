package com.lollipop.wear.blocksbuilding.item

import android.view.ViewGroup

val MetricsValue.itemSize: ItemSize.Absolute
    get() {
        return ItemSize.Absolute(this)
    }

sealed class ItemSize {

    abstract fun update(src: Int): Int

    object None : ItemSize() {
        override fun update(src: Int): Int {
            return src
        }
    }

    object Match : ItemSize() {
        override fun update(src: Int): Int {
            return ViewGroup.LayoutParams.MATCH_PARENT
        }
    }

    object Wrap : ItemSize() {
        override fun update(src: Int): Int {
            return ViewGroup.LayoutParams.WRAP_CONTENT
        }
    }

    data class Absolute(val size: MetricsValue) : ItemSize() {
        override fun update(src: Int): Int {
            return size.px
        }
    }

}