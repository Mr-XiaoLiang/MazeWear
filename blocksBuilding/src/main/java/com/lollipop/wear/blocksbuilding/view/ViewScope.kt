package com.lollipop.wear.blocksbuilding.view

import android.content.Context
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import com.lollipop.wear.blocksbuilding.BBDsl
import com.lollipop.wear.blocksbuilding.data.DataProvider
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

    var isVisible: Boolean
        get() {
            return content.isVisible
        }
        set(value) {
            content.isVisible = value
        }

    var isInvisible: Boolean
        get() {
            return content.isInvisible
        }
        set(value) {
            content.isInvisible = value
        }

    fun onClick(block: () -> Unit) {
        content.setOnClickListener {
            block()
        }
    }

    fun onLongClick(block: () -> Boolean) {
        content.setOnLongClickListener {
            block()
        }
    }

    fun notifyUpdate()

    fun onUpdate(updateCallback: (V) -> Unit)

    fun <T> remember(block: () -> DataProvider<T>): DataProvider<T> {
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

    fun background(color: Int, shape: ViewShape) {
        content.background = ViewBackground.ByColor(color, shape)
    }

    fun background(shape: ViewShape, shader: Shader) {
        content.background = ViewBackground.ByShader(shader, shape)
    }

    fun background(shape: ViewShape, vararg drawable: Drawable) {
        content.background = DrawableWrapper(shape, *drawable)
    }

    fun background(shape: ViewShape, @DrawableRes resId: Int) {
        ContextCompat.getDrawable(context, resId)?.let {
            content.background = DrawableWrapper(shape, it)
        }
    }

    fun background(@DrawableRes resId: Int) {
        content.background = ContextCompat.getDrawable(context, resId)
    }

    fun background(drawable: Drawable) {
        content.background = drawable
    }


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
        horizontal: MetricsValue = PX(0),
        vertical: MetricsValue = PX(0),
    ): ViewGroup.MarginLayoutParams {
        return margin(horizontal, vertical, horizontal, vertical)
    }

    fun ViewGroup.LayoutParams.margin(
        start: MetricsValue = PX(0),
        top: MetricsValue = PX(0),
        end: MetricsValue = PX(0),
        bottom: MetricsValue = PX(0)
    ): ViewGroup.MarginLayoutParams {
        return convert { ViewGroup.MarginLayoutParams(it) }.also {
            it.marginStart = start.px
            it.topMargin = top.px
            it.marginEnd = end.px
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