package com.lollipop.wear.blocksbuilding.dsl

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.wear.blocksbuilding.BlocksBuilder
import com.lollipop.wear.blocksbuilding.BuilderScope
import com.lollipop.wear.blocksbuilding.IBlock
import com.lollipop.wear.blocksbuilding.data.DataObserver
import com.lollipop.wear.blocksbuilding.data.DataProvider
import com.lollipop.wear.blocksbuilding.item.ItemSize
import kotlin.reflect.KProperty

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

fun <T> blockStateOf(value: T): DataObserver<T> {
    return DataObserver(value)
}

inline operator fun <reified T> DataProvider<T>.getValue(
    thisObj: Any?,
    property: KProperty<*>
): T {
    return value
}

inline operator fun <reified T> DataObserver<T>.setValue(
    thisObj: Any?,
    property: KProperty<*>,
    newValue: T
) {
    value = newValue
}


