package com.lollipop.wear.maze.play.layer

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.lollipop.wear.maze.R
import com.lollipop.wear.maze.databinding.LayerPlayVictoryBinding
import com.lollipop.wear.maze.play.state.PlayPageState
import com.lollipop.wear.maze.theme.MazeMapTheme

class VictoryLayer(activity: AppCompatActivity) : BasicLayer(activity) {

    private val binding by lazy {
        LayerPlayVictoryBinding.inflate(activity.layoutInflater)
    }

    private var callback: Callback? = null

    override fun createView(
        container: ViewGroup
    ): View {
        initView()
        return binding.root
    }

    private fun initView() {
        binding.apply {
            victoryBackButton.setOnClickListener {
                activity.onBackPressedDispatcher.onBackPressed()
            }
            victoryContinueButton.setOnClickListener {
                onContinueClick()
            }
            victoryPanel.setOnClickListener { }

            MazeMapTheme.updateMaze(settlementMapView)
        }
    }

    private fun onContinueClick() {
        callback?.onContinue()
    }

    override fun onAttach() {
        super.onAttach()
        if (activity is Callback) {
            callback = activity
        }
    }

    override fun setState(state: PlayPageState) {
        if (state is PlayPageState.Complete) {
            binding.apply {
                if (state.isNewPath) {
                    victoryHintView.setText(R.string.hint_victory_retry)
                    victoryContinueButton.setText(R.string.button_coverage)
                } else {
                    victoryHintView.setText(R.string.hint_victory)
                    victoryContinueButton.setText(R.string.button_continue)
                }
                settlementMapView.setMap(state.treasure)
                settlementMapView.updatePath()
            }
        }
    }

    interface Callback {
        fun onContinue()
    }

}