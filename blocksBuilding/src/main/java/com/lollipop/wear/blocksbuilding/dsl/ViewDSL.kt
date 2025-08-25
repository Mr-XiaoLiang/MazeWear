package com.lollipop.wear.blocksbuilding.dsl

import android.view.View
import android.view.ViewGroup
import com.lollipop.wear.blocksbuilding.item.ItemSize
import com.lollipop.wear.blocksbuilding.item.ViewTypedValue

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

inline fun <reified T : ViewGroup.LayoutParams> T.width(size: ViewTypedValue.DP): T {
    return width(ItemSize.Dp(size.value))
}

inline fun <reified T : ViewGroup.LayoutParams> T.height(size: ViewTypedValue.DP): T {
    return height(ItemSize.Dp(size.value))
}

inline fun <reified T : ViewGroup.LayoutParams> T.width(size: ViewTypedValue.PX): T {
    return width(ItemSize.Px(size.value))
}

inline fun <reified T : ViewGroup.LayoutParams> T.height(size: ViewTypedValue.PX): T {
    return height(ItemSize.Px(size.value))
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
