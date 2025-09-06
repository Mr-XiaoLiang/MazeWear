package com.lollipop.wear.maze.play.layer

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.lollipop.maze.data.MTreasure
import com.lollipop.play.core.controller.TimeDelegate
import com.lollipop.wear.blocksbuilding.data.mutableData
import com.lollipop.wear.blocksbuilding.data.staticData
import com.lollipop.wear.blocksbuilding.dsl.ViewLayoutParams
import com.lollipop.wear.blocksbuilding.dsl.layoutParams
import com.lollipop.wear.blocksbuilding.dsl.widthEmpty
import com.lollipop.wear.blocksbuilding.item.DP
import com.lollipop.wear.blocksbuilding.item.ItemSize
import com.lollipop.wear.blocksbuilding.item.SP
import com.lollipop.wear.blocksbuilding.item.ViewGravity
import com.lollipop.wear.blocksbuilding.view.Constraint
import com.lollipop.wear.blocksbuilding.view.ConstraintScope
import com.lollipop.wear.blocksbuilding.view.Image
import com.lollipop.wear.blocksbuilding.view.ItemView
import com.lollipop.wear.blocksbuilding.view.RoundRectShape
import com.lollipop.wear.blocksbuilding.view.Space
import com.lollipop.wear.blocksbuilding.view.Text
import com.lollipop.wear.blocksbuilding.view.TextStyle
import com.lollipop.wear.maze.R
import com.lollipop.wear.maze.blocks.Button
import com.lollipop.wear.maze.blocks.CurvedText
import com.lollipop.wear.maze.blocks.Footer
import com.lollipop.wear.maze.blocks.MazeOverview
import com.lollipop.wear.maze.blocks.TimeStyle
import com.lollipop.wear.maze.blocks.wearBlocksView
import com.lollipop.wear.maze.play.state.PlayPageState

class MenuLayer(activity: AppCompatActivity) : BasicLayer(activity) {

    private var continueState: PlayPageState? = null
    private var gameControllerCallback: Callback? = null

    private val mazeState = mutableData(MTreasure.EMPTY)

    override fun onAttach() {
        super.onAttach()
        if (activity is Callback) {
            gameControllerCallback = activity
        }
    }

    private fun ConstraintScope.squareWithWidth() =
        ViewLayoutParams(ItemSize.Match, ItemSize.Empty)
            .ratio(1, 1)


