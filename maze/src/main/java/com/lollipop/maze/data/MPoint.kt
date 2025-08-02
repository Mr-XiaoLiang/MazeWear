package com.lollipop.maze.data

abstract class APoint {
    abstract val x: Int
    abstract val y: Int

    fun isSame(point: APoint): Boolean {
        return x == point.x && y == point.y
    }

}

class MPoint(override val x: Int, override val y: Int) : APoint() {

    private val contextValue: String by lazy {
        "MPoint($x, $y)"
    }

    override fun toString(): String {
        return contextValue
    }
}