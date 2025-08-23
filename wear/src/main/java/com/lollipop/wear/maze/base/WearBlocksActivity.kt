package com.lollipop.wear.maze.base

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lollipop.wear.blocksbuilding.BuilderScope
import com.lollipop.wear.blocksbuilding.dsl.blocks
import com.lollipop.wear.blocksbuilding.dsl.layoutParams
import com.lollipop.wear.blocksbuilding.item.ItemSize
import com.lollipop.wear.maze.blocks.BlocksOwner

abstract class WearBlocksActivity() : AppCompatActivity(), BlocksOwner {

    override val context: Context = this

    override val lifecycleOwner: LifecycleOwner = this

    protected fun content(content: BuilderScope.() -> Unit) {
        val view = RecyclerView(this)
        view.layoutParams(ItemSize.Match, ItemSize.Match)
        setContentView(view)
        view.blocks(content)
    }

    protected fun swipeRefreshContent(
        onRefresh: (SwipeRefresh) -> Unit,
        content: BuilderScope.() -> Unit
    ) {
        val refreshLayout = SwipeRefreshLayout(this)
        SwipeRefresh(refreshLayout, onRefresh)
        refreshLayout.layoutParams(ItemSize.Match, ItemSize.Match)
        val listView = RecyclerView(this)
        listView.layoutParams(ItemSize.Match, ItemSize.Match)
        refreshLayout.addView(listView)
        setContentView(refreshLayout)
        refreshLayout.blocks(content)
    }

    protected class SwipeRefresh(
        private var refreshLayout: SwipeRefreshLayout,
        private val onRefresh: (SwipeRefresh) -> Unit
    ) {

        private val refreshListener = SwipeRefreshLayout.OnRefreshListener { refresh() }

        init {
            refreshLayout.setOnRefreshListener(refreshListener)
        }

        private fun refresh() {
            onRefresh(this)
        }

        fun refreshEnd() {
            refreshLayout.isRefreshing = false
        }

    }

}