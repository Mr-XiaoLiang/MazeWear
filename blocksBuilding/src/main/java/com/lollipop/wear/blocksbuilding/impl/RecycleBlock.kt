package com.lollipop.wear.blocksbuilding.impl

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.wear.blocksbuilding.IBlock

class RecycleBlock : IBlock {

    private var updateCallback: ((View) -> Unit)? = null
    private var adapter: RecyclerView.Adapter<*>? = null
    private var adapterIndex = -1

    fun bind(adapter: RecyclerView.Adapter<*>, index: Int) {
        this.adapter = adapter
        this.adapterIndex = index
    }

    fun updateView(view: View) {
        updateCallback?.invoke(view)
    }

    override fun notifyUpdate() {
        val a = adapter ?: return
        val index = adapterIndex
        if (index >= 0 && index < a.itemCount) {
            a.notifyItemChanged(index)
        }
    }

    override fun onUpdate(updateCallback: (View) -> Unit) {
        this.updateCallback = updateCallback
    }

    fun createHolder(view: View): Holder {
        return Holder(this, view)
    }

    class Holder(val block: RecycleBlock, val view: View) {

        fun onBind(adapter: RecyclerView.Adapter<*>, index: Int) {
            block.bind(adapter, index)
            block.updateView(view)
        }

    }

}