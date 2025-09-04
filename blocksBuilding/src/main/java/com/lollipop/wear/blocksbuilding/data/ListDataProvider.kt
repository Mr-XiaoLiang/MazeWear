package com.lollipop.wear.blocksbuilding.data

import com.lollipop.wear.blocksbuilding.BlockManager
import java.util.Collections

class ListDataProvider<T : Any> {

    private val dataList = mutableListOf<T>()

    private var blockManager: BlockManager? = null

    val list: List<T>
        get() {
            return dataList
        }

    val size: Int
        get() {
            return dataList.size
        }

    fun register(blockManager: BlockManager) {
        this.blockManager = blockManager
    }

    fun set(position: Int, value: T) {
        dataList[position] = value
        blockManager?.notifyUpdate(position)
    }

    fun add(value: T) {
        dataList.add(value)
        blockManager?.notifyInsert(dataList.size - 1)
    }

    fun remove(value: T) {
        val index = dataList.indexOf(value)
        if (index >= 0) {
            dataList.remove(value)
            blockManager?.notifyRemove(index)
        }
    }

    fun clear() {
        if (dataList.isNotEmpty()) {
            val size = dataList.size
            dataList.clear()
            blockManager?.notifyRemove(0, size)
        } else {
            blockManager?.notifyAllChanged()
        }
    }

    fun reset(list: List<T>) {
        dataList.clear()
        dataList.addAll(list)
        blockManager?.notifyAllChanged()
    }

    fun addAll(list: List<T>) {
        val index = dataList.size
        dataList.addAll(list)
        blockManager?.notifyInsert(index, list.size)
    }

    fun move(fromPosition: Int, toPosition: Int) {
        Collections.swap(dataList, fromPosition, toPosition)
        blockManager?.notifyMove(fromPosition, toPosition)
    }

}