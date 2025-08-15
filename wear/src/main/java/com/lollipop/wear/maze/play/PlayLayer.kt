package com.lollipop.wear.maze.play

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.lollipop.wear.maze.play.state.PlayPageState

abstract class PlayLayer(protected val activity: AppCompatActivity) {

    abstract fun getView(container: ViewGroup): View

    open fun onAttach() {}

    open fun onShow() {}

    open fun onHide() {}

    abstract fun setState(state: PlayPageState)

}