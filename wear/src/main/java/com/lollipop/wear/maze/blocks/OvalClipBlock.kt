package com.lollipop.wear.maze.blocks

import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.lifecycle.LifecycleOwner
import com.lollipop.wear.blocksbuilding.dsl.ViewLayoutParams
import com.lollipop.wear.blocksbuilding.view.BasicItemGroupScope
import com.lollipop.wear.blocksbuilding.view.ItemGroupScope
import com.lollipop.wear.maze.widget.OvalClipView

fun ItemGroupScope<*>.OvalClip(
    layoutParams: ViewGroup.LayoutParams = ViewLayoutParams(),
    content: OvalClipScope.() -> Unit
): OvalClipScope {
    return OvalClipBlockScope(
        add(
            OvalClipView(
                this.content.context
            ),
            layoutParams
        ), lifecycleOwner
    ).apply(content)
}

interface OvalClipScope : ItemGroupScope<OvalClipView> {

    var color: Int

    fun color(@ColorRes colorId: Int) {
        this.color = context.getColor(colorId)
    }

}

class OvalClipBlockScope(
    clipView: OvalClipView, lifecycleOwner: LifecycleOwner
) : BasicItemGroupScope<OvalClipView>(clipView, lifecycleOwner), OvalClipScope {
    override var color: Int
        get() {
            return view.color
        }
        set(value) {
            view.color = value
        }
}
