package com.lollipop.wear.maze.blocks

import android.view.View
import android.widget.Space
import com.lollipop.wear.blocksbuilding.dsl.layoutParams
import com.lollipop.wear.blocksbuilding.item.ItemSize

class FooterBlockHolder(
    private val blocksOwner: BlocksOwner,
    private val width: ItemSize,
    private val height: ItemSize
) {

    private val spaceView = Space(blocksOwner.context)

    val view: View = spaceView

    init {
        spaceView.layoutParams(width, height)
    }

}

fun BlocksOwner.space(width: ItemSize = ItemSize.Px(1), height: ItemSize = ItemSize.Px(1)): View {
    return FooterBlockHolder(this, width, height).view
}