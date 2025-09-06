package com.lollipop.wear.maze

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lollipop.maze.data.MTreasure
import com.lollipop.play.core.data.DataManager
import com.lollipop.play.core.data.MazeHistory
import com.lollipop.play.core.helper.registerLog
import com.lollipop.wear.blocksbuilding.data.mutableData
import com.lollipop.wear.blocksbuilding.data.staticData
import com.lollipop.wear.maze.base.MazeActivityHelper
import com.lollipop.wear.maze.blocks.Button
import com.lollipop.wear.maze.blocks.DelayButton
import com.lollipop.wear.maze.blocks.MazeOverview
import com.lollipop.wear.maze.blocks.ParameterItem
import com.lollipop.wear.maze.blocks.ProgressButtonStyle
import com.lollipop.wear.maze.blocks.ScaffoldBlock
import com.lollipop.wear.maze.blocks.wearContent


class MazeInfoActivity : AppCompatActivity() {

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
    private val mazeOverviewState = mutableData(MTreasure.EMPTY)

    private var mazeCache: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wearContent {
            ScaffoldBlock(title = mazeNameState) {
                ParameterItem(staticData(R.drawable.baseline_resize_24), mazeSizeState)
                ParameterItem(staticData(R.drawable.baseline_footprint_24), mazeStepsState)
                ParameterItem(
                    staticData(R.drawable.baseline_nest_clock_farsight_analog_24),
                    mazeTimeState
                )
                MazeOverview(mazeOverviewState)
                Button(
                    iconRes = staticData(R.drawable.baseling_sports_esports_24),
                    label = staticData(getString(R.string.label_open_maze)),
                    onClick = ::onOpenClick
                )
                DelayButton(
                    style = ProgressButtonStyle(
                        defIconRes = R.drawable.baseling_delete_24,
                        doneIconRes = R.drawable.baseling_done_24,
                        defLabel = getString(R.string.label_delete),
                        pendingLabel = getString(R.string.label_delete_cancel),
                        doneLabel = getString(R.string.label_delete_done)
                    ),
                    onClickDone = ::onDeleteTimeEnd
                )
            }
        }
        initData()
    }

    private fun onDeleteTimeEnd() {
        DataManager.delete(mazeCache)
        onBackPressedDispatcher.onBackPressed()
    }

    private fun onOpenClick() {
        PlayActivity.resumeMaze(this, mazeCache)
    }

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
        mazeOverviewState.value = mazeInfo.treasure
    }

    override fun onResume() {
        super.onResume()
//        Handler(Looper.getMainLooper()).postDelayed({
//            log("onResume: ${printViewTree().toString(4)}")
//        }, 1000)
    }

}
