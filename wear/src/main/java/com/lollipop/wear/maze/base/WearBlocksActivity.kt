package com.lollipop.wear.maze.base

import android.content.Context
import android.graphics.Color
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lollipop.wear.blocksbuilding.BlocksOwner
import com.lollipop.wear.blocksbuilding.BuilderScope
import com.lollipop.wear.blocksbuilding.dsl.layoutParams
import com.lollipop.wear.blocksbuilding.item.ItemSize
import com.lollipop.wear.blocksbuilding.withBlocks

abstract class WearBlocksActivity() : AppCompatActivity(), BlocksOwner {

    override val context: Context = this

    override val lifecycleOwner: LifecycleOwner = this

    protected fun content(content: BuilderScope.() -> Unit) {
        setContentView(
            RecyclerView(this).also { recyclerView ->
                recyclerView.layoutParams(ItemSize.Match, ItemSize.Match)
                recyclerView.layoutManager = LinearLayoutManager(
                    this, LinearLayoutManager.VERTICAL, false
                )
                withBlocks(recyclerView = recyclerView, content = content)
            }
        )
    }

    protected fun swipeRefreshContent(
        onRefresh: (SwipeRefresh) -> Unit,
        content: BuilderScope.() -> Unit
    ) {
        setContentView(
            SwipeRefreshLayout(this).also { refreshLayout ->
                SwipeRefresh(refreshLayout, onRefresh)
                refreshLayout.layoutParams(ItemSize.Match, ItemSize.Match)
                refreshLayout.addView(
                    RecyclerView(this).also { recyclerView ->
                        recyclerView.layoutParams(ItemSize.Match, ItemSize.Match)
                        recyclerView.layoutManager = LinearLayoutManager(
                            this, LinearLayoutManager.VERTICAL, false
                        )
                        withBlocks(recyclerView, content = content)
                    }
                )
            }
        )
    }

    protected fun staticContent(content: BuilderScope.() -> Unit) {
        setContentView(
            NestedScrollView(this).also { scrollView ->
                scrollView.layoutParams(ItemSize.Match, ItemSize.Match)
                scrollView.addView(
                    LinearLayout(this).also { contentView ->
                        contentView.layoutParams(ItemSize.Match, ItemSize.Wrap)
                        contentView.orientation = LinearLayout.VERTICAL
                        withBlocks(viewGroup = contentView, content = content)
                    }
                )
            }
        )
    }

    protected fun staticSwipeRefreshContent(
        onRefresh: (SwipeRefresh) -> Unit,
        content: BuilderScope.() -> Unit
    ) {
        setContentView(
            SwipeRefreshLayout(this).also { refreshLayout ->
                SwipeRefresh(refreshLayout, onRefresh)
                refreshLayout.layoutParams(ItemSize.Match, ItemSize.Match)
                refreshLayout.addView(
                    NestedScrollView(this).also { scrollView ->
                        scrollView.layoutParams(ItemSize.Match, ItemSize.Match)
                        scrollView.addView(
                            LinearLayout(this).also { contentView ->
                                contentView.layoutParams(ItemSize.Match, ItemSize.Wrap)
                                contentView.orientation = LinearLayout.VERTICAL
                                withBlocks(viewGroup = contentView, content = content)
                            }
                        )
                    }
                )
            }
        )
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