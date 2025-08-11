package com.lollipop.play.core.data

import com.lollipop.play.core.controller.BasicLifecycleDelegate
import java.util.LinkedList

class DataObserver(
    private val onChanged: () -> Unit
) : BasicLifecycleDelegate() {

    val pendingChangedInfo = LinkedList<Pending>()

    private val changeListener = object : DataManager.DataChangeListener {
        override fun onDataChanged(mazeId: Int) {
            newInfo(Pending.Changed(mazeId))
        }

        override fun onDataLoaded() {
            newInfo(Pending.Loaded)
        }

    }

    init {
        DataManager.register(changeListener)
        // 初始化的时候，尝试通知一下
        changeListener.onDataLoaded()
    }

    private fun newInfo(pending: Pending) {
        pendingChangedInfo.addLast(pending)
        if (isActive) {
            onChanged()
        }
    }

    override fun onResumeInvoke() {
        if (pendingChangedInfo.isNotEmpty()) {
            onChanged()
        }
    }

    override fun onPauseInvoke() {
    }

    fun releasePending() {
        pendingChangedInfo.clear()
    }

    fun destroy() {
        DataManager.unregister(changeListener)
    }

    sealed class Pending {

        class Changed(val mazeId: Int) : Pending()

        object Loaded : Pending()

    }

}