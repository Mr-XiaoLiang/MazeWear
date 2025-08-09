package com.lollipop.wear.maze.main

import android.annotation.SuppressLint
import com.lollipop.play.core.data.DataManager
import com.lollipop.play.core.data.DataObserver
import com.lollipop.play.core.data.MazeHistory
import com.lollipop.wear.maze.R
import com.lollipop.wear.maze.databinding.FragmentMainSubpageBinding

class HistoryFragment : MainBaseFragment() {

    private val dataObserver by lazy {
        DataObserver(::onDataChanged)
    }

    private val dataObserverController by lazy {
        dataObserver.controllerByManual()
    }

    private val mazeList = mutableListOf<MazeHistory>()

    private val mazeHistoryAdapter by lazy {
        MazeHistoryAdapter(mazeList, ::onMazeHistoryClick)
    }

    override fun onViewCreated(binding: FragmentMainSubpageBinding) {
        updateTitle(binding.root.context.getString(R.string.title_history))
        initRecyclerView(binding, mazeHistoryAdapter)
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
        DataManager.copyListComplete(mazeList)
        mazeHistoryAdapter.notifyDataSetChanged()
    }

    private fun onMazeHistoryClick(position: Int, mazeHistory: MazeHistory) {
        // TODO
    }

    override fun onDestroy() {
        super.onDestroy()
        dataObserver.destroy()
    }


}