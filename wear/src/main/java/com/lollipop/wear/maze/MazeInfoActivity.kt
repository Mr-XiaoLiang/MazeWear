package com.lollipop.wear.maze

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import com.lollipop.play.core.data.DataManager
import com.lollipop.play.core.data.MazeHistory
import com.lollipop.wear.blocksbuilding.dsl.ViewLayoutParams
import com.lollipop.wear.blocksbuilding.dsl.blockStateOf
import com.lollipop.wear.blocksbuilding.dsl.layoutParams
import com.lollipop.wear.blocksbuilding.dsl.widthMatch
import com.lollipop.wear.blocksbuilding.item.DP
import com.lollipop.wear.blocksbuilding.item.ItemSize
import com.lollipop.wear.blocksbuilding.item.ViewTypedValue
import com.lollipop.wear.blocksbuilding.view.Box
import com.lollipop.wear.blocksbuilding.view.ItemView
import com.lollipop.wear.blocksbuilding.view.RoundRectShape
import com.lollipop.wear.maze.base.MazeActivityHelper
import com.lollipop.wear.maze.base.WearBlocksActivity
import com.lollipop.wear.maze.blocks.Footer
import com.lollipop.wear.maze.blocks.MazeOverviewBlockState
import com.lollipop.wear.maze.blocks.TitleHeader


class MazeInfoActivity : WearBlocksActivity() {

    companion object {
        fun start(context: Context, mazeCache: String) {
            MazeActivityHelper.startWithMaze<MazeInfoActivity>(context, mazeCache)
        }
    }

    private val mazeNameState = blockStateOf("")
    private val mazeSizeState = blockStateOf("")
    private val mazeTimeState = blockStateOf("")
    private val mazeStepsState = blockStateOf("")
    private val mazeOverviewState = MazeOverviewBlockState()

    private var mazeCache: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        content {
            TitleHeader(title = mazeNameState)
            ItemView {
                content.layoutParams(ItemSize.Match, ItemSize.Wrap)
                Box(
                    layoutParams = ViewLayoutParams().widthMatch()
                ) {
                    background(Color.RED, RoundRectShape(ViewTypedValue.Absolute(8.DP)))
                    TextView(context).apply {
                        text = "test"
                    }
                }
            }
            Footer()
        }
        mazeNameState.value = "ABCD"
//        initData()
    }

//    private fun onDeleteTimeEnd() {
//        DataManager.delete(mazeCache)
//        onBackPressedDispatcher.onBackPressed()
//    }
//
//    private fun onOpenClick() {
//        PlayActivity.resumeMaze(this, mazeCache)
//    }

    private fun initData() {
        val cache = MazeActivityHelper.findFromIntent(intent)
        mazeCache = cache
        DataManager.findByFile(cache)?.let {
            onMazeLoaded(it)
        }
    }

    private fun onMazeLoaded(mazeInfo: MazeHistory) {
        mazeNameState.value = mazeInfo.name.ifEmpty { "???" }
        mazeSizeState.value = mazeInfo.level
        mazeTimeState.value = mazeInfo.timeDisplay
        mazeStepsState.value = mazeInfo.pathLength.toString()
        mazeOverviewState.update(mazeInfo.maze, mazeInfo.path)
    }

}
