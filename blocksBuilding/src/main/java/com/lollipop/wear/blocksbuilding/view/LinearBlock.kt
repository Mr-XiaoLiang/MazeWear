package com.lollipop.wear.blocksbuilding.view

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.LifecycleOwner
import com.lollipop.wear.blocksbuilding.BBDsl
import com.lollipop.wear.blocksbuilding.dsl.ViewLayoutParams
import com.lollipop.wear.blocksbuilding.dsl.convert
import com.lollipop.wear.blocksbuilding.item.ViewGravity
import com.lollipop.wear.blocksbuilding.item.sum

fun ItemGroupScope<*>.Column(
    layoutParams: ViewGroup.LayoutParams = ViewLayoutParams(),
    content: LinearScope.() -> Unit
): LinearScope {
    val view = add(LinearLayout(this.content.context), layoutParams)
    view.orientation = LinearLayout.VERTICAL
    return LinearBlockScope(view, lifecycleOwner).apply(content)
}

fun ItemGroupScope<*>.Row(
    layoutParams: ViewGroup.LayoutParams = ViewLayoutParams(),
    content: LinearScope.() -> Unit
): LinearScope {
    val view = add(LinearLayout(this.content.context), layoutParams)
    view.orientation = LinearLayout.HORIZONTAL
    return LinearBlockScope(view, lifecycleOwner).apply(content)
}

@BBDsl
interface LinearScope : ItemGroupScope<LinearLayout>, MarginGroupScope {

    fun ViewGroup.LayoutParams.weight(weight: Float): LinearLayout.LayoutParams

    fun ViewGroup.LayoutParams.gravity(vararg gravity: ViewGravity): LinearLayout.LayoutParams

}

class LinearBlockScope(
    frameLayout: LinearLayout, lifecycleOwner: LifecycleOwner
) : BasicItemGroupScope<LinearLayout>(frameLayout, lifecycleOwner), LinearScope {
    override fun ViewGroup.LayoutParams.weight(weight: Float): LinearLayout.LayoutParams {
        return convert { LinearLayout.LayoutParams(it) }.also {
            it.weight = weight
        }
    }

    override fun ViewGroup.LayoutParams.gravity(vararg gravity: ViewGravity): LinearLayout.LayoutParams {
        return convert {
            if (it is ViewGroup.MarginLayoutParams) {
                LinearLayout.LayoutParams(it)
            } else {
                LinearLayout.LayoutParams(it)
            }
        }.also {
            it.gravity = gravity.sum()
        }
    }

}
