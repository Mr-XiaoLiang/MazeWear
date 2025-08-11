package com.lollipop.wear.maze

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.lollipop.maze.MazeMap
import com.lollipop.maze.MazeTest
import com.lollipop.maze.data.MBlock
import com.lollipop.maze.data.MPath
import com.lollipop.maze.data.MPoint
import com.lollipop.play.core.controller.LifecycleHelper
import com.lollipop.play.core.controller.MazeController
import com.lollipop.play.core.controller.MazeMoveAnimator
import com.lollipop.play.core.controller.TimeDelegate
import com.lollipop.play.core.data.DataManager
import com.lollipop.play.core.helper.JoystickDelegate
import com.lollipop.play.core.helper.JoystickDirection
import com.lollipop.play.core.helper.dp2px
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
        private const val KEY_MAZE_CACHE = "KEY_MAZE_CACHE"

        fun newMaze(context: Context, width: Int) {
            val intent = Intent(context, PlayActivity::class.java)
            intent.putExtra(KEY_MAZE_WIDTH, width)
            context.startActivity(intent)
        }

        fun resumeMaze(context: Context, mazeCache: String) {
            val intent = Intent(context, PlayActivity::class.java)
            intent.putExtra(KEY_MAZE_CACHE, mazeCache)
            context.startActivity(intent)
        }

        private fun getMazeWidth(intent: Intent): Int {
            return intent.getIntExtra(KEY_MAZE_WIDTH, 10)
        }

        private fun getMazeCache(intent: Intent): String {
            return intent.getStringExtra(KEY_MAZE_CACHE) ?: ""
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

    private var mazeCachePath: String = ""

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

    override fun onGenericMotionEvent(event: MotionEvent?): Boolean {
        if (binding.joystickView.onGenericMotionEvent(event)) {
            return true
        }
        return super.onGenericMotionEvent(event)
    }

    private fun loadData() {
        mazeCachePath = getMazeCache(intent)
        if (mazeCachePath.isNotEmpty()) {
            mazeController.load(mazeCachePath)
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
        setMapViewStyle(
            lineWidthMin = 1F.dp2px(this),
            extremeRadiusMin = 3F.dp2px(this),
            lineColor = Color.WHITE,
            extremeStartColor = Color.RED,
            extremeEndColor = Color.GREEN,
            mapColor = Color.GRAY
        )
        binding.settlementMapView
        binding.osdButton.setOnClickListener {
            osdPanelHelper.toggle()
        }
        binding.menuPanel.setOnClickListener {
            osdPanelHelper.hide()
        }
        TimeDelegate.auto(this) {
            binding.timeView.text = it
        }
        osdPanelHelper.onShow {
            binding.overviewView.updatePath()
        }
        binding.victoryContinueButton.setOnClickListener {
            binding.victoryPanel.isVisible = false
        }
        binding.victoryPanel.setOnClickListener { }
        osdPanelHelper.init()
    }

    private fun setMapViewStyle(
        lineWidthMin: Float,
        extremeRadiusMin: Float,
        lineColor: Int,
        extremeStartColor: Int,
        extremeEndColor: Int,
        mapColor: Int
    ) {
        binding.overviewView.setMin(lineWidthMin, extremeRadiusMin)
        binding.overviewView.setColor(
            lineColor = lineColor,
            extremeStartColor = extremeStartColor,
            extremeEndColor = extremeEndColor,
            mapColor = mapColor
        )
        binding.settlementMapView.setMin(lineWidthMin, extremeRadiusMin)
        binding.settlementMapView.setColor(
            lineColor = lineColor,
            extremeStartColor = extremeStartColor,
            extremeEndColor = extremeEndColor,
            mapColor = mapColor
        )
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

    override fun onMazeCacheNotFound() {
        TODO("Not yet implemented")
    }

    override fun onPointChange(
        fromPoint: MPoint,
        toPoint: MPoint
    ) {
        lifecycleHelper.post {
            onMove(fromPoint, toPoint)
        }
    }

    override fun onComplete() {
        binding.victoryPanel.isVisible = true
        binding.settlementMapView.updatePath()
    }

    private fun onJoystickTouch(direction: JoystickDirection) {
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
        binding.overviewView.setMap(maze, path)
        binding.settlementMapView.setMap(maze, path)
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
            action.setFocus(toPoint.x, toPoint.y)
            action.setFrom(fromPoint.x, fromPoint.y)
            action.updateProgress(progress)
        }
    }

    override fun onStop() {
        super.onStop()
        val currentMaze = mazeController.currentMaze
        val currentPath = mazeController.currentPath
        if (currentMaze != null && currentPath != null) {
            // 保存路径，下一次更新的时候，可以再次更新
            mazeCachePath = DataManager.update(
                context = this,
                filePath = mazeCachePath,
                mazeMap = currentMaze,
                path = currentPath,
                isComplete = mazeController.isComplete,
                onEnd = ::onSaveEnd
            ).path
        }
    }

    private fun onSaveEnd() {
    }

    override fun onDestroy() {
        super.onDestroy()
        mazeController.destroy()
    }

}