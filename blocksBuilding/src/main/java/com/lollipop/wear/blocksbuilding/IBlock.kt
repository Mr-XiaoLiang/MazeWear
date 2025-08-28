package com.lollipop.wear.blocksbuilding

import android.view.View
import com.lollipop.wear.blocksbuilding.data.DataProvider

interface IBlock {

    fun notifyUpdate()

    fun onUpdate(updateCallback: (View) -> Unit)

    fun <T> remember(block: () -> DataProvider<T>): DataProvider<T> {
        val observer = block()
        observer.register {
            notifyUpdate()
        }
        return observer
    }

}