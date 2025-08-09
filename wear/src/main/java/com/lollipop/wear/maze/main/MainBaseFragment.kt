package com.lollipop.wear.maze.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.play.core.controller.TimeDelegate
import com.lollipop.play.core.data.MazeHistory
import com.lollipop.play.core.data.PreferencesHelper
import com.lollipop.wear.maze.base.WearListHelper
import com.lollipop.wear.maze.databinding.FragmentMainSubpageBinding
import com.lollipop.wear.maze.databinding.ItemMainMazeBinding

abstract class MainBaseFragment : Fragment() {

    private val listHelper = WearListHelper()

    private val timeDelegate = TimeDelegate.auto(this) { time ->
        updateTime(time)
    }

    private var binding: FragmentMainSubpageBinding? = null

    protected fun updateTitle(title: String) {
        listHelper.updateTitle(title)
    }

    protected var settings: PreferencesHelper? = null

    protected fun createAdapter(vararg contentAdapter: RecyclerView.Adapter<*>): RecyclerView.Adapter<*> {
        return listHelper.createAdapter(*contentAdapter)
    }

    protected fun updateTime(time: String) {
        listHelper.updateTime(time)
    }

    protected fun optSettings(context: Context): PreferencesHelper {
        val helper = settings
        if (helper != null) {
            return helper
        }
        val newHelper = PreferencesHelper.create(context)
        settings = helper
        return newHelper
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        optSettings(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val newBinding = FragmentMainSubpageBinding.inflate(inflater, container, false)
        binding = newBinding
        return newBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply { onViewCreated(this) }
    }

    abstract fun onViewCreated(binding: FragmentMainSubpageBinding)

    protected fun initRecyclerView(
        binding: FragmentMainSubpageBinding,
        vararg adapter: RecyclerView.Adapter<*>
    ) {
        val recyclerView = binding.recyclerView
        recyclerView.adapter = createAdapter(*adapter)
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
    }

    protected open class MazeHistoryAdapter(
        private val mazeHistoryList: List<MazeHistory>,
        private val onItemClick: (Int, MazeHistory) -> Unit
    ) : WearListHelper.BasicAdapter<MazeHistoryHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): MazeHistoryHolder {
            return MazeHistoryHolder(inflate(parent), ::onItemClick)
        }

        private fun onItemClick(position: Int) {
            if (position < 0 || position >= mazeHistoryList.size) {
                return
            }
            onItemClick(position, mazeHistoryList[position])
        }

        override fun onBindViewHolder(
            holder: MazeHistoryHolder,
            position: Int
        ) {
            holder.bind(mazeHistoryList[position])
        }

        override fun getItemCount(): Int {
            return mazeHistoryList.size
        }
    }


    protected class MazeHistoryHolder(
        private val binding: ItemMainMazeBinding,
        private val onClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.overviewView.setDisplayMap(false)
            binding.overviewView.setMin(1F, 1F)
            binding.cardView.setOnClickListener {
                onItemClick()
            }
        }

        private fun onItemClick() {
            onClick(bindingAdapterPosition)
        }

        fun bind(history: MazeHistory) {
            binding.stepView.text = history.pathLength.toString()
            binding.overviewView.setMap(history.maze, history.path)
            binding.timeView.text = history.timeDisplay
            binding.sizeView.text = history.level
        }

    }

}