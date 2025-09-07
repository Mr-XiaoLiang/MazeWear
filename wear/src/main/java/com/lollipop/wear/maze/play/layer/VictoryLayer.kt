package com.lollipop.wear.maze.play.layer

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.lollipop.maze.data.MTreasure
import com.lollipop.wear.blocksbuilding.data.mutableData
import com.lollipop.wear.blocksbuilding.data.staticData
import com.lollipop.wear.blocksbuilding.dsl.ViewLayoutParams
import com.lollipop.wear.blocksbuilding.dsl.layoutParams
import com.lollipop.wear.blocksbuilding.item.DP
import com.lollipop.wear.blocksbuilding.item.ItemSize
import com.lollipop.wear.blocksbuilding.item.SP
import com.lollipop.wear.blocksbuilding.view.Constraint
import com.lollipop.wear.blocksbuilding.view.ItemView
import com.lollipop.wear.maze.R
import com.lollipop.wear.maze.blocks.Button
import com.lollipop.wear.maze.blocks.CurvedText
import com.lollipop.wear.maze.blocks.Footer
import com.lollipop.wear.maze.blocks.MazeOverview
import com.lollipop.wear.maze.blocks.ParameterItem
import com.lollipop.wear.maze.blocks.wearBlocksView
import com.lollipop.wear.maze.play.state.PlayPageState
import com.lollipop.wear.maze.theme.MazeMapTheme

class VictoryLayer(activity: AppCompatActivity) : BasicLayer(activity) {

    private val mazeStepState = mutableData("")
    private val overviewState = mutableData(MTreasure.EMPTY)


    private var callback: Callback? = null

    override fun createView(
        container: ViewGroup
    ): View {
        return activity.wearBlocksView {
            ItemView {
                content.layoutParams(ItemSize.Match, ItemSize.Wrap)
                Constraint(
                    layoutParams = ViewLayoutParams(ItemSize.Match, ItemSize.Wrap)
                ) {
                    val victoryHint = CurvedText(
                        layoutParams = ViewLayoutParams(ItemSize.Match, ItemSize.Empty)
                            .ratio(1, 1)
                            .control().topToParent().startToParent().endToParent().complete()
                    ) {
                        padding(5.DP)
                        fontSize(26.SP)
                        color(Color.WHITE)
                        text(R.string.title_victory)
                    }
                    MazeOverview(
                        layoutParams = ViewLayoutParams(ItemSize.Empty, ItemSize.Empty)
                            .ratio(1, 1)
                            .width(0.5F)
                            .control().topToTopOf(victoryHint).startToStartOf(victoryHint)
                            .endToEndOf(victoryHint).bottomToBottomOf(victoryHint).complete()
                    ) {
                        MazeMapTheme.updateMaze(content)
                        overviewState.remember {
                            setMap(it)
                        }
                    }
                }
            }
            ParameterItem(
                staticData(R.drawable.baseline_nest_clock_farsight_analog_24),
                mazeStepState
            )
            Button(
                label = staticData(activity.getString(R.string.button_continue)),
                onClick = {
                    onContinueClick()
                }
            )
            Button(
                label = staticData(activity.getString(R.string.button_back)),
                onClick = {
                    activity.onBackPressedDispatcher.onBackPressed()
                }
            )
            Footer()
        }
    }

    private fun onContinueClick() {
        callback?.onContinue()
    }

    override fun onAttach() {
        super.onAttach()
        if (activity is Callback) {
            callback = activity
        }
    }

    override fun setState(state: PlayPageState) {
        if (state is PlayPageState.Complete) {
            mazeStepState.value = state.treasure.path.size.toString()
            overviewState.value = state.treasure
        }
    }

    interface Callback {
        fun onContinue()
    }

}