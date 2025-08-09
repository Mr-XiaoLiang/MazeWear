package com.lollipop.wear.maze.main

import com.lollipop.wear.maze.R
import com.lollipop.wear.maze.databinding.FragmentMainSubpageBinding

class ConfigFragment : MainBaseFragment() {

    override fun onViewCreated(binding: FragmentMainSubpageBinding) {
        updateTitle(binding.root.context.getString(R.string.title_config))
        initRecyclerView(binding)
    }

}