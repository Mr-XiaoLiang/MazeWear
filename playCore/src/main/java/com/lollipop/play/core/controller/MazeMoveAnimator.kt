package com.lollipop.play.core.controller

import android.animation.ValueAnimator
import com.lollipop.maze.data.MPoint

class MazeMoveAnimator(
    lifecycleHelper: LifecycleHelper,
    private val callback: (fromPoint: MPoint, toPoint: MPoint, progress: Float) -> Unit
) {

    private val lifecycleQueue = lifecycleHelper.newQueue(instantOnly = true)

    private val updateTask = Runnable { notifyUpdate() }

    private val valueAnimator = ValueAnimator()

    init {
        // TODO
    }

    fun onPointChange(fromPoint: MPoint, toPoint: MPoint) {
        // TODO
    }

    private fun postUpdate() {
        lifecycleQueue.onUI(updateTask)
    }

    private fun notifyUpdate() {
        // TODO
    }

}