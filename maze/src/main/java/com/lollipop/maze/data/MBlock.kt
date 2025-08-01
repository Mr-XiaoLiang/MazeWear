package com.lollipop.maze.data

class MBlock(
    override var x: Int = 0,
    override var y: Int = 0,
) : APoint() {

    constructor(block: MPoint) : this(block.x, block.y)

    private var snapshotPoint: MPoint? = null

    fun set(x: Int, y: Int) {
        this.x = x
        this.y = y
    }

    fun set(point: APoint) {
        set(point.x, point.y)
    }

    /**
     * 快照
     */
    fun snapshot(): MPoint {
        // 获取记录
        val point = snapshotPoint
        // 如果记录为空，或者记录与当前的点不一样的话，则创建新的点
        if (point == null || !point.isSame(point = this)) {
            val newPoint = MPoint(x, y)
            snapshotPoint = newPoint
            return newPoint
        }
        // 否则返回记录
        return point
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

    fun leftPoint(step: Int = 1): MPoint {
        return MPoint(x - step, y)
    }

    fun right(step: Int = 1): MBlock {
        return MBlock(x + step, y)
    }

    fun rightPoint(step: Int = 1): MPoint {
        return MPoint(x + step, y)
    }

    fun up(step: Int = 1): MBlock {
        return MBlock(x, y - step)
    }

    fun upPoint(step: Int = 1): MPoint {
        return MPoint(x, y - step)
    }

    fun down(step: Int = 1): MBlock {
        return MBlock(x, y + step)
    }

    fun downPoint(step: Int = 1): MPoint {
        return MPoint(x, y + step)
    }

}