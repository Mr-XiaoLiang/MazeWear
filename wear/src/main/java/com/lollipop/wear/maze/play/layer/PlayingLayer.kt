package com.lollipop.wear.maze.play.layer

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.lollipop.maze.data.MPoint
import com.lollipop.play.core.controller.LifecycleHelper
import com.lollipop.play.core.controller.MazeMoveAnimator
import com.lollipop.play.core.controller.TimeDelegate
import com.lollipop.play.core.helper.JoystickDelegate
import com.lollipop.play.core.helper.JoystickDirection
import com.lollipop.play.core.helper.dp2px
import com.lollipop.play.core.view.OsdPanelHelper
import com.lollipop.play.core.view.draw.color.ColorPathDrawable
import com.lollipop.play.core.view.draw.color.ColorSpiritDrawable
import com.lollipop.play.core.view.draw.color.ColorTileDrawable
import com.lollipop.play.core.view.joystick.JoystickRingRestrictedZone
import com.lollipop.play.core.view.joystick.RotateJoystickDisplay
import com.lollipop.wear.maze.databinding.LayerPlayGameBinding
import com.lollipop.wear.maze.play.state.PlayPageState
import com.lollipop.wear.maze.theme.MazeMapTheme

class PlayingLayer(activity: AppCompatActivity) : BasicLayer(activity) {

    private val binding by lazy {
        LayerPlayGameBinding.inflate(activity.layoutInflater)
    }
    private var osdPanelHelper: OsdPanelHelper? = null

    private var gameControllerCallback: Callback? = null

    private val joystickDelegate = JoystickDelegate(::onJoystickTouch)

    private val lifecycleHelper: LifecycleHelper by lazy {
        LifecycleHelper.auto(activity)
    }

    private val mazeMoveAnimator by lazy {
        MazeMoveAnimator(lifecycleHelper, ::updateMoveAnimation)
    }

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
            val osdHelper = OsdPanelHelper(menuPanel)
            osdPanelHelper = osdHelper
            osdButton.setOnClickListener {
                osdHelper.toggle()
            }
            menuPanel.setOnClickListener {
                osdHelper.hide()
            }
            TimeDelegate.auto(activity) {
                timeView.text = it
            }
            osdHelper.onShow {
                overviewView.updatePath()
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
                action.setSpiritDrawable(ColorSpiritDrawable().apply {
                    color = Color.WHITE
                    setEndColor(Color.GREEN)
                })
            }
            MazeMapTheme.updateMaze(overviewView)
        }
        osdPanelHelper?.init()
    }

    override fun setState(state: PlayPageState) {
        if (state is PlayPageState.Playing) {
            onPlay(state)
        } else if (state is PlayPageState.PointChange) {
            onMove(state)
        }
    }

    private fun onPlay(state: PlayPageState.Playing) {
        binding.apply {
            hideViews(
                menuPanel,
                osdActionPanel,
            )
            showViews(
                mazePlayView,
                mazeFogView,
                joystickRingView,
                joystickView,
                osdActionPanel,
            )
            val maze = state.maze
            val path = state.path
            val focus = state.focus
            mazePlayView.update { action ->
                action.setSource(maze.map, path)
                action.setFocus(focus.x, focus.y)
                action.setFrom(focus.x, focus.y)
                action.updateProgress(0F)
                action.setExtremePoint(null, maze.end)
            }
            overviewView.setMap(maze, path)
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
    }
}