package com.lollipop.wear.maze.blocks

import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.activity.ComponentActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.widget.WearableLinearLayoutManager
import androidx.wear.widget.WearableRecyclerView
import com.lollipop.wear.blocksbuilding.BlocksOwner
import com.lollipop.wear.blocksbuilding.BuilderScope
import com.lollipop.wear.blocksbuilding.data.DataProvider
import com.lollipop.wear.blocksbuilding.dsl.createBlocksOwner
import com.lollipop.wear.blocksbuilding.dsl.layoutParams
import com.lollipop.wear.blocksbuilding.item.DP
import com.lollipop.wear.blocksbuilding.item.ItemSize
import com.lollipop.wear.blocksbuilding.item.SP
import com.lollipop.wear.blocksbuilding.view.TextStyle
import com.lollipop.wear.blocksbuilding.withBlocks
import com.lollipop.wear.maze.widget.CircularScrollIndicator
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

fun ComponentActivity.wearContent(
    snapEnable: Boolean = false,
    content: BuilderScope.() -> Unit
) {
    setContentView(wearBlocksView(snapEnable = snapEnable, content = content))
}

fun ComponentActivity.wearBlocksView(
    snapEnable: Boolean = false,
    content: BuilderScope.() -> Unit
): View {
    return createBlocksOwner().wearBlocksView(snapEnable = snapEnable, content = content)
}

fun Fragment.wearBlocksView(
    context: Context? = null,
    snapEnable: Boolean = false,
    content: BuilderScope.() -> Unit
): View {
    return createBlocksOwner(context = context).wearBlocksView(
        snapEnable = snapEnable,
        content = content
    )
}

fun BlocksOwner.wearBlocksView(
    snapEnable: Boolean = false,
    content: BuilderScope.() -> Unit
): View {
    return ConstraintLayout(context).also { constraint ->
        constraint.layoutParams(ItemSize.Match, ItemSize.Match)
        constraint.setBackgroundColor(Color.BLACK)
        val wearableRecyclerView = WearableRecyclerView(context).also { recyclerView ->
            if (snapEnable) {
                PagerSnapHelper().attachToRecyclerView(recyclerView)
            }
            recyclerView.id = View.generateViewId()
            recyclerView.layoutManager = WearableLinearLayoutManager(
                context, CenterScrollingLayoutCallback()
            )
            recyclerView.isCircularScrollingGestureEnabled = false
            recyclerView.isEdgeItemsCenteringEnabled = false
            withBlocks(recyclerView = recyclerView, content = content)
        }
        constraint.addView(
            wearableRecyclerView,
            ConstraintLayout.LayoutParams(0, 0).apply {
                topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                dimensionRatio = "1:1"
            }
        )
        val recyclerViewId = wearableRecyclerView.id
        constraint.addView(
            CircularScrollIndicator(context).also { indicator ->
                indicator.attachTo(wearableRecyclerView)
                indicator.thumbBackground = 0x33FFFFFF
                indicator.thumbColor = Color.WHITE
                indicator.thumbWidth = 3.DP.px.toFloat()
                indicator.backgroundWidth = 1.DP.px.toFloat()
                indicator.startAngle = -30f
                indicator.sweepAngle = 60f
                val padding = 2.DP.px
                indicator.setPadding(padding, padding, padding, padding)
            },
            ConstraintLayout.LayoutParams(0, 0).apply {
                topToTop = recyclerViewId
                bottomToBottom = recyclerViewId
                startToStart = recyclerViewId
                endToEnd = recyclerViewId
            }
        )
    }
}

class CenterScrollingLayoutCallback(
    val fillHeader: Boolean = true
) : WearableLinearLayoutManager.LayoutCallback() {

    companion object {
        private const val MAX_ICON_PROGRESS = 0.65f
    }

    override fun onLayoutFinished(child: View, parent: RecyclerView) {
        child.apply {
            var progressToCenter = 0f
            if (fillHeader) {
                val viewHolder = parent.getChildViewHolder(child)
                val position = viewHolder.absoluteAdapterPosition
                if (position == 0) {
                    // 这是第一个
                    // Normalize for center
                    progressToCenter = abs((y / height.toFloat()) / 2)
                    // Adjust to the maximum scale
                    progressToCenter = min(progressToCenter, MAX_ICON_PROGRESS)
                } else {
                    progressToCenter = defaultViewScale(child, parent)
                }
            } else {
                progressToCenter = defaultViewScale(child, parent)
            }
            scaleX = 1 - progressToCenter
            scaleY = 1 - progressToCenter
        }

    }

    private fun defaultViewScale(child: View, parent: RecyclerView): Float {
        // Figure out % progress from top to bottom
        val centerOffset = child.height.toFloat() / 2.0f / parent.height.toFloat()

        // [0, 1]
        val yRelativeToCenterOffset = max(0F, min(1F, child.y / parent.height + centerOffset))
        // 范围 [0, 0.5]
        var weight = abs(0.5f - yRelativeToCenterOffset)
        // 根据勾股定理计算一个曲线，weight就是X，半径为斜边，那么Y就是缩放比例。范围 [0, 0.5]，但是顺序相反
        val d = (0.5 * 0.5) - (weight * weight)
        if (d == 0.0) {
            return MAX_ICON_PROGRESS
        }
        weight = 1F - (sqrt(d) * 2).toFloat()
        // Adjust to the maximum scale
        weight = min(weight, MAX_ICON_PROGRESS)
        return weight
    }

}

fun BuilderScope.ScaffoldBlock(
    title: DataProvider<String>,
    content: BuilderScope.() -> Unit
) {
    TitleHeader(title)
    content()
    Footer()
}

fun CurvedTextScope.TimeStyle() {
    fontStyle(TextStyle.Bold)
    color(Color.WHITE)
    fontSize(14.SP)
}
