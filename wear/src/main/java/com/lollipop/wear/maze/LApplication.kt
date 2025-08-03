package com.lollipop.wear.maze

import android.app.Application
import com.lollipop.play.core.MazePlayConfig

class LApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        MazePlayConfig.init(this, true)
    }

}