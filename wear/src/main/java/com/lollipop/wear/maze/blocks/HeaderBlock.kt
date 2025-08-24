package com.lollipop.wear.maze.blocks

import android.view.LayoutInflater
import android.view.View
import com.lollipop.play.core.controller.TimeDelegate
import com.lollipop.wear.blocksbuilding.BlocksOwner
import com.lollipop.wear.blocksbuilding.data.DataObserver
import com.lollipop.wear.blocksbuilding.dsl.getValue
import com.lollipop.wear.blocksbuilding.dsl.layoutParams
import com.lollipop.wear.blocksbuilding.item.ItemSize
import com.lollipop.wear.maze.databinding.ItemBlocksTitleBinding

class HeaderBlockHolder(
    private val blocksOwner: BlocksOwner,
    private val title: DataObserver<String>
) {

    private val binding = ItemBlocksTitleBinding.inflate(LayoutInflater.from(blocksOwner.context))

    val view: View = binding.root

    private val titleValue by title

    init {
        title.register {
            updateTitle()
        }
        TimeDelegate.auto(blocksOwner.lifecycleOwner) { time ->
            updateTime(time)
        }
        updateTitle()
        view.layoutParams(ItemSize.Match, ItemSize.Wrap)
    }

    private fun updateTitle() {
        binding.titleView.text = titleValue
    }

    private fun updateTime(time: String) {
        binding.timeView.text = time
    }

}

fun BlocksOwner.titleHeader(title: DataObserver<String>): View {
    return HeaderBlockHolder(this, title).view

}