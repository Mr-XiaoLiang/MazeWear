package com.lollipop.wear.maze.blocks

import com.lollipop.wear.blocksbuilding.BuilderScope
import com.lollipop.wear.blocksbuilding.dsl.ViewLayoutParams
import com.lollipop.wear.blocksbuilding.dsl.heightEmpty
import com.lollipop.wear.blocksbuilding.dsl.heightMatch
import com.lollipop.wear.blocksbuilding.dsl.widthMatch
import com.lollipop.wear.blocksbuilding.view.Constraint
import com.lollipop.wear.blocksbuilding.view.ItemView
import com.lollipop.wear.blocksbuilding.view.Space


fun BuilderScope.Footer() {
    ItemView {
        Constraint(
            layoutParams = ViewLayoutParams().widthMatch().heightMatch()
        ) {
            Constraint(
                layoutParams = ViewLayoutParams().ratio(2, 1).widthMatch().heightEmpty()
            ) {
                Space(
                    layoutParams = ViewLayoutParams().widthMatch().heightMatch()
                ) { }
            }
        }
    }
}