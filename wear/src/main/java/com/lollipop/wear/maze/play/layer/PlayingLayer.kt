package com.lollipop.wear.maze.play.layer

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.lollipop.maze.data.MPoint
import com.lollipop.play.core.controller.LifecycleHelper
import com.lollipop.play.core.controller.MazeMoveAnimator
import com.lollipop.play.core.helper.JoystickDelegate
import com.lollipop.play.core.helper.JoystickDirection
import com.lollipop.play.core.view.draw.color.ColorPathDrawable
import com.lollipop.play.core.view.draw.color.ColorSpiritDrawable
import com.lollipop.play.core.view.draw.color.ColorTileDrawable
import com.lollipop.play.core.view.joystick.JoystickRingRestrictedZone
import com.lollipop.play.core.view.joystick.RotateJoystickDisplay
import com.lollipop.wear.maze.databinding.LayerPlayGameBinding
import com.lollipop.wear.maze.play.state.PlayPageState

class PlayingLayer(activity: AppCompatActivity) : BasicLayer(activity) {

    private val binding by lazy {
        LayerPlayGameBinding.inflate(activity.layoutInflater)
    }
    private var gameControllerCallback: Callback? = null

    private val joystickDelegate = JoystickDelegate(::onJoystickTouch)

    private val lifecycleHelper: LifecycleHelper by lazy {
        LifecycleHelper.auto(activity)
    }

    private val mazeMoveAnimator by lazy {
        MazeMoveAnimator(lifecycleHelper, ::updateMoveAnimation)
    }

    private var playState: PlayPageState.Playing? = null

    override fun createView(
        container: ViewGroup
    ): View {
        initView()
        return binding.root
    }

    override fun onAttach() {
        super.onAttach()
        if (activity is Callback) {
            gameControllerCallback = activity
        }
    }

    private fun onJoystickTouch(direction: JoystickDirection) {
        gameControllerCallback?.onJoystickTouch(direction)
    }

    private fun initView() {
        binding.apply {
            osdButton.setOnClickListener {
                openMenu()
            }
            joystickView.addRestrictedZone(
                JoystickRingRestrictedZone(
                    1F,
                    0.5F,
                    insideMode = true
                )
            )
            joystickView.setJoystickDisplay(
                RotateJoystickDisplay.create(joystickRingView)
            )
            joystickDelegate.bind(joystickView)
            mazePlayView.update { action ->
                action.setTileDrawable(ColorTileDrawable().apply { color = Color.GRAY })
                action.setPathDrawable(ColorPathDrawable().apply { color = 0x33FFFFFF })
                action.setGhostDrawable(ColorPathDrawable().apply { color = 0x3356FF56 })
                action.setSpiritDrawable(ColorSpiritDrawable().apply {
                    color = Color.WHITE
                    setEndColor(Color.GREEN)
                })
            }
        }
    }

    private fun openMenu() {
        playState?.let {
            gameControllerCallback?.openMenu(it)
        }
    }

    override fun setState(state: PlayPageState) {
        if (state is PlayPageState.Playing) {
            onPlay(state)
        } else if (state is PlayPageState.PointChange) {
            onMove(state)
        }
    }

    private fun onPlay(state: PlayPageState.Playing) {
        playState = state
        binding.apply {
            showViews(
                mazePlayView,
                mazeFogView,
                joystickRingView,
                joystickView,
                osdActionPanel,
            )
            val treasure = state.treasure
            val maze = treasure.mazeMap
            val focus = state.focus
            mazePlayView.update { action ->
                action.setSource(treasure)
                action.setFocus(focus.x, focus.y)
                action.setFrom(focus.x, focus.y)
                action.updateProgress(0F)
                action.setExtremePoint(null, maze.end)
            }
        }
    }

    private fun onMove(state: PlayPageState.PointChange) {
        mazeMoveAnimator.onPointChange(state.fromPoint, state.toPoint)
    }

    private fun updateMoveAnimation(
        fromPoint: MPoint,
        toPoint: MPoint,
        progress: Float
    ) {
        binding.mazePlayView.update { action ->
            action.setFocus(toPoint.x, toPoint.y)
            action.setFrom(fromPoint.x, fromPoint.y)
            action.updateProgress(progress)
        }
    }

    interface Callback {
        fun onJoystickTouch(direction: JoystickDirection)
        fun openMenu(state: PlayPageState.Playing)
    }
}