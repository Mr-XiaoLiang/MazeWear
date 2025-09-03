package com.lollipop.wear.blocksbuilding.view

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.lollipop.wear.blocksbuilding.BBDsl
import com.lollipop.wear.blocksbuilding.dsl.ViewLayoutParams


fun ItemGroupScope<*>.Space(
    layoutParams: ViewGroup.LayoutParams = ViewLayoutParams(),
    content: SpaceScope.() -> Unit = {}
): SpaceScope {
    return SpaceBlockScope(
        add(
            android.widget.Space(
                this.content.context
            ),
            layoutParams
        ), lifecycleOwner
    ).apply(content)
}

@BBDsl
interface SpaceScope : ItemViewScope<android.widget.Space> {
}

class SpaceBlockScope(
    spaceView: android.widget.Space, lifecycleOwner: LifecycleOwner
) : BasicItemViewScope<android.widget.Space>(spaceView, lifecycleOwner), SpaceScope {

}
