package com.lollipop.wear.maze.blocks

import android.view.LayoutInflater
import android.view.View
import com.lollipop.maze.MazeMap
import com.lollipop.maze.data.MMap
import com.lollipop.maze.data.MPath
import com.lollipop.maze.data.MPoint
import com.lollipop.wear.blocksbuilding.BlocksOwner
import com.lollipop.wear.blocksbuilding.data.mutableData
import com.lollipop.wear.blocksbuilding.dsl.layoutParams
import com.lollipop.wear.blocksbuilding.item.ItemSize
import com.lollipop.wear.maze.databinding.ItemBlocksMazeOverviewBinding
import com.lollipop.wear.maze.theme.MazeMapTheme


class MazeOverviewBlock(
    private val blocksOwner: BlocksOwner,
    private val state: MazeOverviewBlockState
) {

    private val binding = ItemBlocksMazeOverviewBinding.inflate(
        LayoutInflater.from(blocksOwner.context)
    )

    val view: View = binding.root

    init {
        view.layoutParams(ItemSize.Match, ItemSize.Wrap)
        state.value.register { d ->
            binding.overviewView.setMap(d.value.map, d.value.path)
        }
        binding.overviewView.setMap(state.map, state.path)
        MazeMapTheme.updateMaze(binding.overviewView)
    }

}

class MazeOverviewBlockState() {

    companion object {
        val EMPTY = MazeOverviewData(
            MazeMap(MPoint(0, 0), MPoint(0, 0), MMap(0, 0)),
            MPath()
        )

    }

    val value = mutableData(EMPTY)

    val map: MazeMap
        get() {
            return value.value.map
        }

    val path: MPath
        get() {
            return value.value.path
        }

    fun update(map: MazeMap, path: MPath) {
        value.value = MazeOverviewData(map, path)
    }

}

class MazeOverviewData(
    val map: MazeMap,
    val path: MPath
)

fun BlocksOwner.mazeOverview(state: MazeOverviewBlockState): View {
    return MazeOverviewBlock(this, state).view
}
