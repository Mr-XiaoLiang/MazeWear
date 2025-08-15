package com.lollipop.wear.maze.play.layer

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import com.lollipop.wear.maze.play.state.PlayPageState

class EmptyLayer(activity: AppCompatActivity) : BasicLayer(activity) {

    override fun createView(
        container: ViewGroup
    ): View {
        val newView = ImageView(activity)
        newView.setImageDrawable(Color.BLACK.toDrawable())
        return newView
    }

    override fun setState(state: PlayPageState) {
    }
}