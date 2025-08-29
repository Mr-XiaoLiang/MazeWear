package com.lollipop.wear.maze.blocks

import com.lollipop.maze.MazeMap
import com.lollipop.maze.data.MMap
import com.lollipop.maze.data.MPath
import com.lollipop.maze.data.MPoint
import com.lollipop.play.core.view.MazeOverviewView
import com.lollipop.wear.blocksbuilding.BuilderScope
import com.lollipop.wear.blocksbuilding.data.DataProvider
import com.lollipop.wear.blocksbuilding.dsl.ViewLayoutParams
import com.lollipop.wear.blocksbuilding.dsl.layoutParams
import com.lollipop.wear.blocksbuilding.item.DP
import com.lollipop.wear.blocksbuilding.item.ItemSize
import com.lollipop.wear.blocksbuilding.item.PX
import com.lollipop.wear.blocksbuilding.view.Constraint
import com.lollipop.wear.blocksbuilding.view.ItemView
import com.lollipop.wear.blocksbuilding.view.RoundRectShape
import com.lollipop.wear.blocksbuilding.view.ViewBackground

fun BuilderScope.MazeOverview(state: DataProvider<MazeOverviewData>) {

    // background(color = Color.Black, shape = RoundedCornerShape(8.dp))
    //                        .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(8.dp))

    ItemView {
        content.layoutParams(ItemSize.Match, ItemSize.Wrap)
        Constraint(
            layoutParams = ViewLayoutParams(ItemSize.Match, ItemSize.Wrap)
        ) {
            add(
                MazeOverviewView(context).apply {
                    background = ViewBackground.ByColor(
                        shape = RoundRectShape(8.DP.toTypedValue()),
                        color = 0x30FFFFFF.toInt()
                    )
                    state.remember {
                        setMap(it.map, it.path)
                    }
                },
                layoutParams = ViewLayoutParams(0.PX)
                    .ratio(1, 1)
                    .margin(vertical = 4.DP, horizontal = 0.PX)
                    .width(0.8F)
                    .control().startToParent().endToParent().topToParent().bottomToParent()
                    .complete()
            )
        }
    }
}

class MazeOverviewData(
    val map: MazeMap,
    val path: MPath
) {

    companion object {
        val EMPTY = MazeOverviewData(
            MazeMap(MPoint(0, 0), MPoint(0, 0), MMap(0, 0)),
            MPath()
        )
    }

}
