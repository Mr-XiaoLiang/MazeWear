package com.lollipop.wear.blocksbuilding.view

import android.widget.LinearLayout
import androidx.lifecycle.LifecycleOwner
import com.lollipop.wear.blocksbuilding.BBDsl

fun ItemGroupScope<*>.column(content: LinearScope.() -> Unit): LinearScope {
    val view = add(LinearLayout(this.content.context))
    view.orientation = LinearLayout.VERTICAL
    val scope = LinearViewScope(view, lifecycleOwner)
    content.invoke(scope)
    return scope
}

fun ItemGroupScope<*>.row(content: LinearScope.() -> Unit): LinearScope {
    val view = add(LinearLayout(this.content.context))
    view.orientation = LinearLayout.HORIZONTAL
    val scope = LinearViewScope(view, lifecycleOwner)
    content.invoke(scope)
    return scope
}

@BBDsl
interface LinearScope : ItemGroupScope<LinearLayout>

class LinearViewScope(
    frameLayout: LinearLayout, lifecycleOwner: LifecycleOwner
) : BasicItemGroupScope<LinearLayout>(frameLayout, lifecycleOwner), LinearScope
