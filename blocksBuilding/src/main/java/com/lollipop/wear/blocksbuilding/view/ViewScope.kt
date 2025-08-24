package com.lollipop.wear.blocksbuilding.view

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.lollipop.wear.blocksbuilding.BBDsl
import com.lollipop.wear.blocksbuilding.data.DataObserver
import com.lollipop.wear.blocksbuilding.item.ViewTypedValue

@BBDsl
interface ItemViewScope<V : View> {

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

    fun <T : View> add(view: T): T

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
    override fun <T: View> add(view: T): T {
        this.view.addView(view)
        return view
    }
}