    override fun createView(
        container: ViewGroup
    ): View {
        return activity.wearBlocksView {
            ItemView {
                content.layoutParams(ItemSize.Match, ItemSize.Wrap)
                onClick {
                    onContinue()
                }
                Constraint(
                    layoutParams = ViewLayoutParams(ItemSize.Match, ItemSize.Wrap)
                ) {
                    CurvedText(
                        layoutParams = squareWithWidth().control().topToParent().startToParent()
                            .bottomToParent()
                            .endToParent().complete()
                    ) {
                        TimeStyle()
                        TimeDelegate.auto(lifecycleOwner) { time ->
                            text = time
                        }
                    }
                    CurvedText(
                        layoutParams = squareWithWidth().control().topToParent().startToParent()
                            .bottomToParent()
                            .endToParent().complete()
                    ) {
                        isClockwise = false
                        TimeStyle()
                        rotation = 180F
                        padding(10.DP)
                        text(R.string.hint_parse)
                    }
                    MazeOverview(
                        layoutParams = squareWithWidth()
                            .widthEmpty()
                            .width(0.7F)
                            .control().topToParent().startToParent().endToParent().bottomToParent()
                            .complete()
                    ) {
                        background(
                            shape = RoundRectShape(8.DP.toTypedValue()),
                            color = 0x30FFFFFF.toInt()
                        )
                        mazeState.remember {
                            setMap(it)
                        }
                    }
                }
            }
            ItemView {
                onClick {
                    onContinue()
                }
                content.layoutParams(ItemSize.Match, ItemSize.Wrap)
                Constraint(
                    layoutParams = ViewLayoutParams(ItemSize.Match, ItemSize.Wrap)
                ) {
                    val spaceBlock = Space(
                        squareWithWidth()
                            .control().topToParent().startToParent().endToParent().bottomToParent()
                            .complete()
                    )
                    val osdBlock = Image(
                        layoutParams = squareWithWidth().widthEmpty().width(0.6F)
                            .control().topToTopOf(spaceBlock).startToStartOf(spaceBlock)
                            .endToEndOf(spaceBlock).bottomToBottomOf(spaceBlock).complete()
                    ) {
                        src(R.drawable.bg_play_osd_zone)
                        tint(Color.GRAY)
                    }
                    // 标题
                    Text(
                        layoutParams = ViewLayoutParams(ItemSize.Wrap, ItemSize.Wrap)
                            .control().topToTopOf(spaceBlock).startToParent().endToParent()
                            .bottomToTopOf(osdBlock).complete()
                    ) {
                        fontSize(16.SP)
                        fontStyle(TextStyle.Bold)
                        color(Color.WHITE)
                        text(R.string.label_operation_method)
                    }
                    // 向左
                    Text(
                        layoutParams = ViewLayoutParams(ItemSize.Wrap, ItemSize.Wrap)
                            .control().topToTopOf(osdBlock).startToStartOf(osdBlock)
                            .bottomToBottomOf(osdBlock).complete()
                    ) {
                        fontSize(14.SP)
                        gravity(ViewGravity.Center)
                        text(R.string.label_action_left)
                        drawableTint(Color.WHITE)
                        color(Color.WHITE)
                        drawableTop(R.drawable.baseline_keyboard_double_arrow_left_24)
                    }
                    // 向上
                    Text(
                        layoutParams = ViewLayoutParams(ItemSize.Wrap, ItemSize.Wrap)
                            .control().topToTopOf(osdBlock).startToStartOf(osdBlock)
                            .endToEndOf(osdBlock).complete()
                    ) {
                        fontSize(14.SP)
                        gravity(ViewGravity.Center)
                        text(R.string.label_action_up)
                        drawableTint(Color.WHITE)
                        color(Color.WHITE)
                        drawableStart(R.drawable.baseline_keyboard_double_arrow_up_24)
                    }
                    // 向下
                    Text(
                        layoutParams = ViewLayoutParams(ItemSize.Wrap, ItemSize.Wrap)
                            .control().bottomToBottomOf(osdBlock).startToStartOf(osdBlock)
                            .endToEndOf(osdBlock).complete()
                    ) {
                        fontSize(14.SP)
                        gravity(ViewGravity.Center)
                        text(R.string.label_action_down)
                        drawableTint(Color.WHITE)
                        color(Color.WHITE)
                        drawableStart(R.drawable.baseline_keyboard_double_arrow_down_24)
                    }
                    // 向右
                    Text(
                        layoutParams = ViewLayoutParams(ItemSize.Wrap, ItemSize.Wrap)
                            .control().bottomToBottomOf(osdBlock).endToEndOf(osdBlock)
                            .topToTopOf(osdBlock).complete()
                    ) {
                        fontSize(14.SP)
                        gravity(ViewGravity.Center)
                        text(R.string.label_action_right)
                        drawableTint(Color.WHITE)
                        color(Color.WHITE)
                        drawableTop(R.drawable.baseline_keyboard_double_arrow_right_24)
                    }
                    // 菜单
                    Text(
                        layoutParams = ViewLayoutParams(ItemSize.Wrap, ItemSize.Wrap)
                            .control().bottomToBottomOf(osdBlock).endToEndOf(osdBlock)
                            .topToTopOf(osdBlock)
                            .startToStartOf(osdBlock).complete()
                    ) {
                        fontSize(14.SP)
                        gravity(ViewGravity.Center)
                        text(R.string.label_action_menu)
                        drawableTint(Color.WHITE)
                        color(Color.WHITE)
                        drawableTop(R.drawable.baseline_menu_24)
                    }
                }
            }
            Button(
                label = staticData(activity.getString(R.string.label_continue)),
                onClick = {
                    onContinue()
                }
            )
            Button(
                label = staticData(activity.getString(R.string.label_edit)),
                onClick = {
                    onExit()
                }
            )
            Button(
                label = staticData(activity.getString(R.string.label_restart)),
                onClick = {
                    onNewGame()
                }
            )
            Footer()
        }
    }

    private fun onContinue() {
        continueState?.let {
            gameControllerCallback?.onContinue(it)
        }
    }

    override fun setState(state: PlayPageState) {
        if (state is PlayPageState.Menu) {
            continueState = state.continueState
            mazeState.value = state.treasure
        }
    }

    private fun onNewGame() {
        gameControllerCallback?.onNewGame()
    }

    private fun onExit() {
        gameControllerCallback?.onExit()
    }

    interface Callback {
        fun onContinue(state: PlayPageState)
        fun onNewGame()
        fun onExit()
    }

}