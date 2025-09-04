package com.lollipop.wear.blocksbuilding

import android.view.View
import com.lollipop.wear.blocksbuilding.data.ListDataProvider

@BBDsl
interface BuilderScope {

    val blocksOwner: BlocksOwner

    fun item(content: IBlock.() -> View)

    fun <T : Any> items(
        items: List<T>,
        key: (T) -> Int = { 1 },
        createItem: (type: Int) -> RecyclerHolder<T>,
    ): BlockManager

    fun <T : Any> items(
        provider: ListDataProvider<T>,
        key: (T) -> Int = { 1 },
        createItem: (type: Int) -> RecyclerHolder<T>,
    ) {
        provider.register(
            items(
                items = provider.list,
                key = key,
                createItem = createItem
            )
        )
    }

}

interface RecyclerHolder<T : Any> {

    val position: Int

    val itemView: View

    fun onUpdate(data: T)

}

