package com.lollipop.wear.maze.presentation.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lollipop.wear.maze.databinding.FragmentMainHistoryBinding

class HistoryFragment : MainBaseFragment() {

    private var binding: FragmentMainHistoryBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val newBinding = FragmentMainHistoryBinding.inflate(inflater, container, false)
        binding = newBinding
        return newBinding.root
    }

}