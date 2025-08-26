package com.lollipop.wear.blocksbuilding.view

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.lollipop.wear.blocksbuilding.BBDsl
import com.lollipop.wear.blocksbuilding.data.DataObserver
import com.lollipop.wear.blocksbuilding.dsl.convert
import com.lollipop.wear.blocksbuilding.item.MetricsValue
import com.lollipop.wear.blocksbuilding.item.PX

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

    val context: Context
        get() {
            return content.context
        }

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

    fun padding(padding: MetricsValue) {
        padding(padding, padding)
    }

    fun padding(horizontal: MetricsValue, vertical: MetricsValue) {
        padding(horizontal, vertical, horizontal, vertical)
    }

    fun padding(
        left: MetricsValue,
        top: MetricsValue,
        right: MetricsValue,
        bottom: MetricsValue
    )

}

@BBDsl
interface ItemGroupScope<G : ViewGroup> : ItemViewScope<G> {

    fun <T : View> add(view: T, layoutParams: ViewGroup.LayoutParams): T

}

@BBDsl
interface MarginGroupScope {

    fun ViewGroup.LayoutParams.margin(
        margin: MetricsValue,
    ): ViewGroup.MarginLayoutParams {
        return margin(margin, margin)
    }

    fun ViewGroup.LayoutParams.margin(
        horizontal: MetricsValue,
        vertical: MetricsValue,
    ): ViewGroup.MarginLayoutParams {
        return margin(horizontal, vertical, horizontal, vertical)
    }

    fun ViewGroup.LayoutParams.margin(
        left: MetricsValue,
        top: MetricsValue,
        right: MetricsValue,
        bottom: MetricsValue
    ): ViewGroup.MarginLayoutParams {
        return convert { ViewGroup.MarginLayoutParams(it) }.also {
            it.leftMargin = left.px
            it.topMargin = top.px
            it.rightMargin = right.px
            it.bottomMargin = bottom.px
        }
    }

}

open class BasicItemViewScope<V : View>(
    protected val view: V,
    override val lifecycleOwner: LifecycleOwner
) : ItemViewScope<V> {

    override val content: V = view

    protected val paddingArray = Array<MetricsValue>(4) { PX(0) }

    protected fun isSelf(scope: ItemViewScope<*>): Boolean {
        return scope.content === view
    }

    private var updateCallback: ((V) -> Unit)? = null

    override fun onUpdate(updateCallback: (V) -> Unit) {
        updatePadding()
        this.updateCallback = updateCallback
    }

    override fun padding(
        left: MetricsValue,
        top: MetricsValue,
        right: MetricsValue,
        bottom: MetricsValue
    ) {
        paddingArray[0] = left
        paddingArray[1] = top
        paddingArray[2] = right
        paddingArray[3] = bottom
        updatePadding()
    }

    private fun updatePadding() {
        view.setPadding(
            paddingArray[0].px,
            paddingArray[1].px,
            paddingArray[2].px,
            paddingArray[3].px
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

    override fun <T : View> add(view: T, layoutParams: ViewGroup.LayoutParams): T {
        this.view.addView(view, layoutParams)
        return view
    }
}