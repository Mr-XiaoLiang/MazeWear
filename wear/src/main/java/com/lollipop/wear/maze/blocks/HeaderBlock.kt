package com.lollipop.wear.maze.blocks

import android.graphics.Color
import com.lollipop.play.core.controller.TimeDelegate
import com.lollipop.wear.blocksbuilding.BuilderScope
import com.lollipop.wear.blocksbuilding.data.DataObserver
import com.lollipop.wear.blocksbuilding.dsl.ViewLayoutParams
import com.lollipop.wear.blocksbuilding.dsl.heightEmpty
import com.lollipop.wear.blocksbuilding.dsl.heightMatch
import com.lollipop.wear.blocksbuilding.dsl.layoutParams
import com.lollipop.wear.blocksbuilding.dsl.widthMatch
import com.lollipop.wear.blocksbuilding.item.DP
import com.lollipop.wear.blocksbuilding.item.ItemSize
import com.lollipop.wear.blocksbuilding.item.SP
import com.lollipop.wear.blocksbuilding.item.ViewGravity
import com.lollipop.wear.blocksbuilding.view.Constraint
import com.lollipop.wear.blocksbuilding.view.ItemView
import com.lollipop.wear.blocksbuilding.view.Text
import com.lollipop.wear.blocksbuilding.view.TextStyle

fun BuilderScope.TitleHeader(title: DataObserver<String>) {
    ItemView {
        content.layoutParams(ItemSize.Match, ItemSize.Wrap)
        Constraint(
            layoutParams = ViewLayoutParams().widthMatch().heightMatch()
        ) {
            Constraint(
                layoutParams = ViewLayoutParams().ratio(2, 1).widthMatch().heightEmpty()
            ) {
                Text(
                    layoutParams = ViewLayoutParams().widthMatch().heightMatch()
                ) {
                    gravity(ViewGravity.Bottom, ViewGravity.Start)
                    fontSize(24.SP)
                    padding(horizontal = 12.DP, vertical = 6.DP)
                    color(Color.WHITE)
                    fontStyle(TextStyle.Bold)
                    title.remember {
                        text = it
                    }
                }
                CurvedText(
                    layoutParams = ViewLayoutParams().ratio(1, 1).widthMatch().heightEmpty()
                        .control().startToParent().endToParent().topToParent().bottomToParent()
                        .complete()
                ) {
                    fontStyle(TextStyle.Bold)
                    color(Color.WHITE)
                    fontSize(11.SP)
                    TimeDelegate.auto(lifecycleOwner) { time ->
                        text = time
                    }
                }
            }
        }
    }
}