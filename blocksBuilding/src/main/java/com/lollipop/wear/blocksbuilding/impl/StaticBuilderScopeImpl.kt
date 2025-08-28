package com.lollipop.wear.blocksbuilding.impl

import android.view.View
import android.view.ViewGroup
import com.lollipop.wear.blocksbuilding.BlockManager
import com.lollipop.wear.blocksbuilding.BlocksOwner
import com.lollipop.wear.blocksbuilding.BuilderScope
import com.lollipop.wear.blocksbuilding.IBlock
import com.lollipop.wear.blocksbuilding.dsl.bbLog

class StaticBuilderScopeImpl(override val blocksOwner: BlocksOwner) : BuilderScope {

    private var viewGroup: ViewGroup? = null
    private val managerList = mutableListOf<BasicManager>()
    private var currentManager: BasicManager? = null

    fun build(viewGroup: ViewGroup) {
        this.viewGroup = viewGroup
        rebuild()
    }

    private fun rebuild() {
        val viewGroup = viewGroup ?: return
        viewGroup.removeAllViews()
        var itemOffset = 0
        for (manager in managerList) {
            // 设置偏移量
            manager.itemOffset = itemOffset
            // 构建
            manager.build(viewGroup)
            // 累加偏移量
            itemOffset += manager.itemCount
        }
    }

    private fun optStaticManager(): StaticBlockManager {
        val manager = currentManager
        if (manager is StaticBlockManager) {
            return manager
        }
        val newManager = StaticBlockManager()
        managerList.add(newManager)
        currentManager = newManager
        bindCallback(newManager)
        return newManager
    }

    private fun <T> optMultiManager(
        items: List<T>,
        key: (T) -> Int,
        createItem: (Int) -> View,
        update: (View, T) -> Unit
    ): MutableBlockManager<T> {
        val newManager = MutableBlockManager(items, key, createItem, update)
        managerList.add(newManager)
        currentManager = null
        bindCallback(newManager)
        return newManager
    }

    private fun bindCallback(manager: BasicManager) {
        manager.insertCallback = ::onInsert
        manager.removeCallback = ::onRemove
        manager.updateCallback = ::onUpdate
        manager.moveCallback = ::onMove
        manager.allChangedCallback = ::onAllChanged
    }

    private fun onInsert(manager: BasicManager, position: Int, count: Int) {
        val group = viewGroup ?: return
        val offset = manager.itemOffset + position
        var managerPosition = position
        var newViewCount = 0
        for (i in 0 until count) {
            val view = manager.createView(managerPosition)
            if (view != null) {
                group.addView(view, offset + newViewCount)
                manager.updateView(managerPosition, view)
                managerPosition++
                newViewCount++
            }
        }
    }

    private fun onRemove(manager: BasicManager, position: Int, count: Int) {
        val group = viewGroup ?: return
        val offset = manager.itemOffset + position
        group.removeViews(offset, count)
    }

    private fun onUpdate(manager: BasicManager, position: Int, count: Int) {
        val group = viewGroup ?: return
        val offset = manager.itemOffset + position
        for (i in 0 until count) {
            val view = group.getChildAt(offset + i)
            manager.updateView(position + i, view)
        }
    }

    private fun onMove(manager: BasicManager, fromPosition: Int, toPosition: Int) {
        val group = viewGroup ?: return
        val from = manager.itemOffset + fromPosition
        val to = manager.itemOffset + toPosition
        val fromView = group.getChildAt(from)
        val toView = group.getChildAt(to)
        // 先移除后面的，再移除前面的
        if (from < to) {
            group.removeViewAt(to)
            group.removeViewAt(from)
            group.addView(toView, from)
            group.addView(fromView, to)
        } else {
            group.removeViewAt(from)
            group.removeViewAt(to)
            group.addView(fromView, to)
            group.addView(toView, from)
        }
    }

    private fun onAllChanged(manager: BasicManager) {
        rebuild()
    }

    override fun item(content: IBlock.() -> View) {
        val staticBlock = StaticBlock()
        val view = content(staticBlock)
        staticBlock.bind(view)
        optStaticManager().viewList.add(view)
    }

    override fun <T : Any> items(
        items: List<T>,
        key: (T) -> Int,
        createItem: (Int) -> View,
        update: (View, T) -> Unit
    ): BlockManager {
        return optMultiManager(items, key, createItem, update)
    }

    private abstract class BasicManager : BlockManager {

        var updateCallback: (manager: BasicManager, position: Int, count: Int) -> Unit =
            { _, _, _ -> }
        var insertCallback: (manager: BasicManager, position: Int, count: Int) -> Unit =
            { _, _, _ -> }
        var removeCallback: (manager: BasicManager, position: Int, count: Int) -> Unit =
            { _, _, _ -> }
        var moveCallback: (manager: BasicManager, position: Int, count: Int) -> Unit =
            { _, _, _ -> }
        var allChangedCallback: (manager: BasicManager) -> Unit = {}

        abstract fun build(viewGroup: ViewGroup)

        abstract val itemCount: Int

        var itemOffset: Int = 0

        override fun notifyUpdate(position: Int) {
            updateCallback(this, position, 1)
        }

        override fun notifyUpdate(position: Int, count: Int) {
            updateCallback(this, position, count)
        }

        override fun notifyInsert(position: Int) {
            insertCallback(this, position, 1)
        }

        override fun notifyInsert(position: Int, count: Int) {
            insertCallback(this, position, count)
        }

        override fun notifyRemove(position: Int) {
            removeCallback(this, position, 1)
        }

        override fun notifyRemove(position: Int, count: Int) {
            removeCallback(this, position, count)
        }

        override fun notifyMove(fromPosition: Int, toPosition: Int) {
            moveCallback(this, fromPosition, toPosition)
        }

        override fun notifyAllChanged() {
            allChangedCallback(this)
        }

        open fun createView(position: Int): View? {
            return null
        }

        open fun updateView(position: Int, view: View) {

        }

    }

    private class StaticBlockManager : BasicManager() {

        val viewList = ArrayList<View>()

        private val log = bbLog()

        override fun build(viewGroup: ViewGroup) {
            log("build: $itemCount")
            for (item in viewList) {
                viewGroup.addView(item)
            }
        }

        override val itemCount: Int = viewList.size
    }

    private class MutableBlockManager<T>(
        private val items: List<T>,
        private val typeProvider: (T) -> Int,
        private val createItem: (Int) -> View,
        private val update: (View, T) -> Unit
    ) : BasicManager() {

        private val log = bbLog()

        override val itemCount: Int
            get() {
                return items.size
            }

        override fun build(viewGroup: ViewGroup) {
            log("build: $itemCount")
            for (item in items) {
                val type = typeProvider(item)
                val itemView = createItem(type)
                update(itemView, item)
                viewGroup.addView(itemView)
            }
        }

        override fun createView(position: Int): View? {
            if (position >= items.size || position < 0) {
                return null
            }
            val item = items[position]
            val type = typeProvider(item)
            return createItem(type)
        }

        override fun updateView(position: Int, view: View) {
            val item = items[position]
            update(view, item)
        }

    }

}