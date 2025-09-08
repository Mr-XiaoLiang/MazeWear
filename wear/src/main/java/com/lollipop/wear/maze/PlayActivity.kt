package com.lollipop.wear.maze

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.lollipop.maze.Maze
import com.lollipop.maze.MazeTest
import com.lollipop.maze.data.MBlock
import com.lollipop.maze.data.MPoint
import com.lollipop.maze.data.MTreasure
import com.lollipop.play.core.controller.LifecycleHelper
import com.lollipop.play.core.controller.MazeController
import com.lollipop.play.core.data.DataManager
import com.lollipop.play.core.helper.JoystickDirection
import com.lollipop.play.core.helper.registerLog
import com.lollipop.play.core.helper.tagName
import com.lollipop.wear.maze.base.MazeActivityHelper
import com.lollipop.wear.maze.databinding.ActivityPlayBinding
import com.lollipop.wear.maze.play.PlayLayer
import com.lollipop.wear.maze.play.layer.MenuLayer
import com.lollipop.wear.maze.play.layer.PlayingLayer
import com.lollipop.wear.maze.play.layer.VictoryLayer
import com.lollipop.wear.maze.play.state.PlayPageState

class PlayActivity : AppCompatActivity(), MazeController.Callback,
    PlayingLayer.Callback,
    VictoryLayer.Callback,
    MenuLayer.Callback {

    companion object {

        private const val KEY_MAZE_WIDTH = "KEY_MAZE_WIDTH"

        fun newMaze(context: Context, width: Int) {
            val intent = Intent(context, PlayActivity::class.java)
            intent.putExtra(KEY_MAZE_WIDTH, width)
            context.startActivity(intent)
        }

        fun resumeMaze(context: Context, mazeCache: String) {
            MazeActivityHelper.startWithMaze<PlayActivity>(context, mazeCache)
        }

        private fun getMazeWidth(intent: Intent): Int {
            return intent.getIntExtra(KEY_MAZE_WIDTH, 10)
        }

    }

    private val log = registerLog()

    private val binding by lazy {
        ActivityPlayBinding.inflate(layoutInflater)
    }

    private val lifecycleHelper by lazy {
        LifecycleHelper.auto(this)
    }

    private val mazeController by lazy {
        MazeController(lifecycleHelper, this)
    }

    private var mazeCachePath: String = ""

    private val stateController by lazy {
        StateController(this, binding.layerContainer)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        postState(PlayPageState.Init)
        loadData()
    }

    private fun postState(state: PlayPageState) {
        stateController.postState(state)
    }

    override fun onResume() {
        super.onResume()
        stateController.onResume()
        mazeController.onResume()
    }

    override fun onPause() {
        super.onPause()
        stateController.onPause()
    }

    private fun loadData() {
        mazeCachePath = MazeActivityHelper.findFromIntent(intent)
        if (mazeCachePath.isNotEmpty()) {
            mazeController.load(mazeCachePath)
        } else {
            mazeController.create(getMazeWidth(intent))
        }
    }

    override fun onLoadingStart() {
        postState(PlayPageState.Loading)
    }

    override fun onLoadingEnd() {
    }

    override fun onMazeResult(
        treasure: MTreasure,
        focus: MBlock
    ) {
        onNewMaze(treasure, focus)
    }

    override fun onMazeCacheNotFound() {
        postState(PlayPageState.Error(R.string.msg_maze_not_found))
    }

    override fun onPointChange(
        fromPoint: MPoint,
        toPoint: MPoint
    ) {
        onMove(fromPoint, toPoint)
    }

    override fun onComplete(treasure: MTreasure) {
        saveMaze(treasure)
        postState(
            PlayPageState.Complete(
                isNewPath = mazeController.isRetry,
                treasure = treasure
            )
        )
    }

    override fun onJoystickTouch(direction: JoystickDirection) {
        mazeController.manipulate(direction)
    }

    override fun openMenu(state: PlayPageState.Playing) {
        postState(PlayPageState.Menu(continueState = state, treasure = state.treasure))
    }

    override fun onContinue(state: PlayPageState) {
        postState(state)
    }

    override fun onNewGame() {
        mazeController.newGame()
    }

    override fun onExit() {
        onBackPressedDispatcher.onBackPressed()
    }

    private fun onNewMaze(treasure: MTreasure, focus: MBlock) {
        postState(PlayPageState.Playing(treasure, focus))
        val maze = treasure.mazeMap
        log("start = [${maze.start.x}, ${maze.start.y}] ")
        log("map \n" + MazeTest.print(maze).build())
    }

    override fun onContinue() {
        // 继续就重新发一遍游玩的事件
        val snapshot = mazeController.snapshot() ?: return
        val focus = MBlock(mazeController.focus)
        postState(PlayPageState.Playing(treasure = snapshot, focus = focus))
    }

    private fun onMove(
        fromPoint: MPoint,
        toPoint: MPoint
    ) {
        postState(PlayPageState.PointChange(fromPoint, toPoint))
    }

    override fun onStop() {
        super.onStop()
        if (mazeController.isPathChanged) {
            val treasure = mazeController.snapshot()
            if (treasure != null) {
                // 保存路径，下一次更新的时候，可以再次更新
                saveMaze(treasure)
            }
        }
    }

    private fun saveMaze(treasure: MTreasure) {
        val current = treasure.path
        var hiPath = treasure.hiPath
        val mazeMap = treasure.mazeMap
        val completeStepCount = Maze.completeStepCount(mazeMap, current)
        if (completeStepCount > 0) {
            val hiStepCount = hiPath?.size ?: Int.MAX_VALUE
            if (hiStepCount > completeStepCount) {
                hiPath = current.subPath(completeStepCount)
            }
        }
        // 保存路径，下一次更新的时候，可以再次更新
        mazeCachePath = DataManager.update(
            context = this,
            filePath = mazeCachePath,
            treasure = MTreasure(mazeMap = mazeMap, path = current, hiPath = hiPath),
            isComplete = completeStepCount > 0 || hiPath != null,
            onEnd = ::onSaveEnd
        ).path
        mazeController.onSaved()
    }

    private fun onSaveEnd() {
    }

    override fun onDestroy() {
        super.onDestroy()
        mazeController.destroy()
    }

    private class StateController(
        private val activity: AppCompatActivity,
        private val layerContainer: ViewGroup
    ) {

        private var isResumed = false
        private var pendingState: PlayPageState? = null

        var pageState: PlayPageState = PlayPageState.Init
            private set

        val layerList = mutableListOf<PlayLayer>()

        private val log = registerLog()

        fun postState(state: PlayPageState) {
            if (isResumed) {
                pendingState = null
                log("postState： updateState = ${state.tagName()}")
                activity.runOnUiThread {
                    updateState(state)
                }
            } else {
                log("postState： pendingState = ${state.tagName()}")
                pendingState = state
            }
        }

        fun onResume() {
            isResumed = true
            releaseState()
        }

        private fun releaseState() {
            val state = pendingState
            if (state != null) {
                pendingState = null
                updateState(state)
            }
        }

        private fun updateState(state: PlayPageState) {
            val oldState = pageState
            pageState = state
            log("updateState： oldState = ${oldState.tagName()}, newState = ${state.tagName()}")
            val oldLayer = findLayer(oldState)
            val newLayer = findLayer(state)
            log("updateState： oldLayer = ${oldLayer.tagName()}, newLayer = ${newLayer.tagName()}")
            if (oldLayer != newLayer) {
                log("updateState： oldLayer.onHide(), newLayer.onShow()")
                oldLayer.onHide()
                newLayer.onShow()
            }
            newLayer.setState(state)
        }

        fun onPause() {
            isResumed = false
        }


        private fun findLayer(state: PlayPageState): PlayLayer {
            return findLayer(state.layerClass)
        }

        private fun findLayer(layerClass: Class<out PlayLayer>): PlayLayer {
            for (layer in layerList) {
                if (layer.javaClass == layerClass) {
                    return layer
                }
            }
            val constructor = layerClass.getDeclaredConstructor(AppCompatActivity::class.java)
            val newInstance = constructor.newInstance(activity)
            layerList.add(newInstance)
            val view = newInstance.getView(layerContainer)
            layerContainer.addView(
                view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            newInstance.onAttach()
            return newInstance
        }

    }

}