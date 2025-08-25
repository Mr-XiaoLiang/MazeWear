package com.lollipop.wear.blocksbuilding

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.wear.blocksbuilding.dsl.BBDSL
import com.lollipop.wear.blocksbuilding.impl.RecyclerBuilderScopeImpl
import com.lollipop.wear.blocksbuilding.impl.StaticBuilderScopeImpl

fun BlocksOwner.withBlocks(recyclerView: RecyclerView, content: BuilderScope.() -> Unit) {
    BBDSL.bind(context.resources)
    val scopeImpl = RecyclerBuilderScopeImpl(this)
    scopeImpl.content()
    recyclerView.adapter = scopeImpl.build()
}

fun BlocksOwner.withBlocks(viewGroup: ViewGroup, content: BuilderScope.() -> Unit) {
    BBDSL.bind(context.resources)
    val scopeImpl = StaticBuilderScopeImpl(this)
    scopeImpl.content()
    scopeImpl.build(viewGroup)
}
