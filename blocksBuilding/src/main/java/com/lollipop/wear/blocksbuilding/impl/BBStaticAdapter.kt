package com.lollipop.wear.blocksbuilding.impl

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class BBStaticAdapter() : RecyclerView.Adapter<BBStaticHolder>() {

    private val contentList = ArrayList<View>()

    fun add(view: View) {
        contentList.add(view)
        notifyItemInserted(contentList.size - 1)
    }

    fun remove(view: View) {
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
        return BBStaticHolder(contentList[viewType])
    }

    override fun onBindViewHolder(holder: BBStaticHolder, position: Int) {}

    override fun getItemCount(): Int {
        return contentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}

class BBStaticHolder(view: View) : RecyclerView.ViewHolder(view)
