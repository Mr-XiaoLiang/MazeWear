package com.lollipop.wear.maze.blocks

import android.graphics.Color
import android.view.View
import androidx.activity.ComponentActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.widget.WearableLinearLayoutManager
import androidx.wear.widget.WearableRecyclerView
import com.lollipop.wear.blocksbuilding.BuilderScope
import com.lollipop.wear.blocksbuilding.data.DataProvider
import com.lollipop.wear.blocksbuilding.dsl.ActivityBlocksOwner
import com.lollipop.wear.blocksbuilding.dsl.layoutParams
import com.lollipop.wear.blocksbuilding.item.ItemSize
import com.lollipop.wear.blocksbuilding.withBlocks
import kotlin.math.abs
import kotlin.math.min

fun ComponentActivity.wearContent(
    content: BuilderScope.() -> Unit
) {
    val blocksOwner = ActivityBlocksOwner(this)
    setContentView(
        ConstraintLayout(this).also { constraint ->
            constraint.layoutParams(ItemSize.Match, ItemSize.Match)
            constraint.setBackgroundColor(Color.BLACK)
            constraint.addView(
                WearableRecyclerView(this).also { recyclerView ->
                    PagerSnapHelper().attachToRecyclerView(recyclerView)
                    recyclerView.layoutManager = WearableLinearLayoutManager(
                        this, CenterScrollingLayoutCallback()
                    )
                    recyclerView.isCircularScrollingGestureEnabled = false
                    recyclerView.isEdgeItemsCenteringEnabled = false
                    blocksOwner.withBlocks(recyclerView = recyclerView, content = content)
                },
                ConstraintLayout.LayoutParams(0, 0).apply {
                    topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                    startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                    dimensionRatio = "1:1"
                }
            )
        }
    )
}

class CenterScrollingLayoutCallback(val fillHeader: Boolean = true) :
    WearableLinearLayoutManager.LayoutCallback() {

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
        val yRelativeToCenterOffset = child.y / parent.height + centerOffset

        // Normalize for center
        var weight = abs(0.5f - yRelativeToCenterOffset)
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