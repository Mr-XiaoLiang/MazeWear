package com.lollipop.wear.blocksbuilding.view

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.wear.blocksbuilding.BBDsl
import com.lollipop.wear.blocksbuilding.BuilderScope
import com.lollipop.wear.blocksbuilding.RecyclerHolder
import com.lollipop.wear.blocksbuilding.data.DataProvider
import com.lollipop.wear.blocksbuilding.data.MutableDataObserver
import com.lollipop.wear.blocksbuilding.data.mutableData
import com.lollipop.wear.blocksbuilding.dsl.bbLog
import com.lollipop.wear.blocksbuilding.dsl.convert
import com.lollipop.wear.blocksbuilding.dsl.layoutParams
import com.lollipop.wear.blocksbuilding.item.ItemSize
import com.lollipop.wear.blocksbuilding.item.ViewGravity
import com.lollipop.wear.blocksbuilding.item.sum

fun <T : Any> BuilderScope.ViewHolder(
    content: RecyclerHolderScope<T>.() -> Unit
): RecyclerHolder<T> {
    return RecyclerHolderBlock<T>(
        FrameLayout(blocksOwner.context).apply {
            layoutParams(ItemSize.Match, ItemSize.Wrap)
        },
        blocksOwner.lifecycleOwner
    ).apply(content)
}

@BBDsl
interface RecyclerHolderScope<T : Any> : BoxScope, RecyclerHolder<T> {

    val itemState: DataProvider<T?>

}

class RecyclerHolderBlock<T : Any>(
    frameLayout: FrameLayout,
    lifecycleOwner: LifecycleOwner,
) : BasicItemGroupScope<FrameLayout>(frameLayout, lifecycleOwner), RecyclerHolderScope<T> {

    override val itemState: MutableDataObserver<T?> = mutableData(null)

    private val log = bbLog()

    override var position: Int = RecyclerView.NO_POSITION

    override val itemView: View
        get() {
            return view
        }

    override fun onUpdate(data: T) {
        itemState.value = data
    }

    override fun ViewGroup.LayoutParams.gravity(
        vararg gravity: ViewGravity
    ): FrameLayout.LayoutParams {
        return convert {
            if (it is ViewGroup.MarginLayoutParams) {
                FrameLayout.LayoutParams(it)
            } else {
                FrameLayout.LayoutParams(it)
            }
        }.also {
            it.gravity = gravity.sum()
        }
    }

}