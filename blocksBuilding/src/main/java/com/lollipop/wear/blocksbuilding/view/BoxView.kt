package com.lollipop.wear.blocksbuilding.view

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.LifecycleOwner
import com.lollipop.wear.blocksbuilding.BBDsl
import com.lollipop.wear.blocksbuilding.dsl.ViewLayoutParams
import com.lollipop.wear.blocksbuilding.dsl.convert
import com.lollipop.wear.blocksbuilding.item.ViewGravity

fun ItemGroupScope<*>.box(
    layoutParams: ViewGroup.LayoutParams = ViewLayoutParams(),
    content: BoxScope.() -> Unit
): BoxScope {
    return BoxViewScope(
        add(
            FrameLayout(
                this.content.context
            ),
            layoutParams
        ), lifecycleOwner
    ).apply(content)
}

@BBDsl
interface BoxScope : ItemGroupScope<FrameLayout> {

    fun ViewGroup.LayoutParams.gravity(vararg gravity: ViewGravity): FrameLayout.LayoutParams

}

class BoxViewScope(
    frameLayout: FrameLayout, lifecycleOwner: LifecycleOwner
) : BasicItemGroupScope<FrameLayout>(frameLayout, lifecycleOwner), BoxScope {

    companion object {
        private fun updateChildGravity(child: View, gravity: Int) {
            val layoutParams = child.layoutParams as FrameLayout.LayoutParams
            layoutParams.gravity = gravity
            child.requestLayout()
        }
    }

    override fun ViewGroup.LayoutParams.gravity(vararg gravity: ViewGravity): FrameLayout.LayoutParams {
        return convert { FrameLayout.LayoutParams(it) }.also {
            it.gravity = gravity.sum()
        }
    }

}

