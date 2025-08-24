package com.lollipop.wear.blocksbuilding.impl

import android.view.View
import com.lollipop.wear.blocksbuilding.IBlock

class StaticBlock() : IBlock {

    private var contentView: View? = null
    private var updateCallback: ((View) -> Unit)? = null

    fun bind(view: View) {
        contentView = view
    }

    override fun notifyUpdate() {
        contentView?.let {
            updateCallback?.invoke(it)
        }
    }

    override fun onUpdate(updateCallback: (View) -> Unit) {
        this.updateCallback = updateCallback
    }

}