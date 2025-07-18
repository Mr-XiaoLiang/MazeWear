package com.lollipop.maze.generate

import com.lollipop.maze.MazeMap

interface MazeGenerator {

    fun generate(width: Int, height: Int): MazeMap

}