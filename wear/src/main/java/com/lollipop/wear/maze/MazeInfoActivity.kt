package com.lollipop.wear.maze

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import com.lollipop.play.core.data.DataManager
import com.lollipop.play.core.data.MazeHistory
import com.lollipop.play.core.helper.registerLog
import com.lollipop.wear.blocksbuilding.data.mutableData
import com.lollipop.wear.blocksbuilding.dsl.ViewLayoutParams
import com.lollipop.wear.blocksbuilding.dsl.heightWrap
import com.lollipop.wear.blocksbuilding.dsl.layoutParams
import com.lollipop.wear.blocksbuilding.dsl.widthMatch
import com.lollipop.wear.blocksbuilding.item.DP
import com.lollipop.wear.blocksbuilding.item.ItemSize
import com.lollipop.wear.blocksbuilding.view.Box
import com.lollipop.wear.blocksbuilding.view.ItemView
import com.lollipop.wear.blocksbuilding.view.RoundRectShape
import com.lollipop.wear.blocksbuilding.view.Text
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

    private val log = registerLog()

    private val mazeNameState = mutableData("")
    private val mazeSizeState = mutableData("")
    private val mazeTimeState = mutableData("")
    private val mazeStepsState = mutableData("")
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
                    padding(horizontal = 12.DP, vertical = 8.DP)
                    Text(
                        layoutParams = ViewLayoutParams().widthMatch().heightWrap()
                    ) {
                        text = "Maze Overview"
                        color(Color.WHITE)
                        background(Color.RED, RoundRectShape(8.DP.toTypedValue()))
                        padding(horizontal = 8.DP, vertical = 4.DP)
                    }
                }
            }
            Footer()
        }
        initData()
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

    override fun onResume() {
        super.onResume()
//        Handler(Looper.getMainLooper()).postDelayed({
//            log("onResume: ${printViewTree().toString(4)}")
//        }, 1000)
    }

}
