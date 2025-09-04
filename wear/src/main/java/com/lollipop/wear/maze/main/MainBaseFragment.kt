package com.lollipop.wear.maze.main

import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import com.lollipop.play.core.data.MazeHistory
import com.lollipop.play.core.data.PreferencesHelper
import com.lollipop.play.core.helper.registerLog
import com.lollipop.wear.blocksbuilding.BuilderScope
import com.lollipop.wear.blocksbuilding.RecyclerHolder
import com.lollipop.wear.blocksbuilding.data.DataProvider
import com.lollipop.wear.blocksbuilding.data.mutableData
import com.lollipop.wear.blocksbuilding.dsl.ViewLayoutParams
import com.lollipop.wear.blocksbuilding.dsl.layoutParams
import com.lollipop.wear.blocksbuilding.item.DP
import com.lollipop.wear.blocksbuilding.item.ItemSize
import com.lollipop.wear.blocksbuilding.item.SP
import com.lollipop.wear.blocksbuilding.item.ViewGravity
import com.lollipop.wear.blocksbuilding.view.Constraint
import com.lollipop.wear.blocksbuilding.view.Image
import com.lollipop.wear.blocksbuilding.view.ItemGroupScope
import com.lollipop.wear.blocksbuilding.view.ItemViewScope
import com.lollipop.wear.blocksbuilding.view.Row
import com.lollipop.wear.blocksbuilding.view.Text
import com.lollipop.wear.blocksbuilding.view.ViewHolder
import com.lollipop.wear.maze.R
import com.lollipop.wear.maze.blocks.MazeOverview
import com.lollipop.wear.maze.blocks.MazeOverviewData

abstract class MainBaseFragment : Fragment() {

    protected val log = registerLog()

    protected var settings: PreferencesHelper? = null

    protected fun optSettings(context: Context): PreferencesHelper {
        val helper = settings
        if (helper != null) {
            return helper
        }
        val newHelper = PreferencesHelper.create(context)
        settings = newHelper
        return newHelper
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        optSettings(context)
    }

    override fun onResume() {
        super.onResume()
        log("onResume")
    }

    override fun onPause() {
        super.onPause()
        log("onPause")
    }

    protected fun BuilderScope.MazeHolder(
        onItemLongClick: (Int, MazeHistory) -> Boolean = { _, _ -> false },
        onItemClick: (Int, MazeHistory) -> Unit
    ): RecyclerHolder<MazeHistory> {
        return ViewHolder {
            val state = itemState
            val titleState = mutableData("")
            val timeState = mutableData("")
            val sizeState = mutableData("")
            val stepState = mutableData("")
            state.remember {
                titleState.value = it?.name ?: ""
                timeState.value = it?.timeDisplay ?: ""
                sizeState.value = it?.level ?: ""
                stepState.value = it?.pathLength?.toString() ?: ""
            }
            padding(horizontal = 12.DP, vertical = 6.DP)
            content.layoutParams(width = ItemSize.Match, height = ItemSize.Wrap)
            onClick {
                state.value?.let {
                    onItemClick(position, it)
                }
            }
            onLongClick {
                val result = state.value?.let {
                    onItemLongClick(position, it)
                }
                result ?: false
            }
            Constraint(
                layoutParams = ViewLayoutParams(ItemSize.Match, ItemSize.Wrap)
            ) {
                background(R.drawable.bg_item_main_maze)
                padding(6.DP)
                val overview = MazeOverview(
                    layoutParams = ViewLayoutParams(48.DP)
                        .control().endToParent().topToParent().complete()
                ) {
                    background(R.drawable.bg_item_maze_overview)
                    state.remember {
                        if (it == null) {
                            setMap(MazeOverviewData.EMPTY)
                        } else {
                            setMap(it.maze, it.path)
                        }
                    }
                }
                val title = Text(
                    layoutParams = ViewLayoutParams(width = ItemSize.Empty, height = ItemSize.Wrap)
                        .margin(end = 6.DP)
                        .control().startToParent().topToParent().endToStartOf(overview).complete()
                ) {
                    fontSize(16.SP)
                    color(Color.WHITE)
                    fontByResource(com.lollipop.play.core.R.font.leckerli_one_regular)
                    titleState.remember {
                        text = it
                    }
                }
                val sizeItem = Item(
                    layoutParams = ViewLayoutParams(width = ItemSize.Empty, height = ItemSize.Wrap)
                        .control().topToBottomOf(title).startToParent().endToStartOf(overview)
                        .complete().margin(top = 4.DP, end = 6.DP),
                    icon = R.drawable.baseline_resize_24,
                    label = sizeState
                )

                val stepItem = Item(
                    layoutParams = ViewLayoutParams(width = ItemSize.Empty, height = ItemSize.Wrap)
                        .control().topToBottomOf(sizeItem).startToStartOf(sizeItem)
                        .endToEndOf(sizeItem)
                        .complete().margin(top = 4.DP),
                    icon = R.drawable.baseline_footprint_24,
                    label = stepState
                )
                val timeItem = Item(
                    layoutParams = ViewLayoutParams(width = ItemSize.Empty, height = ItemSize.Wrap)
                        .control().topToBottomOf(stepItem).startToStartOf(stepItem).endToParent()
                        .complete().margin(top = 4.DP),
                    icon = R.drawable.baseline_nest_clock_farsight_analog_24,
                    label = timeState
                )
            }
        }
    }

    private fun ItemGroupScope<*>.Item(
        layoutParams: ViewGroup.LayoutParams,
        @DrawableRes icon: Int,
        label: DataProvider<String>
    ): ItemViewScope<*> {
        return Row(layoutParams = layoutParams) {
            Image(
                layoutParams = ViewLayoutParams(16.DP).gravity(ViewGravity.CenterVertical)
            ) {
                src(icon)
            }
            Text(
                layoutParams = ViewLayoutParams(
                    width = ItemSize.Match,
                    height = ItemSize.Wrap
                ).margin(start = 4.DP).gravity(ViewGravity.CenterVertical)
            ) {
                fontSize(11.SP)
                color(Color.WHITE)
                fontByResource(com.lollipop.play.core.R.font.leckerli_one_regular)
                label.remember {
                    text = it
                }
            }
        }
    }

}