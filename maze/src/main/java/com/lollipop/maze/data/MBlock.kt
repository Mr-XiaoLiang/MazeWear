package com.lollipop.maze.data

class MBlock(
    override var x: Int = 0,
    override var y: Int = 0,
) : MPoint(x, y) {

    constructor(block: MPoint) : this(block.x, block.y)

    fun set(x: Int, y: Int) {
        this.x = x
        this.y = y
    }

    fun set(block: MBlock) {
        set(block.x, block.y)
    }

    fun set(point: MPoint) {
        set(point.x, point.y)
    }

    fun toPoint(): MPoint {
        return this
    }

    fun leftWith(target: MBlock, step: Int = 1) {
        offsetOf(target = target, offsetX = -step, offsetY = 0)
    }

    fun rightWith(target: MBlock, step: Int = 1) {
        offsetOf(target = target, offsetX = step, offsetY = 0)
    }

    fun upWith(target: MBlock, step: Int = 1) {
        offsetOf(target = target, offsetX = 0, offsetY = -step)
    }

    fun downWith(target: MBlock, step: Int = 1) {
        offsetOf(target = target, offsetX = 0, offsetY = step)
    }

    fun offsetOf(target: MBlock, offsetX: Int = 0, offsetY: Int = 0) {
        this.x = target.x + offsetX
        this.y = target.y + offsetY
    }

    fun left(step: Int = 1): MBlock {
        return MBlock(x - step, y)
    }

    fun right(step: Int = 1): MBlock {
        return MBlock(x + step, y)
    }

    fun up(step: Int = 1): MBlock {
        return MBlock(x, y - step)
    }

    fun down(step: Int = 1): MBlock {
        return MBlock(x, y + step)
    }

}