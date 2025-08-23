package com.lollipop.wear.blocksbuilding

interface BlockManager {

    fun notifyUpdate(position: Int)

    fun notifyUpdate(position: Int, count: Int)

    fun notifyInsert(position: Int)

    fun notifyInsert(position: Int, count: Int)

    fun notifyRemove(position: Int)

    fun notifyRemove(position: Int, count: Int)

    fun notifyMove(fromPosition: Int, toPosition: Int)

    fun notifyAllChanged()

}