package com.lollipop.wear.blocksbuilding.view

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.LifecycleOwner
import com.lollipop.wear.blocksbuilding.BBDsl
import com.lollipop.wear.blocksbuilding.dsl.ViewLayoutParams
import com.lollipop.wear.blocksbuilding.dsl.convert
import com.lollipop.wear.blocksbuilding.item.ViewGravity
import com.lollipop.wear.blocksbuilding.item.sum

fun ItemGroupScope<*>.Box(
    layoutParams: ViewGroup.LayoutParams = ViewLayoutParams(),
    content: BoxScope.() -> Unit
): BoxScope {
    return BoxBlockScope(
        add(
            FrameLayout(
                this.content.context
            ),
            layoutParams
        ), lifecycleOwner
    ).apply(content)
}

@BBDsl
interface BoxScope : ItemGroupScope<FrameLayout>, MarginGroupScope {

    fun ViewGroup.LayoutParams.gravity(vararg gravity: ViewGravity): FrameLayout.LayoutParams

}

class BoxBlockScope(
    frameLayout: FrameLayout, lifecycleOwner: LifecycleOwner
) : BasicItemGroupScope<FrameLayout>(frameLayout, lifecycleOwner), BoxScope {

    override fun ViewGroup.LayoutParams.gravity(vararg gravity: ViewGravity): FrameLayout.LayoutParams {
        return convert { FrameLayout.LayoutParams(it) }.also {
            it.gravity = gravity.sum()
        }
    }

}

