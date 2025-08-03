package com.lollipop.play.core.controller

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import java.lang.ref.WeakReference

abstract class BasicLifecycleDelegate {

    var isActive = false
        private set

    private var lifecycleController: LifecycleController? = null

    fun controllerByAuto(source: LifecycleOwner): Auto {
        val oldController = lifecycleController
        if (oldController != null) {
            if (oldController is Auto) {
                if (oldController.owner.get() === source) {
                    return oldController
                }
                oldController.delegate = null
            } else if (oldController is Manual) {
                oldController.delegate = null
            }
        }
        val newController = Auto(source, this)
        lifecycleController = newController
        return newController
    }

    fun controllerByManual(): Manual {
        val oldController = lifecycleController
        if (oldController != null) {
            if (oldController is Manual) {
                return oldController
            } else if (oldController is Auto) {
                oldController.delegate = null
            }
        }
        val newController = Manual(this)
        lifecycleController = newController
        return newController
    }

    protected fun onResume() {
        updateState(true)
        onResumeInvoke()
    }

    protected fun onPause() {
        updateState(false)
        onPauseInvoke()
    }

    protected abstract fun onResumeInvoke()

    protected abstract fun onPauseInvoke()

    private fun updateState(isActive: Boolean) {
        this.isActive = isActive
        onStateChanged(isActive)
    }

    protected open fun onStateChanged(isActive: Boolean) {

    }

    interface LifecycleController

    class Auto(
        source: LifecycleOwner,
        internal var delegate: BasicLifecycleDelegate?
    ) : LifecycleController {

        val owner = WeakReference(source)

        private val lifecycleEventObserver = LifecycleEventObserver { source, event ->
            if (delegate == null) {
                removeObserver(source)
            }
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    delegate?.onResume()
                }

                Lifecycle.Event.ON_PAUSE -> {
                    delegate?.onPause()
                }

                else -> {}
            }
        }

        init {
            bind(source)
        }

        private fun bind(source: LifecycleOwner) {
            if (delegate == null) {
                source.lifecycle.removeObserver(lifecycleEventObserver)
                owner.clear()
                return
            }
            source.lifecycle.addObserver(lifecycleEventObserver)
            if (source.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                delegate?.onResume()
            }
        }

        private fun removeObserver(source: LifecycleOwner) {
            owner.clear()
            source.lifecycle.removeObserver(lifecycleEventObserver)
        }

    }

    class Manual(
        internal var delegate: BasicLifecycleDelegate?
    ) : LifecycleController {

        fun resume() {
            delegate?.onResume()
        }

        fun pause() {
            delegate?.onPause()
        }

    }

}