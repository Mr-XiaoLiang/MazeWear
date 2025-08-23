package com.lollipop.wear.blocksbuilding.dsl

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.wear.blocksbuilding.BlocksBuilder
import com.lollipop.wear.blocksbuilding.BuilderScope
import com.lollipop.wear.blocksbuilding.item.ItemSize

fun ViewGroup.blocks(content: BuilderScope.() -> Unit) {
    if (this is RecyclerView) {
        BlocksBuilder.with(this, content)
    } else {
        BlocksBuilder.with(this, content)
    }
}

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
