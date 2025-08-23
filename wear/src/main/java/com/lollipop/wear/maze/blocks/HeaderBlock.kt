package com.lollipop.wear.maze.blocks

import android.view.LayoutInflater
import android.view.View
import com.lollipop.play.core.controller.TimeDelegate
import com.lollipop.wear.blocksbuilding.dsl.layoutParams
import com.lollipop.wear.blocksbuilding.item.ItemSize
import com.lollipop.wear.maze.databinding.ItemBlocksTitleBinding

class HeaderBlockHolder(
    private val blocksOwner: BlocksOwner,
    private val title: String
) {

    private val binding = ItemBlocksTitleBinding.inflate(LayoutInflater.from(blocksOwner.context))

    val view: View = binding.root

    init {
        TimeDelegate.auto(blocksOwner.lifecycleOwner) { time ->
            updateTime(time)
        }
        binding.titleView.text = title
        view.layoutParams(ItemSize.Match, ItemSize.Wrap)
    }

    private fun updateTime(time: String) {
        binding.timeView.text = time
    }

}

fun BlocksOwner.titleHeader(title: String): View {
    return HeaderBlockHolder(this, title).view
}