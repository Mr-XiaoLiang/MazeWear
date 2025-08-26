package com.lollipop.wear.blocksbuilding.data

class DataObserver<T>(v: T) : DataProvider<T> {

    private val updateCallbacks = ArrayList<UpdateCallback<T>>()
    private val dataCallbacks = ArrayList<DataCallback<T>>()

    var modeCount = 0
        private set

    override var value: T = v
        set(value) {
            modeCount++
            field = value
            notifyUpdate()
        }

    private fun notifyUpdate() {
        updateCallbacks.forEach {
            it.onUpdate(this)
        }
        val v = value
        dataCallbacks.forEach {
            it.onUpdate(v)
        }
    }

    fun register(callback: UpdateCallback<T>) {
        updateCallbacks.add(callback)
    }

    fun remember(callback: DataCallback<T>) {
        dataCallbacks.add(callback)
    }

    fun unregister(callback: UpdateCallback<T>) {
        updateCallbacks.remove(callback)
    }

    fun unregister(callback: DataCallback<T>) {
        dataCallbacks.remove(callback)
    }

    fun interface UpdateCallback<T> {
        fun onUpdate(value: DataObserver<T>)
    }

    fun interface DataCallback<T> {
        fun onUpdate(value: T)
    }

}