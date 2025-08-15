package com.lollipop.wear.maze.play.layer

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.lollipop.wear.maze.databinding.LayerPlayLoadingBinding
import com.lollipop.wear.maze.play.state.PlayPageState

class LoadingLayer(activity: AppCompatActivity) : BasicLayer(activity) {

    private val binding by lazy {
        LayerPlayLoadingBinding.inflate(activity.layoutInflater)
    }

    override fun createView(
        container: ViewGroup
    ): View {
        return binding.root
    }

    override fun onShow() {
        super.onShow()
        binding.contentLoadingView.setIndeterminate(true)
    }

    override fun onHide() {
        super.onHide()
        binding.contentLoadingView.setIndeterminate(false)
    }

    override fun setState(state: PlayPageState) {
    }

}