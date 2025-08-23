package com.lollipop.wear.blocksbuilding.impl

import android.view.View
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.wear.blocksbuilding.BlockManager
import com.lollipop.wear.blocksbuilding.BuilderScope

class RecyclerBuilderScopeImpl : BuilderScope {

    private val adapterList = mutableListOf<RecyclerView.Adapter<*>>()

    private var currentAdapter: RecyclerView.Adapter<*>? = null

    fun build(): RecyclerView.Adapter<*> {
        return ConcatAdapter(adapterList)
    }

    private fun optStaticAdapter(): BBStaticAdapter {
        val adapter = currentAdapter
        if (adapter is BBStaticAdapter) {
            return adapter
        }
        val newAdapter = BBStaticAdapter()
        adapterList.add(newAdapter)
        currentAdapter = newAdapter
        return newAdapter
    }

    private fun <T> optRecyclerAdapter(
        items: List<T>,
        typeProvider: (T) -> Int,
        createItem: (Int) -> View,
        update: (View, T) -> Unit
    ): BBRecyclerAdapter<T> {
        val newAdapter = BBRecyclerAdapter<T>(
            items,
            typeProvider,
            createItem,
            update
        )
        adapterList.add(newAdapter)
        currentAdapter = null
        return newAdapter
    }

    override fun item(content: () -> View) {
        optStaticAdapter().add(content())
    }

    override fun <T : Any> items(
        items: List<T>,
        key: (T) -> Int,
        createItem: (Int) -> View,
        update: (View, T) -> Unit
    ): BlockManager {
        return optRecyclerAdapter(items, key, createItem, update)
    }


}