package com.lollipop.wear.maze.blocks

import android.view.LayoutInflater
import android.view.View
import com.lollipop.wear.blocksbuilding.BlocksOwner
import com.lollipop.wear.blocksbuilding.dsl.layoutParams
import com.lollipop.wear.blocksbuilding.item.ItemSize
import com.lollipop.wear.maze.databinding.ItemBlocksFooterBinding

class FooterBlockHolder(
    private val blocksOwner: BlocksOwner,
) {

    private val binding = ItemBlocksFooterBinding.inflate(LayoutInflater.from(blocksOwner.context))

    val view: View = binding.root

    init {
        view.layoutParams(ItemSize.Match, ItemSize.Wrap)
    }

}

fun BlocksOwner.footer(): View {
    return FooterBlockHolder(this).view
}