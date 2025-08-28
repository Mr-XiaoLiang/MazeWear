package com.lollipop.wear.blocksbuilding.impl

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.wear.blocksbuilding.BlockManager
import com.lollipop.wear.blocksbuilding.dsl.bbLog

class BBRecyclerAdapter<T>(
    private val items: List<T>,
    private val typeProvider: (T) -> Int,
    private val createItem: (Int) -> View,
    private val update: (View, T) -> Unit
) : RecyclerView.Adapter<BBRecyclerViewHolder>(), BlockManager {

    private val log = bbLog()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BBRecyclerViewHolder {
        log("onCreateViewHolder: $viewType")
        return BBRecyclerViewHolder(createItem(viewType))
    }

    override fun onBindViewHolder(
        holder: BBRecyclerViewHolder,
        position: Int
    ) {
        log("onBindViewHolder: $position")
        val item = items[position]
        update(holder.itemView, item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return typeProvider(items[position])
    }

    override fun notifyUpdate(position: Int) {
        notifyItemChanged(position)
    }

    override fun notifyUpdate(position: Int, count: Int) {
        notifyItemRangeChanged(position, count)
    }

    override fun notifyInsert(position: Int) {
        notifyItemInserted(position)
    }

    override fun notifyInsert(position: Int, count: Int) {
        notifyItemRangeInserted(position, count)
    }

    override fun notifyRemove(position: Int) {
        notifyItemRemoved(position)
    }

    override fun notifyRemove(position: Int, count: Int) {
        notifyItemRangeRemoved(position, count)
    }

    override fun notifyMove(fromPosition: Int, toPosition: Int) {
        notifyItemMoved(fromPosition, toPosition)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun notifyAllChanged() {
        notifyDataSetChanged()
    }

}

class BBRecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view)
