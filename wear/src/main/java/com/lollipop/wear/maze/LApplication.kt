package com.lollipop.wear.maze

import android.app.Application
import com.lollipop.play.core.MazePlayConfig
import com.lollipop.play.core.helper.MLog
import com.lollipop.wear.blocksbuilding.dsl.BBDSL
import com.lollipop.wear.blocksbuilding.dsl.BBLog

class LApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        BBDSL.init(this)
        BBDSL.logImpl = BBLogImpl()
        MazePlayConfig.init(this, true)
    }

    private class BBLogImpl : BBLog {

        private val logImpl = MLog("BB")

        override fun invoke(message: String) {
            logImpl.i(message)
        }

        override fun invoke(message: String, throwable: Throwable) {
            logImpl.e(message, throwable)
        }
    }

}