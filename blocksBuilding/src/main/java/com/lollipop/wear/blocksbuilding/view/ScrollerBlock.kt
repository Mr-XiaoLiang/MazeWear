package com.lollipop.wear.blocksbuilding.view

import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.LifecycleOwner
import com.lollipop.wear.blocksbuilding.BBDsl
import com.lollipop.wear.blocksbuilding.dsl.ViewLayoutParams


fun ItemGroupScope<*>.Scroller(
    layoutParams: ViewGroup.LayoutParams = ViewLayoutParams(),
    content: ScrollerScope.() -> Unit
): ScrollerScope {
    return ScrollerBlockScope(
        add(
            NestedScrollView(
                context
            ),
            layoutParams
        ), lifecycleOwner
    ).apply(content)
}

@BBDsl
interface ScrollerScope : ItemViewScope<NestedScrollView> {

}

class ScrollerBlockScope(
    scrollerView: NestedScrollView, lifecycleOwner: LifecycleOwner
) : BasicItemGroupScope<NestedScrollView>(scrollerView, lifecycleOwner), ScrollerScope {

}

