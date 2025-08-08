package com.lollipop.wear.maze.presentation.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lollipop.wear.maze.databinding.FragmentMainHomeBinding

class HomeFragment : MainBaseFragment() {

    private var binding: FragmentMainHomeBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val newBinding = FragmentMainHomeBinding.inflate(inflater, container, false)
        binding = newBinding
        return newBinding.root
    }

}