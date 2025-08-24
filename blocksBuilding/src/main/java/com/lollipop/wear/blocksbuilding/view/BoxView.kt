package com.lollipop.wear.blocksbuilding.view

import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.LifecycleOwner
import com.lollipop.wear.blocksbuilding.BBDsl

fun ItemGroupScope<*>.box(content: BoxScope.() -> Unit): BoxScope {
    val view = add(FrameLayout(this.content.context))
    val scope = BoxViewScope(view, lifecycleOwner)
    content.invoke(scope)
    FrameLayout.LayoutParams(0, 0).gravity
    return scope
}

@BBDsl
interface BoxScope : ItemGroupScope<FrameLayout> {

    var ItemViewScope<*>.layoutGravity: Int

    var contentGravity: Int

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

    override var ItemViewScope<*>.layoutGravity: Int
        get() {
            if (this@BoxViewScope.isSelf(this)) {
                throw IllegalStateException("BoxViewScope.layoutGravity can only be used by child")
            }
            val layoutParams = content.layoutParams as FrameLayout.LayoutParams
            return layoutParams.gravity
        }
        set(value) {
            if (this@BoxViewScope.isSelf(this)) {
                throw IllegalStateException("BoxViewScope.layoutGravity can only be used by child")
            }
            updateChildGravity(content, value)
        }

    override var contentGravity: Int = 0
        set(value) {
            field = value
            updateChildGravity(value)
        }

    private fun updateChildGravity(gravity: Int) {
        val scope = this
        val frameLayout = scope.content
        val childCount = frameLayout.childCount
        for (i in 0 until childCount) {
            val child = frameLayout.getChildAt(i)
            updateChildGravity(child, gravity)
        }
    }

}

