package com.lollipop.wear.maze.presentation

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.lollipop.maze.MazeMap
import com.lollipop.maze.MazeTest
import com.lollipop.maze.data.MPath
import com.lollipop.wear.maze.controller.LifecycleHelper
import com.lollipop.wear.maze.controller.MazeController
import com.lollipop.wear.maze.databinding.ActivityPlayBinding
import com.lollipop.wear.maze.helper.JoystickDelegate
import com.lollipop.wear.maze.view.OsdPanelHelper
import com.lollipop.wear.maze.view.draw.color.ColorPathDrawable
import com.lollipop.wear.maze.view.draw.color.ColorSpiritDrawable
import com.lollipop.wear.maze.view.draw.color.ColorTileDrawable
import com.lollipop.wear.maze.view.joystick.JoystickRingRestrictedZone
import com.lollipop.wear.maze.view.joystick.RotateJoystickDisplay

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

    private val binding by lazy {
        ActivityPlayBinding.inflate(layoutInflater)
    }

    private val osdPanelHelper by lazy {
        OsdPanelHelper(binding.menuPanel)
    }

    private val mazeController by lazy {
        MazeController(this)
    }

    private val lifecycleHelper by lazy {
        LifecycleHelper.auto(this)
    }

    private val joystickDelegate = JoystickDelegate(::onJoystickTouch)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
        loadData()
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
        binding.joystickView.setJoystickTouchListener(joystickDelegate)
        binding.mazePlayView.update { action ->
            action.setTileDrawable(ColorTileDrawable().apply { color = Color.GRAY })
            action.setPathDrawable(ColorPathDrawable().apply { color = 0x330000FF })
            action.setSpiritDrawable(ColorSpiritDrawable().apply { color = Color.WHITE })
        }
        binding.osdButton.setOnClickListener {
            osdPanelHelper.toggle()
        }
        binding.menuPanel.setOnClickListener {
            osdPanelHelper.hide()
        }
        osdPanelHelper.init()
    }

    override fun onLoading() {
        lifecycleHelper.post {
            binding.contentLoadingView.show()
        }
    }

    override fun onMazeResult(maze: MazeMap, path: MPath) {
        lifecycleHelper.post {
            onNewMaze(maze, path)
        }
    }

    private fun onJoystickTouch(direction: JoystickDelegate.Direction) {
        // tODO
        Log.i("Maze", "direction = $direction")
    }

    private fun onNewMaze(maze: MazeMap, path: MPath) {
        binding.contentLoadingView.hide()
        if (path.isEmpty()) {
            path.add(maze.start)
        }
        binding.mazePlayView.update { action ->
            action.setSource(maze.map, path)
            val startPoint = path.last() ?: maze.start
            action.setFocus(startPoint.x, startPoint.y)
            action.setNext(startPoint.x, startPoint.y)
            action.updateProgress(0F)
        }
        Log.i("Maze", "start = [${maze.start.x}, ${maze.start.y}] ")
        Log.i("Maze", MazeTest.print(maze).build())
    }

    override fun onDestroy() {
        super.onDestroy()
        mazeController.destroy()
    }

}