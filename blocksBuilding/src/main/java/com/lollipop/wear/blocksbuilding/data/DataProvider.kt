package com.lollipop.wear.blocksbuilding.data

interface DataProvider<T> {

    val value: T

    fun register(callback: UpdateCallback<T>)

    fun remember(callback: DataCallback<T>)

    fun unregister(callback: UpdateCallback<T>)

    fun unregister(callback: DataCallback<T>)

    fun interface UpdateCallback<T> {
        fun onUpdate(value: DataProvider<T>)
    }

    fun interface DataCallback<T> {
        fun onUpdate(value: T)
    }

}