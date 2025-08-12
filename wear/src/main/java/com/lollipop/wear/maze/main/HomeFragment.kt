package com.lollipop.wear.maze.main

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.play.core.data.DataManager
import com.lollipop.play.core.data.DataObserver
import com.lollipop.play.core.data.MazeHistory
import com.lollipop.play.core.data.mazeSettings
import com.lollipop.wear.maze.PlayActivity
import com.lollipop.wear.maze.R
import com.lollipop.wear.maze.base.WearListHelper
import com.lollipop.wear.maze.databinding.FragmentMainSubpageBinding
import com.lollipop.wear.maze.databinding.ItemMainHomeNewBinding

class HomeFragment : MainBaseFragment() {

    private val dataObserver by lazy {
        DataObserver(::onDataChanged)
    }

    private val dataObserverController by lazy {
        dataObserver.controllerByManual()
    }

    private val mazeList = mutableListOf<MazeHistory>()

    private val newGameAdapter by lazy {
        NewGameAdapter(::onNewGameClick)
    }

    private val mazeHistoryAdapter by lazy {
        MazeHistoryAdapter(mazeList, ::onMazeHistoryClick)
    }

    override fun onViewCreated(binding: FragmentMainSubpageBinding) {
        updateTitle(binding.root.context.getString(R.string.app_name))
        initRecyclerView(binding, newGameAdapter, mazeHistoryAdapter)
    }

    override fun onResume() {
        super.onResume()
        newGameAdapter.update()
        dataObserverController.resume()
    }

    override fun onPause() {
        super.onPause()
        dataObserverController.pause()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onDataChanged() {
        dataObserver.releasePending()
        DataManager.copyListUnfinished(mazeList)
        log("onDataChanged: ${mazeList.size}")
        mazeHistoryAdapter.notifyDataSetChanged()
    }

    private fun onNewGameClick() {
        activity?.let {
            val mazeWidth = optSettings(it).mazeWidth
            PlayActivity.newMaze(it, mazeWidth)
        }
    }

    private fun onMazeHistoryClick(position: Int, mazeHistory: MazeHistory) {
        activity?.let {
            PlayActivity.resumeMaze(it, mazeHistory.cachePath)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dataObserver.destroy()
    }

    private class NewGameAdapter(
        private val onNewGameClick: () -> Unit
    ) : WearListHelper.BasicAdapter<NewGameHolder>() {

        fun update() {
            notifyItemChanged(0)
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): NewGameHolder {
            return NewGameHolder(inflate(parent), onNewGameClick)
        }

        override fun onBindViewHolder(
            holder: NewGameHolder,
            position: Int
        ) {
            holder.bind()
        }

        override fun getItemCount(): Int {
            return 1
        }
    }

    private class NewGameHolder(
        private val binding: ItemMainHomeNewBinding,
        private val onClick: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val settings by itemView.context.mazeSettings()

        init {
            binding.root.setOnClickListener {
                onClick()
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind() {
            val mazeWidth = settings.mazeWidth
            binding.sizeView.text = "${mazeWidth}x${mazeWidth}"
        }

    }

}