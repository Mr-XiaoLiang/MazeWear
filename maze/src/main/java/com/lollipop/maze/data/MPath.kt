package com.lollipop.maze.data

class MPath {

    var pointList = mutableListOf<MPoint>()

    val size: Int
        get() {
            return pointList.size
        }

    fun subPath(length: Int): MPath {
        return MPath().also {
            it.pointList.clear()
            it.pointList.addAll(pointList.subList(0, length))
        }
    }

    fun put(point: MPoint) {
        pointList.add(point)
    }

    fun isEmpty(): Boolean {
        return pointList.isEmpty()
    }

    fun last(): MPoint? {
        return pointList.lastOrNull()
    }

    fun secondLast(): MPoint? {
        if (size > 1) {
            return pointList.getOrNull(pointList.lastIndex - 1)
        }
        return null
    }

    fun back() {
        if (pointList.isEmpty()) {
            return
        }
        pointList.removeAt(pointList.lastIndex)
    }

    fun clear() {
        pointList.clear()
    }

}