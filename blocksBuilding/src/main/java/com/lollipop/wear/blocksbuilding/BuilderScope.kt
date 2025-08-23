package com.lollipop.wear.blocksbuilding

import android.view.View

@BBDsl
interface BuilderScope {

    fun item(content: () -> View)

    fun <T : Any> items(
        items: List<T>,
        key: (T) -> Int,
        createItem: (Int) -> View,
        update: (View, T) -> Unit
    ): BlockManager

}