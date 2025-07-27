package com.lollipop.wear.maze.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lollipop.maze.MazeMap
import com.lollipop.maze.data.MPath
import com.lollipop.wear.maze.R
import com.lollipop.wear.maze.controller.LifecycleHelper
import com.lollipop.wear.maze.controller.MazeController
import com.lollipop.wear.maze.databinding.ActivityPlayBinding
import com.lollipop.wear.maze.view.OsdPanelHelper
import com.lollipop.wear.maze.view.joystick.JoystickRingRestrictedZone
import com.lollipop.wear.maze.view.joystick.RotateJoystickDisplay

class PlayActivity : AppCompatActivity(), MazeController.Callback {

    companion object {

        private const val KEY_MAZE_WIDTH = "KEY_MAZE_WIDTH"

        fun start(context: Context, width: Int) {
            val intent = Intent(context, PlayActivity::class.java)
            intent.putExtra(KEY_MAZE_WIDTH, width)
            context.startActivity(intent)
        }

        private fun getMazeWidth(intent: Intent): Int {
            return intent.getIntExtra(KEY_MAZE_WIDTH, 10)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
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
            RotateJoystickDisplay.create(
                R.drawable.bg_joystick_ring
            )
        )
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

    private fun onNewMaze(maze: MazeMap, path: MPath) {
        binding.contentLoadingView.hide()
        binding.mazePlayView.setSource(maze.map, path)
    }

}