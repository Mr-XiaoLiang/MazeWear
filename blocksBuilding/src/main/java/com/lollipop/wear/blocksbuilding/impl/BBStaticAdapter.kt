package com.lollipop.wear.blocksbuilding.impl

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.wear.blocksbuilding.dsl.bbLog

class BBStaticAdapter() : RecyclerView.Adapter<BBStaticHolder>() {

    private val log = bbLog()

    private val contentList = ArrayList<RecycleBlock.Holder>()

    fun add(view: RecycleBlock.Holder) {
        contentList.add(view)
        notifyItemInserted(contentList.size - 1)
    }

    fun remove(view: RecycleBlock.Holder) {
        val index = contentList.indexOf(view)
        if (index >= 0) {
            contentList.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BBStaticHolder {
//        log("onCreateViewHolder: ${viewType + 1} / ${contentList.size}")
        return BBStaticHolder(contentList[viewType])
    }

    override fun onBindViewHolder(holder: BBStaticHolder, position: Int) {
//        log("onBindViewHolder: ${position + 1} / ${contentList.size}")
        holder.bind(this, position)
    }

    override fun getItemCount(): Int {
        return contentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}

class BBStaticHolder(private val holder: RecycleBlock.Holder) :
    RecyclerView.ViewHolder(holder.view) {

    fun bind(adapter: RecyclerView.Adapter<*>, position: Int) {
        holder.onBind(adapter, position)
    }

}
