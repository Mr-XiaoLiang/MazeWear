package com.lollipop.wear.maze.base

import android.app.Activity
import android.content.Context
import android.content.Intent

object MazeActivityHelper {

    private const val KEY_MAZE_CACHE = "KEY_MAZE_CACHE"

    fun bindToIntent(intent: Intent, mazeCache: String) {
        intent.putExtra(KEY_MAZE_CACHE, mazeCache)
    }

    fun findFromIntent(intent: Intent): String {
        return intent.getStringExtra(KEY_MAZE_CACHE) ?: ""
    }

    inline fun <reified T : Activity> startWithMaze(
        context: Context,
        mazeCache: String,
        buildIntent: (Intent) -> Unit = {}
    ) {
        val intent = Intent(context, T::class.java)
        bindToIntent(intent, mazeCache)
        buildIntent(intent)
        context.startActivity(intent)
    }

}