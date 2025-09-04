package com.lollipop.wear.maze.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lollipop.play.core.data.DataManager
import com.lollipop.play.core.data.DataObserver
import com.lollipop.play.core.data.MazeHistory
import com.lollipop.wear.blocksbuilding.data.ListDataProvider
import com.lollipop.wear.blocksbuilding.data.staticData
import com.lollipop.wear.maze.MazeInfoActivity
import com.lollipop.wear.maze.R
import com.lollipop.wear.maze.blocks.ScaffoldBlock
import com.lollipop.wear.maze.blocks.wearBlocksView

class HistoryFragment : MainBaseFragment() {

    private val dataObserver by lazy {
        DataObserver(::onDataChanged)
    }

    private val mazeHistoryProvider = ListDataProvider<MazeHistory>()

    private val dataObserverController by lazy {
        dataObserver.controllerByManual()
    }

    private val mazeList = mutableListOf<MazeHistory>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return wearBlocksView(inflater.context) {
            ScaffoldBlock(
                title = staticData(blocksOwner.context.getString(R.string.title_history))
            ) {
                items(
                    provider = mazeHistoryProvider
                ) {
                    MazeHolder(
                        onItemClick = ::onMazeHistoryClick
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        dataObserverController.resume()
    }

    override fun onPause() {
        super.onPause()
        dataObserverController.pause()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onDataChanged() {
        dataObserver.releasePending()
        DataManager.copyListComplete(mazeList)
        mazeHistoryProvider.reset(mazeList)
    }

    private fun onMazeHistoryClick(position: Int, mazeHistory: MazeHistory) {
        activity?.let {
            MazeInfoActivity.start(it, mazeHistory.cachePath)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dataObserver.destroy()
    }


}