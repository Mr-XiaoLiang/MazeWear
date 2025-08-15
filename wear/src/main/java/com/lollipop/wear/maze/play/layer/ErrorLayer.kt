package com.lollipop.wear.maze.play.layer

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.lollipop.wear.maze.databinding.LayerPlayErrorBinding
import com.lollipop.wear.maze.play.state.PlayPageState

class ErrorLayer(activity: AppCompatActivity) : BasicLayer(activity) {

    private val binding by lazy {
        LayerPlayErrorBinding.inflate(activity.layoutInflater)
    }

    override fun createView(
        container: ViewGroup
    ): View {
        initView()
        return binding.root
    }

    private fun initView() {
        binding.errorBackButton.setOnClickListener {
            activity.onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun setState(state: PlayPageState) {
        if (state is PlayPageState.Error) {
            binding.errorMessageView.setText(state.message)
        }
    }

}