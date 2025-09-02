package com.lollipop.wear.maze.play.layer

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.lollipop.play.core.controller.TimeDelegate
import com.lollipop.wear.maze.databinding.LayerPlayMenuBinding
import com.lollipop.wear.maze.play.PlayLayerState
import com.lollipop.wear.maze.play.state.PlayPageState
import com.lollipop.wear.maze.theme.MazeMapTheme

class MenuLayer(activity: AppCompatActivity) : BasicLayer(activity) {

    private val binding by lazy {
        LayerPlayMenuBinding.inflate(activity.layoutInflater)
    }

    private var continueState: PlayPageState? = null
    private var gameControllerCallback: Callback? = null

    override fun onAttach() {
        super.onAttach()
        if (activity is Callback) {
            gameControllerCallback = activity
        }
    }

    override fun createView(
        container: ViewGroup
    ): View {
        initView()
        return binding.root
    }

    private fun initView() {
        binding.apply {
            TimeDelegate.auto(activity) {
                timeView.text = it
            }
            continueButton.setOnClickListener {
                onContinue()
            }
            restartButton.setOnClickListener {
                gameControllerCallback?.onNewGame()
            }
            exitButton.setOnClickListener {
                gameControllerCallback?.onExit()
            }
            MazeMapTheme.updateMaze(overviewView)
        }
    }

    private fun onContinue() {
        continueState?.let {
            gameControllerCallback?.onContinue(it)
        }
    }

    override fun setState(state: PlayPageState) {
        if (state is PlayPageState.Menu) {
            continueState = state.continueState
            binding.apply {
                overviewView.setMap(state.maze, state.path)
            }
        }
    }

    interface Callback {
        fun onContinue(state: PlayPageState)
        fun onNewGame()
        fun onExit()
    }

}