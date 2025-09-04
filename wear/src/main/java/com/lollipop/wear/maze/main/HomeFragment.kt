package com.lollipop.wear.maze.main

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lollipop.play.core.data.DataManager
import com.lollipop.play.core.data.DataObserver
import com.lollipop.play.core.data.MazeHistory
import com.lollipop.wear.blocksbuilding.BuilderScope
import com.lollipop.wear.blocksbuilding.data.ListDataProvider
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
import com.lollipop.wear.maze.MazeInfoActivity
import com.lollipop.wear.maze.PlayActivity
import com.lollipop.wear.maze.R
import com.lollipop.wear.maze.blocks.ScaffoldBlock
import com.lollipop.wear.maze.blocks.wearBlocksView

class HomeFragment : MainBaseFragment() {

    private val dataObserver by lazy {
        DataObserver(::onDataChanged)
    }

    private val mazeHistoryProvider = ListDataProvider<MazeHistory>()

    private val dataObserverController by lazy {
        dataObserver.controllerByManual()
    }

    private val mazeList = mutableListOf<MazeHistory>()

    private val mazeSizeDisplay = mutableData("")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return wearBlocksView(inflater.context) {
            ScaffoldBlock(
                title = staticData(blocksOwner.context.getString(R.string.app_name))
            ) {
                NewGameItem()
                items(
                    provider = mazeHistoryProvider
                ) {
                    MazeHolder(
                        onItemClick = ::onMazeHistoryClick,
                        onItemLongClick = ::onMazeHistoryLongClick
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        context?.let { c ->
            val mazeWidth = optSettings(c).mazeWidth
            mazeSizeDisplay.value = "${mazeWidth}x${mazeWidth}"
        }
        dataObserverController.resume()
    }

    override fun onPause() {
        super.onPause()
        dataObserverController.pause()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onDataChanged() {
        dataObserver.releasePending()
        DataManager.copyListUnfinished(mazeList)
        log("onDataChanged: ${mazeList.size}")
        mazeHistoryProvider.reset(mazeList)
    }

    private fun onNewGameClick() {
        activity?.let {
            val mazeWidth = optSettings(it).mazeWidth
            PlayActivity.newMaze(it, mazeWidth)
        }
    }

    private fun onMazeHistoryClick(position: Int, mazeHistory: MazeHistory) {
        activity?.let {
            PlayActivity.resumeMaze(it, mazeHistory.cachePath)
        }
    }

    private fun onMazeHistoryLongClick(position: Int, mazeHistory: MazeHistory): Boolean {
        val act = activity
        if (act != null) {
            MazeInfoActivity.start(act, mazeHistory.cachePath)
            return true
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        dataObserver.destroy()
    }

    private fun BuilderScope.NewGameItem() {
        ItemView {
            content.layoutParams(ItemSize.Match, ItemSize.Wrap)
            padding(horizontal = 12.DP, vertical = 6.DP)
            Constraint(
                layoutParams = ViewLayoutParams(ItemSize.Match, ItemSize.Wrap)
            ) {
                onClick {
                    onNewGameClick()
                }
                background(R.drawable.bg_item_main_maze)
                backgroundTint(0xFF489E8F.toInt())
                padding(6.DP)

                val icon = Image(
                    layoutParams = ViewLayoutParams(36.DP)
                        .margin(end = 6.DP)
                        .control().bottomToParent().topToParent().endToParent().complete()
                ) {
                    src(R.drawable.baseline_play_circle_24)
                    tint(Color.WHITE)
                }
                val title = Text(
                    layoutParams = ViewLayoutParams(ItemSize.Empty, ItemSize.Wrap)
                        .margin(start = 4.DP, end = 6.DP)
                        .control().startToParent().topToParent().endToStartOf(icon).complete()
                ) {
                    fontSize(18.SP)
                    fontStyle(TextStyle.Bold)
                    color(Color.WHITE)
                    text(R.string.title_new_game)
                }
                Text(
                    layoutParams = ViewLayoutParams(ItemSize.Empty, ItemSize.Wrap)
                        .control().startToStartOf(title).topToBottomOf(title).endToEndOf(title)
                        .complete()
                ) {
                    fontSize(11.SP)
                    fontByResource(com.lollipop.play.core.R.font.leckerli_one_regular)
                    color(Color.WHITE)
                    mazeSizeDisplay.remember {
                        text = it
                    }
                }
            }
        }
    }

}