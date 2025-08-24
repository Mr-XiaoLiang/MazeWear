package com.lollipop.wear.blocksbuilding.dsl

import android.view.View
import android.view.ViewGroup
import com.lollipop.wear.blocksbuilding.item.ItemSize

fun View.layoutParams(width: ItemSize = ItemSize.None, height: ItemSize = ItemSize.None) {
    val old = layoutParams
    if (old == null) {
        layoutParams = ViewGroup.LayoutParams(
            width.update(ViewGroup.LayoutParams.WRAP_CONTENT, context),
            height.update(ViewGroup.LayoutParams.WRAP_CONTENT, context)
        )
    } else {
        old.width = width.update(old.width, context)
        old.height = height.update(old.height, context)
        this.layoutParams = old
    }
}
