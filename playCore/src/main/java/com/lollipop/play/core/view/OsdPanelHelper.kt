package com.lollipop.play.core.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import kotlin.math.abs

class OsdPanelHelper(
    val view: View
) {

    companion object {
        private const val ANIMATION_DURATION = 300L
        private const val ANIMATION_MIN = 0F
        private const val ANIMATION_MAX = 1F
        private const val ANIMATION_MAX_LIMIT = 0.999F
        private const val ANIMATION_MIN_LIMIT = 0.001F

        private const val ANIMATION_LENGTH = ANIMATION_MAX - ANIMATION_MIN
    }

    private var animationProgress = 0F

    private var animationToShow = false

    private val animationListener = AnimatorDelegate(
        onStart = ::onAnimationStart,
        onEnd = ::onAnimationEnd,
        onUpdate = ::onAnimationUpdate
    )

    private val animator by lazy {
        ValueAnimator().apply {
            interpolator = LinearInterpolator()
            addListener(animationListener)
            addUpdateListener(animationListener)
        }
    }

    fun init() {
        view.visibility = View.INVISIBLE
        animationToShow = false
        onAnimationUpdate(ANIMATION_MIN)
        onAnimationEnd()
    }

    fun show() {
        animationToShow = true
        animationTo(ANIMATION_MAX)
    }

    fun hide() {
        animationToShow = false
        animationTo(ANIMATION_MIN)
    }

    fun toggle() {
        if (animationToShow) {
            hide()
        } else {
            show()
        }
    }

    private fun animationTo(target: Float) {
        val duration = abs(target - animationProgress) / ANIMATION_LENGTH * ANIMATION_DURATION
        animator.cancel()
        animator.setFloatValues(animationProgress, target)
        animator.duration = duration.toLong()
        animator.start()
    }

    private fun onAnimationEnd() {
        if (animationProgress < ANIMATION_MAX_LIMIT && !animationToShow) {
            view.isInvisible = true
        }
    }

    private fun onAnimationStart() {
        view.isVisible = true
    }

    private fun onAnimationUpdate(progress: Float) {
        animationProgress = progress
        view.alpha = progress
    }

    private class AnimatorDelegate(
        val onStart: () -> Unit,
        val onEnd: () -> Unit,
        val onUpdate: (Float) -> Unit
    ) : Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {
        override fun onAnimationCancel(animation: Animator) {
        }

        override fun onAnimationEnd(animation: Animator) {
            onEnd()
        }

        override fun onAnimationRepeat(animation: Animator) {
        }

        override fun onAnimationStart(animation: Animator) {
            onStart()
        }

        override fun onAnimationUpdate(animation: ValueAnimator) {
            val animatedValue = animation.animatedValue
            if (animatedValue is Float) {
                onUpdate(animatedValue)
            }
        }
    }

}