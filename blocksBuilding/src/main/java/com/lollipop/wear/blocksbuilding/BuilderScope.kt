package com.lollipop.wear.blocksbuilding

import android.view.View

@BBDsl
interface BuilderScope {

    val blocksOwner: BlocksOwner

    fun item(content: IBlock.() -> View)

    fun <T : Any> items(
        items: List<T>,
        key: (T) -> Int = { 1 },
        createItem: (type: Int) -> RecyclerHolder<T>,
    ): BlockManager

}

interface RecyclerHolder<T : Any> {

    val itemView: View

    fun onUpdate(data: T)

}

