package com.lollipop.wear.blocksbuilding.impl

import android.view.View
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.wear.blocksbuilding.BlockManager
import com.lollipop.wear.blocksbuilding.BlocksOwner
import com.lollipop.wear.blocksbuilding.BuilderScope
import com.lollipop.wear.blocksbuilding.IBlock
import com.lollipop.wear.blocksbuilding.RecyclerHolder

class RecyclerBuilderScopeImpl(override val blocksOwner: BlocksOwner) : BuilderScope {

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

    private fun <T : Any> optRecyclerAdapter(
        items: List<T>,
        typeProvider: (T) -> Int,
        createItem: (Int) -> RecyclerHolder<T>,
    ): BBRecyclerAdapter<T> {
        val newAdapter = BBRecyclerAdapter(
            items,
            typeProvider,
            createItem,
        )
        adapterList.add(newAdapter)
        currentAdapter = null
        return newAdapter
    }

    override fun item(content: IBlock.() -> View) {
        val recycleBlock = RecycleBlock()
        val view = content(recycleBlock)
        optStaticAdapter().add(recycleBlock.createHolder(view))
    }

    override fun <T : Any> items(
        items: List<T>,
        key: (T) -> Int,
        createItem: (Int) -> RecyclerHolder<T>
    ): BlockManager {
        return optRecyclerAdapter(items, key, createItem)
    }

}