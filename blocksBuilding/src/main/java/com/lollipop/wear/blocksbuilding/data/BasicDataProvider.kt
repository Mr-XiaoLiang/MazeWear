package com.lollipop.wear.blocksbuilding.data


abstract class BasicDataProvider<T> : DataProvider<T> {
    protected val updateCallbacks = ArrayList<DataProvider.UpdateCallback<T>>()
    protected val dataCallbacks = ArrayList<DataProvider.DataCallback<T>>()

    protected fun notifyUpdate() {
        updateCallbacks.forEach {
            it.onUpdate(this)
        }
        val v = value
        dataCallbacks.forEach {
            it.onUpdate(v)
        }
    }

    override fun register(callback: DataProvider.UpdateCallback<T>) {
        updateCallbacks.add(callback)
        callback.onUpdate(this)
    }

    override fun remember(callback: DataProvider.DataCallback<T>) {
        dataCallbacks.add(callback)
        callback.onUpdate(value)
    }

    override fun unregister(callback: DataProvider.UpdateCallback<T>) {
        updateCallbacks.remove(callback)
    }

    override fun unregister(callback: DataProvider.DataCallback<T>) {
        dataCallbacks.remove(callback)
    }

}