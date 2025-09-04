package com.lollipop.wear.blocksbuilding.dsl

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
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

class FragmentBlocksOwner(
    val ownerContext: Context?,
    val fragment: Fragment
) : BlocksOwner {
    override val context: Context = ownerContext ?: fragment.requireContext()
    override val lifecycleOwner: LifecycleOwner = fragment
}

fun ComponentActivity.createBlocksOwner(): ActivityBlocksOwner {
    return ActivityBlocksOwner(this)
}

fun Fragment.createBlocksOwner(context: Context? = null): FragmentBlocksOwner {
    return FragmentBlocksOwner(context ?: activity, this)
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
    return createBlocksOwner().blocksView(
        rooView = rooView,
        content = content
    )
}

fun ComponentActivity.blocksViewBySwipeRefresh(
    rooView: (SwipeRefreshLayout) -> Unit = {},
    onRefresh: (SwipeRefresh) -> Unit,
    content: BuilderScope.() -> Unit
): View {
    return createBlocksOwner().blocksViewBySwipeRefresh(
        rooView = rooView,
        onRefresh = onRefresh,
        content = content
    )
}

fun ComponentActivity.blocksStaticView(
    rooView: (NestedScrollView) -> Unit = {},
    content: BuilderScope.() -> Unit
): View {
    return createBlocksOwner().blocksStaticView(
        rooView = rooView,
        content = content
    )
}

fun ComponentActivity.blocksStaticViewBySwipeRefresh(
    rooView: (SwipeRefreshLayout) -> Unit = {},
    onRefresh: (SwipeRefresh) -> Unit,
    content: BuilderScope.() -> Unit
): View {
    return createBlocksOwner().blocksStaticViewBySwipeRefresh(
        rooView = rooView,
        onRefresh = onRefresh,
        content = content
    )
}

fun Fragment.blocksView(
    context: Context? = null,
    rooView: (RecyclerView) -> Unit = {},
    content: BuilderScope.() -> Unit
): View {
    return createBlocksOwner(context = context).blocksView(
        rooView = rooView,
        content = content
    )
}

fun Fragment.blocksViewBySwipeRefresh(
    context: Context? = null,
    rooView: (SwipeRefreshLayout) -> Unit = {},
    onRefresh: (SwipeRefresh) -> Unit,
    content: BuilderScope.() -> Unit
): View {
    return createBlocksOwner(context = context).blocksViewBySwipeRefresh(
        rooView = rooView,
        onRefresh = onRefresh,
        content = content
    )
}

fun Fragment.blocksStaticView(
    context: Context? = null,
    rooView: (NestedScrollView) -> Unit = {},
    content: BuilderScope.() -> Unit
): View {
    return createBlocksOwner(context = context).blocksStaticView(
        rooView = rooView,
        content = content
    )
}

fun Fragment.blocksStaticViewBySwipeRefresh(
    context: Context? = null,
    rooView: (SwipeRefreshLayout) -> Unit = {},
    onRefresh: (SwipeRefresh) -> Unit,
    content: BuilderScope.() -> Unit
): View {
    return createBlocksOwner(context = context).blocksStaticViewBySwipeRefresh(
        rooView = rooView,
        onRefresh = onRefresh,
        content = content
    )
}

fun BlocksOwner.blocksView(
    rooView: (RecyclerView) -> Unit = {},
    content: BuilderScope.() -> Unit
): View {
    return RecyclerView(context).also { recyclerView ->
        recyclerView.layoutParams(ItemSize.Match, ItemSize.Match)
        recyclerView.layoutManager = LinearLayoutManager(
            context, RecyclerView.VERTICAL, false
        )
        rooView(recyclerView)
        withBlocks(recyclerView = recyclerView, content = content)
    }
}

fun BlocksOwner.blocksViewBySwipeRefresh(
    rooView: (SwipeRefreshLayout) -> Unit = {},
    onRefresh: (SwipeRefresh) -> Unit,
    content: BuilderScope.() -> Unit
): View {
    return SwipeRefreshLayout(context).also { refreshLayout ->
        SwipeRefresh(refreshLayout, onRefresh)
        refreshLayout.layoutParams(ItemSize.Match, ItemSize.Match)
        rooView(refreshLayout)
        refreshLayout.addView(
            RecyclerView(context).also { recyclerView ->
                recyclerView.layoutParams(ItemSize.Match, ItemSize.Match)
                recyclerView.layoutManager = LinearLayoutManager(
                    context, LinearLayoutManager.VERTICAL, false
                )
                withBlocks(recyclerView, content = content)
            }
        )
    }
}

fun BlocksOwner.blocksStaticView(
    rooView: (NestedScrollView) -> Unit = {},
    content: BuilderScope.() -> Unit
): View {
    return NestedScrollView(context).also { scrollView ->
        scrollView.layoutParams(ItemSize.Match, ItemSize.Match)
        rooView(scrollView)
        scrollView.addView(
            LinearLayout(context).also { contentView ->
                contentView.layoutParams(ItemSize.Match, ItemSize.Wrap)
                contentView.orientation = LinearLayout.VERTICAL
                withBlocks(viewGroup = contentView, content = content)
            }
        )
    }
}

fun BlocksOwner.blocksStaticViewBySwipeRefresh(
    rooView: (SwipeRefreshLayout) -> Unit = {},
    onRefresh: (SwipeRefresh) -> Unit,
    content: BuilderScope.() -> Unit
): View {
    return SwipeRefreshLayout(context).also { refreshLayout ->
        SwipeRefresh(refreshLayout, onRefresh)
        refreshLayout.layoutParams(ItemSize.Match, ItemSize.Match)
        rooView(refreshLayout)
        refreshLayout.addView(
            NestedScrollView(context).also { scrollView ->
                scrollView.layoutParams(ItemSize.Match, ItemSize.Match)
                scrollView.addView(
                    LinearLayout(context).also { contentView ->
                        contentView.layoutParams(ItemSize.Match, ItemSize.Wrap)
                        contentView.orientation = LinearLayout.VERTICAL
                        withBlocks(viewGroup = contentView, content = content)
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