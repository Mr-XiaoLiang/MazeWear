package com.lollipop.wear.blocksbuilding.dsl

import android.view.View
import android.view.ViewGroup
import com.lollipop.wear.blocksbuilding.item.DP
import com.lollipop.wear.blocksbuilding.item.ItemSize
import com.lollipop.wear.blocksbuilding.item.PX

fun View.layoutParams(width: ItemSize = ItemSize.None, height: ItemSize = ItemSize.None) {
    val old = layoutParams
    if (old == null) {
        layoutParams = ViewGroup.LayoutParams(
            width.update(ViewGroup.LayoutParams.WRAP_CONTENT),
            height.update(ViewGroup.LayoutParams.WRAP_CONTENT)
        )
    } else {
        old.width = width.update(old.width)
        old.height = height.update(old.height)
        this.layoutParams = old
    }
}

inline fun <reified T : ViewGroup.LayoutParams> T.width(size: ItemSize): T {
    return also {
        it.width = size.update(it.width)
    }
}

inline fun <reified T : ViewGroup.LayoutParams> T.height(size: ItemSize): T {
    return also {
        it.height = size.update(it.width)
    }
}

inline fun <reified T : ViewGroup.LayoutParams> T.width(size: DP): T {
    return width(ItemSize.Absolute(size))
}

inline fun <reified T : ViewGroup.LayoutParams> T.height(size: DP): T {
    return height(ItemSize.Absolute(size))
}

inline fun <reified T : ViewGroup.LayoutParams> T.widthMatch(): T {
    return width(ItemSize.Match)
}

inline fun <reified T : ViewGroup.LayoutParams> T.heightMatch(): T {
    return height(ItemSize.Match)
}

inline fun <reified T : ViewGroup.LayoutParams> T.widthWrap(): T {
    return width(ItemSize.Wrap)
}

inline fun <reified T : ViewGroup.LayoutParams> T.heightWrap(): T {
    return height(ItemSize.Wrap)
}

inline fun <reified T : ViewGroup.LayoutParams> T.widthEmpty(): T {
    return width(ItemSize.Absolute(0.PX))
}

inline fun <reified T : ViewGroup.LayoutParams> T.heightEmpty(): T {
    return height(ItemSize.Absolute(0.PX))
}

class ViewLayoutParams : ViewGroup.LayoutParams(
    WRAP_CONTENT,
    WRAP_CONTENT
)

inline fun <reified L : ViewGroup.LayoutParams> ViewGroup.LayoutParams.convert(create: (ViewGroup.LayoutParams) -> L): L {
    if (this is L) {
        return this
    } else {
        val oldWith = width
        val oldHeight = height
        return create(this).also {
            it.width = oldWith
            it.height = oldHeight
        }
    }
}
