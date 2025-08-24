package com.lollipop.wear.blocksbuilding.view

import android.widget.FrameLayout
import androidx.lifecycle.LifecycleOwner
import com.lollipop.wear.blocksbuilding.BlocksOwner
import com.lollipop.wear.blocksbuilding.BuilderScope

fun BlocksOwner.viewBlock(content: ItemRootScope.() -> Unit): FrameLayout {
    val view = FrameLayout(context)
    val scope = ItemRootViewScope(view, lifecycleOwner)
    content.invoke(scope)
    return view
}

fun BuilderScope.itemView(content: ItemRootScope.() -> Unit) {
    item {
        blocksOwner.viewBlock(content)
    }
}


interface ItemRootScope : ItemGroupScope<FrameLayout>

class ItemRootViewScope(
    frameLayout: FrameLayout, lifecycleOwner: LifecycleOwner
) : BasicItemGroupScope<FrameLayout>(frameLayout, lifecycleOwner), ItemRootScope

