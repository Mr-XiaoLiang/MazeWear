package com.lollipop.wear.maze.blocks

import com.lollipop.wear.blocksbuilding.BuilderScope
import com.lollipop.wear.blocksbuilding.dsl.ViewLayoutParams
import com.lollipop.wear.blocksbuilding.dsl.height
import com.lollipop.wear.blocksbuilding.dsl.widthMatch
import com.lollipop.wear.blocksbuilding.item.DP
import com.lollipop.wear.blocksbuilding.view.ItemView
import com.lollipop.wear.blocksbuilding.view.Space


fun BuilderScope.Space(spaceDp: DP) {
    ItemView {
        Space(
            layoutParams = ViewLayoutParams().widthMatch().height(spaceDp)
        ) { }
    }
}