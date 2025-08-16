package com.lollipop.wear.maze.play.layer

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.lollipop.wear.maze.play.PlayLayer

abstract class BasicLayer(activity: AppCompatActivity) : PlayLayer(activity) {

    protected var contentView: View? = null

    private var animationView: View? = null

    private val showTask = Runnable {
        animationView?.let { view ->
            view.isVisible = true
            view.alpha = 0F
            view.animate().alpha(1F).setDuration(300).start()
        }
    }

    private val hideTask = Runnable {
        animationView?.let { view ->
            view.animate()
                .alpha(0F)
                .setDuration(300)
                .withEndAction {
                    view.isVisible = false
                }.start()
        }
    }

    protected fun hideViews(vararg views: View) {
        views.forEach {
            it.isInvisible = true
        }
    }

    protected fun showViews(vararg views: View) {
        views.forEach {
            it.isVisible = true
        }
    }

    override fun getView(
        container: ViewGroup
    ): View {
        val view = contentView
        if (view != null) {
            return view
        }
        val newView = createView(container)
        contentView = newView
        return newView
    }

    protected fun showByPost(view: View) {
        view.removeCallbacks(showTask)
        view.removeCallbacks(hideTask)
        if (view.isVisible) {
            return
        }
        animationView = view
        view.post(showTask)
    }

    protected fun hideByPost(view: View) {
        view.removeCallbacks(showTask)
        view.removeCallbacks(hideTask)
        if (!view.isVisible) {
            return
        }
        animationView = view
        view.post(hideTask)
    }

    protected abstract fun createView(container: ViewGroup): View

    override fun onShow() {
        super.onShow()
        contentView?.let {
            showByPost(it)
        }
    }

    override fun onHide() {
        super.onHide()
        contentView?.let {
            hideByPost(it)
        }
    }
}