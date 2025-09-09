package com.lollipop.wear.maze.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lollipop.play.core.kit.MazeSizeActivity
import com.lollipop.wear.blocksbuilding.BuilderScope
import com.lollipop.wear.blocksbuilding.data.DataProvider
import com.lollipop.wear.blocksbuilding.data.mutableData
import com.lollipop.wear.blocksbuilding.data.staticData
import com.lollipop.wear.blocksbuilding.dsl.ViewLayoutParams
import com.lollipop.wear.blocksbuilding.dsl.layoutParams
import com.lollipop.wear.blocksbuilding.item.DP
import com.lollipop.wear.blocksbuilding.item.ItemSize
import com.lollipop.wear.blocksbuilding.item.SP
import com.lollipop.wear.blocksbuilding.view.Constraint
import com.lollipop.wear.blocksbuilding.view.Image
import com.lollipop.wear.blocksbuilding.view.ItemView
import com.lollipop.wear.blocksbuilding.view.Text
import com.lollipop.wear.blocksbuilding.view.TextStyle
import com.lollipop.wear.maze.R
import com.lollipop.wear.maze.blocks.ScaffoldBlock
import com.lollipop.wear.maze.blocks.wearBlocksView

class ConfigFragment : MainBaseFragment() {

    private var appVersionState = mutableData("")
    private var mazeSizeState = mutableData("")
    private var themeState = mutableData("None")

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appVersionState.value = context.packageManager.getPackageInfo(
            context.packageName,
            0
        ).versionName ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return wearBlocksView(inflater.context) {
            ScaffoldBlock(
                title = staticData(blocksOwner.context.getString(R.string.title_config))
            ) {
                Item(
                    labelRes = R.string.label_map_size,
                    summaryState = mazeSizeState,
                    hasArrow = true
                ) {
                    activity?.let {
                        it.startActivity(Intent(it, MazeSizeActivity::class.java))
                    }
                }
//                Item(
//                    labelRes = R.string.label_theme,
//                    summaryState = themeState,
//                    hasArrow = true
//                ) {
//                    // TODO
//                }
                Item(
                    labelRes = R.string.label_about,
                    summaryState = appVersionState,
                    hasArrow = false
                ) {
                    // TODO
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        context?.let { c ->
            mazeSizeState.value = optSettings(c).mazeWidth.let { "${it}x${it}" }
        }
    }

    private fun BuilderScope.Item(
        labelRes: Int,
        summaryState: DataProvider<String>,
        hasArrow: Boolean = true,
        onItemClick: () -> Unit
    ) {
        Item(
            labelState = staticData(blocksOwner.context.getString(labelRes)),
            summaryState = summaryState,
            hasArrow = hasArrow,
            onItemClick = onItemClick
        )
    }

    private fun BuilderScope.Item(
        labelState: DataProvider<String>,
        summaryState: DataProvider<String>,
        hasArrow: Boolean = true,
        onItemClick: () -> Unit
    ) {
        ItemView {
            content.layoutParams(ItemSize.Match, ItemSize.Wrap)
            padding(horizontal = 12.DP, vertical = 6.DP)
            Constraint(
                layoutParams = ViewLayoutParams(ItemSize.Match, ItemSize.Wrap)
            ) {
                onClick(onItemClick)
                background(R.drawable.bg_item_main_maze)
                padding(6.DP)

                val icon = Image(
                    layoutParams = ViewLayoutParams(24.DP)
                        .margin(end = 6.DP)
                        .control().bottomToParent().topToParent().endToParent().complete()
                ) {
                    src(R.drawable.baseline_chevron_forward_24)
                    tint(Color.WHITE)
                    isVisible = hasArrow
                }
                val title = Text(
                    layoutParams = ViewLayoutParams(ItemSize.Empty, ItemSize.Wrap)
                        .margin(start = 4.DP, end = 6.DP)
                ) {
                    fontSize(14.SP)
                    fontStyle(TextStyle.Bold)
                    color(Color.WHITE)
                    labelState.remember {
                        text = it
                    }
                }
                val summary = Text(
                    layoutParams = ViewLayoutParams(ItemSize.Empty, ItemSize.Wrap)
                ) {
                    fontSize(11.SP)
                    color(Color.WHITE)
                    fontByResource(com.lollipop.play.core.R.font.leckerli_one_regular)
                    summaryState.remember {
                        text = it
                    }
                }
                title.control {
                    topToParent()
                    startToParent()
                    endToStartOf(icon)
                    bottomToTopOf(summary)
                }
                summary.control {
                    topToBottomOf(title)
                    startToStartOf(title)
                    endToEndOf(title)
                    bottomToParent()
                }
            }
        }
    }

}