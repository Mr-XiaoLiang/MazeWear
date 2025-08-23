package com.lollipop.wear.maze.blocks

import android.content.Context
import androidx.lifecycle.LifecycleOwner

interface BlocksOwner {

    val context: Context

    val lifecycleOwner: LifecycleOwner

}