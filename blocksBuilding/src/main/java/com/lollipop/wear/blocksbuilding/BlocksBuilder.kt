package com.lollipop.wear.blocksbuilding

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.wear.blocksbuilding.impl.RecyclerBuilderScopeImpl
import com.lollipop.wear.blocksbuilding.impl.StaticBuilderScopeImpl

object BlocksBuilder {

    fun with(recyclerView: RecyclerView, content: BuilderScope.() -> Unit) {
        val scopeImpl = RecyclerBuilderScopeImpl()
        scopeImpl.content()
        recyclerView.adapter = scopeImpl.build()
    }

    fun with(viewGroup: ViewGroup, content: BuilderScope.() -> Unit) {
        val scopeImpl = StaticBuilderScopeImpl()
        scopeImpl.content()
        scopeImpl.build(viewGroup)
    }

}
