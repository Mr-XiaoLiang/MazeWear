package com.lollipop.maze.data

class MPath {

    var pointList = mutableListOf<MPoint>()

    fun add(point: MPoint) {
        pointList.add(point)
    }

    fun back() {
        pointList.removeAt(pointList.lastIndex)
    }

}