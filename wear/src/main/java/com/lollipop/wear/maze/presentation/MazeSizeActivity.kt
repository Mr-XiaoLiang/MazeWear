package com.lollipop.wear.maze.presentation

import android.view.View
import com.lollipop.play.core.page.ThumbBezelActivity
import com.lollipop.play.core.view.ThumbBezelView
import com.lollipop.wear.maze.databinding.ActivityMazeSizeBinding

class MazeSizeActivity : ThumbBezelActivity() {

    private val binding by lazy {
        ActivityMazeSizeBinding.inflate(layoutInflater)
    }

    private var mazeWidth = 0

    override fun getContentView(): View {
        return binding.root
    }

    override fun onThumbMove(state: ThumbBezelView.ThumbState) {
        when (state) {
            ThumbBezelView.ThumbState.NEXT -> {
                mazeWidth++
            }

            ThumbBezelView.ThumbState.PREVIOUS -> {
                mazeWidth--
            }
        }
        binding.textView.text = "$mazeWidth"
    }
}