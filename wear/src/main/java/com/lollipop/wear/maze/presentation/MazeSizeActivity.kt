package com.lollipop.wear.maze.presentation

import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding.textView.setTypeface(ResourcesCompat.getFont(this, R.font.digital_numbers_regular))
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
        if (mazeWidth < 0) {
            mazeWidth = 0
        }
        if (mazeWidth > 999) {
            mazeWidth = 999
        }
        val builder = StringBuilder()
        if (mazeWidth < 100) {
            builder.append("0")
        }
        if (mazeWidth < 10) {
            builder.append("0")
        }
        builder.append(mazeWidth)
        binding.textView.text = builder.toString()
    }
}