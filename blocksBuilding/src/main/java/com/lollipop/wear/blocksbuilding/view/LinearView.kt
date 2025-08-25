package com.lollipop.wear.blocksbuilding.view

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.LifecycleOwner
import com.lollipop.wear.blocksbuilding.BBDsl
import com.lollipop.wear.blocksbuilding.dsl.ViewLayoutParams
import com.lollipop.wear.blocksbuilding.dsl.convert
import com.lollipop.wear.blocksbuilding.item.ViewGravity

fun ItemGroupScope<*>.column(
    layoutParams: ViewGroup.LayoutParams = ViewLayoutParams(),
    content: LinearScope.() -> Unit
): LinearScope {
    val view = add(LinearLayout(this.content.context), layoutParams)
    view.orientation = LinearLayout.VERTICAL
    return LinearViewScope(view, lifecycleOwner).apply(content)
}

fun ItemGroupScope<*>.row(
    layoutParams: ViewGroup.LayoutParams = ViewLayoutParams(),
    content: LinearScope.() -> Unit
): LinearScope {
    val view = add(LinearLayout(this.content.context), layoutParams)
    view.orientation = LinearLayout.HORIZONTAL
    return LinearViewScope(view, lifecycleOwner).apply(content)
}

@BBDsl
interface LinearScope : ItemGroupScope<LinearLayout> {

    fun ViewGroup.LayoutParams.weight(weight: Float): LinearLayout.LayoutParams

    fun ViewGroup.LayoutParams.gravity(vararg gravity: ViewGravity): LinearLayout.LayoutParams

}

class LinearViewScope(
    frameLayout: LinearLayout, lifecycleOwner: LifecycleOwner
) : BasicItemGroupScope<LinearLayout>(frameLayout, lifecycleOwner), LinearScope {
    override fun ViewGroup.LayoutParams.weight(weight: Float): LinearLayout.LayoutParams {
        return convert { LinearLayout.LayoutParams(it) }.also {
            it.weight = weight
        }
    }

    override fun ViewGroup.LayoutParams.gravity(vararg gravity: ViewGravity): LinearLayout.LayoutParams {
        return convert { LinearLayout.LayoutParams(it) }.also {
            it.gravity = gravity.sum()
        }
    }

}
