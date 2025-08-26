package com.lollipop.wear.blocksbuilding.view

import android.widget.FrameLayout
import androidx.lifecycle.LifecycleOwner
import com.lollipop.wear.blocksbuilding.BBDsl
import com.lollipop.wear.blocksbuilding.BlocksOwner
import com.lollipop.wear.blocksbuilding.BuilderScope

fun BlocksOwner.ViewBlock(content: ItemRootScope.() -> Unit): FrameLayout {
    val view = FrameLayout(context)
    val scope = ItemRootBlockScope(view, lifecycleOwner)
    content.invoke(scope)
    return view
}

fun BuilderScope.ItemView(content: ItemRootScope.() -> Unit) {
    item {
        blocksOwner.ViewBlock(content)
    }
}


@BBDsl
interface ItemRootScope : ItemGroupScope<FrameLayout>, MarginGroupScope

class ItemRootBlockScope(
    frameLayout: FrameLayout, lifecycleOwner: LifecycleOwner
) : BasicItemGroupScope<FrameLayout>(frameLayout, lifecycleOwner), ItemRootScope

