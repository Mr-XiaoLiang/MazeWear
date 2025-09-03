package com.lollipop.wear.blocksbuilding.dsl

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lollipop.wear.blocksbuilding.BlocksOwner
import com.lollipop.wear.blocksbuilding.BuilderScope
import com.lollipop.wear.blocksbuilding.item.ItemSize
import com.lollipop.wear.blocksbuilding.withBlocks

class ActivityBlocksOwner(
    val activity: ComponentActivity
) : BlocksOwner {
    override val context: Context = activity
    override val lifecycleOwner: LifecycleOwner = activity
}

fun ComponentActivity.content(
    rooView: (RecyclerView) -> Unit = {},
    content: BuilderScope.() -> Unit
) {
    setContentView(blocksView(rooView, content))
}

fun ComponentActivity.swipeRefreshContent(
    rooView: (SwipeRefreshLayout) -> Unit = {},
    onRefresh: (SwipeRefresh) -> Unit,
    content: BuilderScope.() -> Unit
) {
    setContentView(blocksViewBySwipeRefresh(rooView, onRefresh, content))
}

fun ComponentActivity.staticContent(
    rooView: (NestedScrollView) -> Unit = {},
    content: BuilderScope.() -> Unit
) {
    setContentView(blocksStaticView(rooView, content))
}

fun ComponentActivity.staticSwipeRefreshContent(
    rooView: (SwipeRefreshLayout) -> Unit = {},
    onRefresh: (SwipeRefresh) -> Unit,
    content: BuilderScope.() -> Unit
) {
    setContentView(blocksViewBySwipeRefresh(rooView, onRefresh, content))
}

fun ComponentActivity.blocksView(
    rooView: (RecyclerView) -> Unit = {},
    content: BuilderScope.() -> Unit
): View {
    val blocksOwner = ActivityBlocksOwner(this)
    return RecyclerView(this).also { recyclerView ->
        recyclerView.layoutParams(ItemSize.Match, ItemSize.Match)
        recyclerView.layoutManager = LinearLayoutManager(
            this, RecyclerView.VERTICAL, false
        )
        rooView(recyclerView)
        blocksOwner.withBlocks(recyclerView = recyclerView, content = content)
    }
}

fun ComponentActivity.blocksViewBySwipeRefresh(
    rooView: (SwipeRefreshLayout) -> Unit = {},
    onRefresh: (SwipeRefresh) -> Unit,
    content: BuilderScope.() -> Unit
): View {
    val blocksOwner = ActivityBlocksOwner(this)
    return SwipeRefreshLayout(this).also { refreshLayout ->
        SwipeRefresh(refreshLayout, onRefresh)
        refreshLayout.layoutParams(ItemSize.Match, ItemSize.Match)
        rooView(refreshLayout)
        refreshLayout.addView(
            RecyclerView(this).also { recyclerView ->
                recyclerView.layoutParams(ItemSize.Match, ItemSize.Match)
                recyclerView.layoutManager = LinearLayoutManager(
                    this, LinearLayoutManager.VERTICAL, false
                )
                blocksOwner.withBlocks(recyclerView, content = content)
            }
        )
    }
}

fun ComponentActivity.blocksStaticView(
    rooView: (NestedScrollView) -> Unit = {},
    content: BuilderScope.() -> Unit
): View {
    val blocksOwner = ActivityBlocksOwner(this)
    return NestedScrollView(this).also { scrollView ->
        scrollView.layoutParams(ItemSize.Match, ItemSize.Match)
        rooView(scrollView)
        scrollView.addView(
            LinearLayout(this).also { contentView ->
                contentView.layoutParams(ItemSize.Match, ItemSize.Wrap)
                contentView.orientation = LinearLayout.VERTICAL
                blocksOwner.withBlocks(viewGroup = contentView, content = content)
            }
        )
    }
}

fun ComponentActivity.blocksStaticViewBySwipeRefresh(
    rooView: (SwipeRefreshLayout) -> Unit = {},
    onRefresh: (SwipeRefresh) -> Unit,
    content: BuilderScope.() -> Unit
): View {
    val blocksOwner = ActivityBlocksOwner(this)
    return SwipeRefreshLayout(this).also { refreshLayout ->
        SwipeRefresh(refreshLayout, onRefresh)
        refreshLayout.layoutParams(ItemSize.Match, ItemSize.Match)
        rooView(refreshLayout)
        refreshLayout.addView(
            NestedScrollView(this).also { scrollView ->
                scrollView.layoutParams(ItemSize.Match, ItemSize.Match)
                scrollView.addView(
                    LinearLayout(this).also { contentView ->
                        contentView.layoutParams(ItemSize.Match, ItemSize.Wrap)
                        contentView.orientation = LinearLayout.VERTICAL
                        blocksOwner.withBlocks(viewGroup = contentView, content = content)
                    }
                )
            }
        )
    }
}

class SwipeRefresh(
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