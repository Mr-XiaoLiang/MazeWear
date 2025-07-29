package com.lollipop.maze.data

class MPath {

    var pointList = mutableListOf<MPoint>()

    fun add(point: MPoint) {
        pointList.add(point)
    }

    fun isEmpty(): Boolean{
        return pointList.isEmpty()
    }

    fun last(): MPoint? {
        return pointList.lastOrNull()
    }

    fun back() {
        pointList.removeAt(pointList.lastIndex)
    }

    fun clear() {
        pointList.clear()
    }

}