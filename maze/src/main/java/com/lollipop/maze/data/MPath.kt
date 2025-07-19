package com.lollipop.maze.data

import android.graphics.Point

class MPath {

    var pointList = mutableListOf<Point>()

    fun add(point: Point) {
        pointList.add(point)
    }

    fun back() {
        pointList.removeAt(pointList.lastIndex)
    }

}