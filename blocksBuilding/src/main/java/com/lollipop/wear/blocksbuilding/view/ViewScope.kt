package com.lollipop.wear.blocksbuilding.view

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.lollipop.wear.blocksbuilding.BBDsl
import com.lollipop.wear.blocksbuilding.data.DataObserver
import com.lollipop.wear.blocksbuilding.dsl.convert
import com.lollipop.wear.blocksbuilding.item.ItemSize
import com.lollipop.wear.blocksbuilding.item.ViewGravity
import com.lollipop.wear.blocksbuilding.item.ViewTypedValue

@BBDsl
interface ItemViewScope<V : View> {

    val viewId: Int
        get() {
            val id = content.id
            if (id == View.NO_ID) {
                val newId = View.generateViewId()
                content.id = newId
                return newId
            } else {
                return id
            }
        }

    val content: V

    val lifecycleOwner: LifecycleOwner

    fun notifyUpdate()

    fun onUpdate(updateCallback: (V) -> Unit)

    fun <T> remember(block: () -> DataObserver<T>): DataObserver<T> {
        val observer = block()
        observer.register {
            notifyUpdate()
        }
        return observer
    }

    fun padding(padding: ViewTypedValue) {
        padding(padding, padding)
    }

    fun padding(horizontal: ViewTypedValue, vertical: ViewTypedValue) {
        padding(horizontal, vertical, horizontal, vertical)
    }

    fun padding(
        left: ViewTypedValue,
        top: ViewTypedValue,
        right: ViewTypedValue,
        bottom: ViewTypedValue
    )

}

@BBDsl
interface ItemGroupScope<G : ViewGroup> : ItemViewScope<G> {

    fun <T : View> add(view: T, layoutParams: ViewGroup.LayoutParams): T

}

@BBDsl
interface MarginGroupScope {

    fun ViewGroup.LayoutParams.margin(
        left: ViewTypedValue.DP,
        top: ViewTypedValue.DP,
        right: ViewTypedValue.DP,
        bottom: ItemSize
    ): ViewGroup.MarginLayoutParams {
        return convert { ViewGroup.MarginLayoutParams(it) }.also {
            it.leftMargin = left.getValue(bounds)
        }
    }

}

open class BasicItemViewScope<V : View>(
    protected val view: V,
    override val lifecycleOwner: LifecycleOwner
) : ItemViewScope<V> {

    override val content: V = view

    protected val paddingArray = Array<ViewTypedValue>(4) { ViewTypedValue.PX(0) }

    protected fun isSelf(scope: ItemViewScope<*>): Boolean {
        return scope.content === view
    }

    private var updateCallback: ((V) -> Unit)? = null

    override fun onUpdate(updateCallback: (V) -> Unit) {
        updatePadding()
        this.updateCallback = updateCallback
    }

    override fun padding(
        left: ViewTypedValue,
        top: ViewTypedValue,
        right: ViewTypedValue,
        bottom: ViewTypedValue
    ) {
        paddingArray[0] = left
        paddingArray[1] = top
        paddingArray[2] = right
        paddingArray[3] = bottom
        view.post {
            updatePadding()
        }
    }

    private fun updatePadding() {
        val bounds = Rect()
        bounds.set(0, 0, view.width, view.height)
        view.setPadding(
            paddingArray[0].getValue(bounds),
            paddingArray[1].getValue(bounds),
            paddingArray[2].getValue(bounds),
            paddingArray[3].getValue(bounds)
        )
    }

    override fun notifyUpdate() {
        this.updateCallback?.invoke(view)
    }

}

open class BasicItemGroupScope<G : ViewGroup>(
    view: G,
    lifecycleOwner: LifecycleOwner
) : BasicItemViewScope<G>(view, lifecycleOwner), ItemGroupScope<G> {

    protected fun Array<out ViewGravity>.sum(): Int {
        var result = 0
        for (gravity in this) {
            result = gravity.or(result)
        }
        return result
    }

    override fun <T : View> add(view: T, layoutParams: ViewGroup.LayoutParams): T {
        this.view.addView(view, layoutParams)
        return view
    }
}