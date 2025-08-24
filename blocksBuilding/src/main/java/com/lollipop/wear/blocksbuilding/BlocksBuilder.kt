package com.lollipop.wear.blocksbuilding

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.wear.blocksbuilding.impl.RecyclerBuilderScopeImpl
import com.lollipop.wear.blocksbuilding.impl.StaticBuilderScopeImpl

fun BlocksOwner.withBlocks(recyclerView: RecyclerView, content: BuilderScope.() -> Unit) {
    val scopeImpl = RecyclerBuilderScopeImpl(this)
    scopeImpl.content()
    recyclerView.adapter = scopeImpl.build()
}

fun BlocksOwner.withBlocks(viewGroup: ViewGroup, content: BuilderScope.() -> Unit) {
    val scopeImpl = StaticBuilderScopeImpl(this)
    scopeImpl.content()
    scopeImpl.build(viewGroup)
}
