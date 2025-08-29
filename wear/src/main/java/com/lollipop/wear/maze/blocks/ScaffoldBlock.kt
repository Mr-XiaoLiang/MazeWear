package com.lollipop.wear.maze.blocks

import com.lollipop.wear.blocksbuilding.BuilderScope
import com.lollipop.wear.blocksbuilding.data.DataProvider

fun BuilderScope.ScaffoldBlock(
    title: DataProvider<String>,
    content: BuilderScope.() -> Unit
) {
    TitleHeader(title)
    content()
    Footer()
}