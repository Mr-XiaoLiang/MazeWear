package com.lollipop.wear.maze.presentation

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lollipop.maze.MazeMap
import com.lollipop.maze.MazeTest
import com.lollipop.maze.data.MBlock
import com.lollipop.maze.data.MPath
import com.lollipop.maze.data.MPoint
import com.lollipop.play.core.controller.LifecycleHelper
import com.lollipop.play.core.controller.MazeController
import com.lollipop.play.core.controller.MazeMoveAnimator
import com.lollipop.play.core.controller.TimeDelegate
import com.lollipop.play.core.helper.JoystickDelegate
import com.lollipop.play.core.helper.JoystickDirection
import com.lollipop.play.core.helper.registerLog
import com.lollipop.play.core.view.OsdPanelHelper
import com.lollipop.play.core.view.draw.color.ColorPathDrawable
import com.lollipop.play.core.view.draw.color.ColorSpiritDrawable
import com.lollipop.play.core.view.draw.color.ColorTileDrawable
import com.lollipop.play.core.view.joystick.JoystickRingRestrictedZone
import com.lollipop.play.core.view.joystick.RotateJoystickDisplay
import com.lollipop.wear.maze.databinding.ActivityPlayBinding

class PlayActivity : AppCompatActivity(), MazeController.Callback {

    companion object {

        private const val KEY_MAZE_WIDTH = "KEY_MAZE_WIDTH"
        private const val KEY_MAZE_ID = "KEY_MAZE_ID"

        fun newMaze(context: Context, width: Int) {
            val intent = Intent(context, PlayActivity::class.java)
            intent.putExtra(KEY_MAZE_WIDTH, width)
            context.startActivity(intent)
        }

        fun resumeMaze(context: Context, mazeId: Int) {
            val intent = Intent(context, PlayActivity::class.java)
            intent.putExtra(KEY_MAZE_ID, mazeId)
            context.startActivity(intent)
        }

        private fun getMazeWidth(intent: Intent): Int {
            return intent.getIntExtra(KEY_MAZE_WIDTH, 10)
        }

        private fun getMazeId(intent: Intent): Int {
            return intent.getIntExtra(KEY_MAZE_ID, -1)
        }

    }

    private val log = registerLog()

    private val binding by lazy {
        ActivityPlayBinding.inflate(layoutInflater)
    }

    private val osdPanelHelper by lazy {
        OsdPanelHelper(binding.menuPanel)
    }

    private val lifecycleHelper by lazy {
        LifecycleHelper.auto(this)
    }

    private val mazeController by lazy {
        MazeController(lifecycleHelper, this)
    }

    private val joystickDelegate = JoystickDelegate(::onJoystickTouch)

    private val mazeMoveAnimator by lazy {
        MazeMoveAnimator(lifecycleHelper, ::updateMoveAnimation)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
        loadData()
    }

    override fun onResume() {
        super.onResume()
        mazeController.onResume()
    }

    private fun loadData() {
        val mazeId = getMazeId(intent)
        if (mazeId >= 0) {
            mazeController.load(mazeId)
        } else {
            mazeController.create(getMazeWidth(intent))
        }
    }

    private fun initView() {
        binding.joystickView.addRestrictedZone(
            JoystickRingRestrictedZone(
                1F,
                0.5F,
                insideMode = true
            )
        )
        binding.joystickView.setJoystickDisplay(
            RotateJoystickDisplay.create(binding.joystickRingView)
        )
        joystickDelegate.bind(binding.joystickView)
        binding.mazePlayView.update { action ->
            action.setTileDrawable(ColorTileDrawable().apply { color = Color.GRAY })
            action.setPathDrawable(ColorPathDrawable().apply { color = 0x33FFFFFF })
            action.setSpiritDrawable(ColorSpiritDrawable().apply {
                color = Color.WHITE
                setEndColor(Color.GREEN)
            })
        }
        binding.osdButton.setOnClickListener {
            osdPanelHelper.toggle()
        }
        binding.menuPanel.setOnClickListener {
            osdPanelHelper.hide()
        }
        TimeDelegate.auto(this) {
            binding.timeView.text = it
        }
        osdPanelHelper.init()
    }

    override fun onLoadingStart() {
        lifecycleHelper.post {
            binding.contentLoadingView.show()
        }
    }

    override fun onLoadingEnd() {
        lifecycleHelper.post {
            binding.contentLoadingView.hide()
        }
    }

    override fun onMazeResult(maze: MazeMap, path: MPath, focus: MBlock) {
        lifecycleHelper.post {
            onNewMaze(maze, path, focus)
        }
    }

    override fun onPointChange(
        fromPoint: MPoint,
        toPoint: MPoint
    ) {
        log("onPointChange: [${fromPoint.x}, ${fromPoint.y}] ==> [${toPoint.x}, ${toPoint.y}] ")
        lifecycleHelper.post {
            onMove(fromPoint, toPoint)
        }
    }

    private fun onJoystickTouch(direction: JoystickDirection) {
        log("direction = $direction")
        mazeController.manipulate(direction)
    }

    private fun onNewMaze(maze: MazeMap, path: MPath, focus: MBlock) {
        binding.contentLoadingView.hide()
        binding.mazePlayView.update { action ->
            action.setSource(maze.map, path)
            action.setFocus(focus.x, focus.y)
            action.setFrom(focus.x, focus.y)
            action.updateProgress(0F)
            action.setExtremePoint(null, maze.end)
        }
        log("start = [${maze.start.x}, ${maze.start.y}] ")
        log("map \n" + MazeTest.print(maze).build())
    }

    private fun onMove(
        fromPoint: MPoint,
        toPoint: MPoint
    ) {
        mazeMoveAnimator.onPointChange(fromPoint, toPoint)
    }

    private fun updateMoveAnimation(
        fromPoint: MPoint,
        toPoint: MPoint,
        progress: Float
    ) {
        binding.mazePlayView.update { action ->
//            log("updateMoveAnimation: from = $fromPoint, to = $toPoint, progress = $progress")
            action.setFocus(toPoint.x, toPoint.y)
            action.setFrom(fromPoint.x, fromPoint.y)
            action.updateProgress(progress)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mazeController.destroy()
    }

}