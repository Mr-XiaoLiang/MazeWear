package com.lollipop.maze.data

class MBlock(
    var x: Int = 0,
    var y: Int = 0,
) {

    constructor(block: MBlock) : this(block.x, block.y)

    fun set(x: Int, y: Int) {
        this.x = x
        this.y = y
    }

    fun set(block: MBlock) {
        set(block.x, block.y)
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