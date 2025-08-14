package com.lollipop.wear.maze

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
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

    private var pageState: PageState = PageState.Init

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
        binding.victoryBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.errorBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.victoryContinueButton.setOnClickListener {
            binding.victoryPanel.isVisible = false
        }
        binding.victoryPanel.setOnClickListener { }
        osdPanelHelper.init()

        updatePageState(PageState.Init)
    }

    private fun updatePageState(state: PageState) {
        pageState = state
        osdPanelHelper.hide()
        when (state) {
            PageState.Init -> {
                binding.apply {
                    contentLoadingView.setIndeterminate(false)
                    hideViews(
                        mazePlayView,
                        mazeFogView,
                        joystickRingView,
                        joystickView,
                        menuPanel,
                        osdActionPanel,
                        contentLoadingView,
                        victoryPanel,
                        errorPanel
                    )
                }
            }

            PageState.Loading -> {
                lifecycleHelper.post {
                    binding.apply {
                        hideViews(
                            mazePlayView,
                            mazeFogView,
                            joystickRingView,
                            joystickView,
                            menuPanel,
                            osdActionPanel,
                            victoryPanel,
                            errorPanel
                        )
                        contentLoadingView.show()
                        contentLoadingView.setIndeterminate(true)
                    }
                }
            }

            is PageState.Complete -> {
                lifecycleHelper.post {
                    binding.apply {
                        // 加载动画要停止
                        contentLoadingView.setIndeterminate(false)
                        hideViews(
                            mazePlayView,
                            mazeFogView,
                            joystickRingView,
                            joystickView,
                            menuPanel,
                            osdActionPanel,
                            contentLoadingView,
                            victoryPanel,
                            errorPanel
                        )
                        showViews(
                            victoryPanel
                        )
                        if (state.isNewPath) {
                            victoryHintView.setText(R.string.hint_victory_retry)
                            victoryContinueButton.setText(R.string.button_coverage)
                        } else {
                            victoryHintView.setText(R.string.hint_victory)
                            victoryContinueButton.setText(R.string.button_continue)
                        }
                        settlementMapView.updatePath()
                    }
                }
            }

            is PageState.Error -> {
                lifecycleHelper.post {
                    binding.apply {
                        // 加载动画要停止
                        contentLoadingView.setIndeterminate(false)
                        hideViews(
                            mazePlayView,
                            mazeFogView,
                            joystickRingView,
                            joystickView,
                            menuPanel,
                            osdActionPanel,
                            contentLoadingView,
                            victoryPanel
                        )
                        showViews(errorPanel)
                        errorMessageView.setText(state.message)
                    }
                }
            }

            is PageState.Playing -> {
                lifecycleHelper.post {
                    binding.apply {
                        // 加载动画要停止
                        contentLoadingView.setIndeterminate(false)
                        hideViews(
                            menuPanel,
                            osdActionPanel,
                            contentLoadingView,
                            victoryPanel,
                            errorPanel
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
                        settlementMapView.setMap(maze, path)

                    }
                }
            }
        }
    }

    private fun hideViews(vararg views: View) {
        views.forEach {
            it.isInvisible = true
        }
    }

    private fun showViews(vararg views: View) {
        views.forEach {
            it.isVisible = true
        }
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
        updatePageState(PageState.Loading)
    }

    override fun onLoadingEnd() {
    }

    override fun onMazeResult(maze: MazeMap, path: MPath, focus: MBlock) {
        lifecycleHelper.post {
            onNewMaze(maze, path, focus)
        }
    }

    override fun onMazeCacheNotFound() {
        updatePageState(PageState.Error(R.string.msg_maze_not_found))
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
        updatePageState(PageState.Complete(mazeController.isRetry))
    }

    private fun onJoystickTouch(direction: JoystickDirection) {
        mazeController.manipulate(direction)
    }

    private fun onNewMaze(maze: MazeMap, path: MPath, focus: MBlock) {
        updatePageState(PageState.Playing(maze, path, focus))
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

    private sealed class PageState {
        object Init : PageState()
        object Loading : PageState()
        class Playing(val maze: MazeMap, val path: MPath, val focus: MBlock) : PageState()
        class Complete(val isNewPath: Boolean) : PageState()
        class Error(val message: Int) : PageState()
    }

}