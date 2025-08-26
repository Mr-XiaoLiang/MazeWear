package com.lollipop.wear.maze.blocks

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import com.lollipop.play.core.controller.TimeDelegate
import com.lollipop.wear.blocksbuilding.BlocksOwner
import com.lollipop.wear.blocksbuilding.BuilderScope
import com.lollipop.wear.blocksbuilding.data.DataObserver
import com.lollipop.wear.blocksbuilding.dsl.ViewLayoutParams
import com.lollipop.wear.blocksbuilding.dsl.getValue
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

fun BuilderScope.TitleHeader(title: DataObserver<String>) {
    ItemView {
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
                        .control().startToParent().endToParent().topToParent().bottomToParent().complete()
                ) {
                    fontStyle(TextStyle.Bold)
                    color(Color.WHITE)
                    fontSize(11.SP)
                }
            }
        }
    }
}