package com.lollipop.maze.data

import com.lollipop.maze.MazeMap

class MTreasure(
    val mazeMap: MazeMap,
    val path: MPath,
    val hiPath: MPath? = null
) {

    companion object {
        val EMPTY = MTreasure(
            mazeMap = MazeMap(MPoint(0, 0), MPoint(0, 0), MMap(0, 0)),
            path = MPath(),
            hiPath = null
        )
    }

}
