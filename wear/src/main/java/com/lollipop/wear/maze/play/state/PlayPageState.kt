package com.lollipop.wear.maze.play.state

import com.lollipop.maze.MazeMap
import com.lollipop.maze.data.MBlock
import com.lollipop.maze.data.MPath
import com.lollipop.maze.data.MPoint
import com.lollipop.wear.maze.play.PlayLayer
import com.lollipop.wear.maze.play.PlayLayerState
import com.lollipop.wear.maze.play.layer.EmptyLayer
import com.lollipop.wear.maze.play.layer.ErrorLayer
import com.lollipop.wear.maze.play.layer.LoadingLayer
import com.lollipop.wear.maze.play.layer.PlayingLayer
import com.lollipop.wear.maze.play.layer.VictoryLayer

sealed class PlayPageState(
    val layerClass: Class<out PlayLayer>
) : PlayLayerState {

    fun setToLayer(layer: PlayLayer) {
        layer.setState(this)
    }

    object Init : PlayPageState(EmptyLayer::class.java)

    object Loading : PlayPageState(LoadingLayer::class.java)

    class Playing(
        val maze: MazeMap,
        val path: MPath,
        val focus: MBlock
    ) : PlayPageState(PlayingLayer::class.java)

    class PointChange(
        val fromPoint: MPoint,
        val toPoint: MPoint
    ) : PlayPageState(PlayingLayer::class.java)

    class Complete(
        val isNewPath: Boolean,
        val maze: MazeMap,
        val path: MPath,
    ) : PlayPageState(VictoryLayer::class.java)

    class Error(val message: Int) : PlayPageState(ErrorLayer::class.java)

}