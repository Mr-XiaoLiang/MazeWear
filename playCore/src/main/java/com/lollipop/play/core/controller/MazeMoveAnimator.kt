package com.lollipop.play.core.controller

import android.animation.ValueAnimator
import com.lollipop.maze.data.MPoint

class MazeMoveAnimator(
    lifecycleHelper: LifecycleHelper,
    private val callback: (fromPoint: MPoint, toPoint: MPoint, progress: Float) -> Unit
) {

    private val lifecycleQueue = lifecycleHelper.newQueue(instantOnly = true)

    private val updateTask = Runnable { notifyUpdate() }

    private var fromPoint: MPoint? = null

    private var toPoint: MPoint? = null

    private var animationProgressValue = 0F

    private val valueAnimator = ValueAnimator().apply {
        addUpdateListener(AnimatorUpdateListener(::onAnimationUpdate))
    }

    fun onPointChange(fromPoint: MPoint, toPoint: MPoint) {
        var isChanged = false
        val oldFrom = this.fromPoint
        val oldTo = this.toPoint
        if (oldFrom == null || oldFrom.x != fromPoint.x || oldFrom.y != fromPoint.y) {
            isChanged = true
        }
        if (oldTo == null || oldTo.x != toPoint.x || oldTo.y != toPoint.y) {
            isChanged = true
        }
        this.fromPoint = fromPoint
        this.toPoint = toPoint
        if (fromPoint.x == toPoint.x && fromPoint.y == toPoint.y) {
            valueAnimator.cancel()
            animationProgressValue = 1F
            postUpdate()
        } else if (isChanged) {
            valueAnimator.cancel()
            valueAnimator.setDuration(1000L)
            valueAnimator.setFloatValues(0F, 1F)
            valueAnimator.start()
        }
    }

    private fun postUpdate() {
        lifecycleQueue.onUI(updateTask)
    }

    private fun notifyUpdate() {
        val f = fromPoint ?: return
        val t = toPoint ?: return
        callback.invoke(f, t, animationProgressValue)
    }

    private fun onAnimationUpdate(progress: Float) {
        this.animationProgressValue = progress
        postUpdate()
    }

    private class AnimatorUpdateListener(
        private val callback: (progress: Float) -> Unit
    ) : ValueAnimator.AnimatorUpdateListener {
        override fun onAnimationUpdate(animation: ValueAnimator) {
            val animatedValue = animation.animatedValue
            if (animatedValue is Float) {
                callback.invoke(animatedValue)
            }
        }
    }

}