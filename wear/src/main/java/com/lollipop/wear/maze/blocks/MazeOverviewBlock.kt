package com.lollipop.wear.maze.blocks

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.lollipop.maze.data.MTreasure
import com.lollipop.play.core.view.MazeOverviewView
import com.lollipop.wear.blocksbuilding.BBDsl
import com.lollipop.wear.blocksbuilding.BuilderScope
import com.lollipop.wear.blocksbuilding.data.DataProvider
import com.lollipop.wear.blocksbuilding.dsl.ViewLayoutParams
import com.lollipop.wear.blocksbuilding.dsl.layoutParams
import com.lollipop.wear.blocksbuilding.item.DP
import com.lollipop.wear.blocksbuilding.item.ItemSize
import com.lollipop.wear.blocksbuilding.item.PX
import com.lollipop.wear.blocksbuilding.view.BasicItemViewScope
import com.lollipop.wear.blocksbuilding.view.Constraint
import com.lollipop.wear.blocksbuilding.view.ItemGroupScope
import com.lollipop.wear.blocksbuilding.view.ItemView
import com.lollipop.wear.blocksbuilding.view.ItemViewScope
import com.lollipop.wear.blocksbuilding.view.RoundRectShape
import com.lollipop.wear.maze.theme.MazeMapTheme

fun ItemGroupScope<*>.MazeOverview(
    layoutParams: ViewGroup.LayoutParams = ViewLayoutParams(),
    content: MazeOverviewScope.() -> Unit
): MazeOverviewScope {
    return MazeOverviewBlockScope(
        add(
            MazeOverviewView(
                this.content.context
            ).apply {
                MazeMapTheme.updateMaze(this)
            },
            layoutParams
        ), lifecycleOwner
    ).apply(content)
}

fun BuilderScope.MazeOverview(state: DataProvider<MTreasure>) {

    ItemView {
        content.layoutParams(ItemSize.Match, ItemSize.Wrap)
        Constraint(
            layoutParams = ViewLayoutParams(ItemSize.Match, ItemSize.Wrap)
        ) {
            MazeOverview(
                layoutParams = ViewLayoutParams(0.PX)
                    .ratio(1, 1)
                    .margin(vertical = 4.DP, horizontal = 0.PX)
                    .width(0.8F)
                    .control().startToParent().endToParent().topToParent().bottomToParent()
                    .complete()
            ) {
                background(0x30FFFFFF.toInt(), RoundRectShape(8.DP.toTypedValue()))
                state.remember {
                    setMap(it)
                }
            }
        }
    }
}

@BBDsl
interface MazeOverviewScope : ItemViewScope<MazeOverviewView> {
    fun setMap(treasure: MTreasure)

    fun setMin(lineWidthMin: Float, extremeRadiusMin: Float)

    fun setColor(lineColor: Int, extremeStartColor: Int, extremeEndColor: Int, mapColor: Int)

    fun setReverseDisplayMap(reverseDisplay: Boolean)

    fun setDisplayMap(display: Boolean)

    fun updatePath()
}

class MazeOverviewBlockScope(
    view: MazeOverviewView,
    lifecycleOwner: LifecycleOwner
) : BasicItemViewScope<MazeOverviewView>(view, lifecycleOwner), MazeOverviewScope {
    override fun setMap(treasure: MTreasure) {
        content.setMap(treasure)
    }

    override fun setMin(lineWidthMin: Float, extremeRadiusMin: Float) {
        content.setMin(lineWidthMin, extremeRadiusMin)
    }

    override fun setColor(
        lineColor: Int,
        extremeStartColor: Int,
        extremeEndColor: Int,
        mapColor: Int
    ) {
        content.setColor(lineColor, extremeStartColor, extremeEndColor, mapColor)
    }

    override fun setReverseDisplayMap(reverseDisplay: Boolean) {
        content.setReverseDisplayMap(reverseDisplay)
    }

    override fun setDisplayMap(display: Boolean) {
        content.setDisplayMap(display)
    }

    override fun updatePath() {
        content.updatePath()
    }
}