package com.lollipop.wear.blocksbuilding.data

import kotlin.reflect.KProperty

fun <T> staticData(v: T) = StaticDataObserver(v)

fun <T> mutableData(v: T) = MutableDataObserver(v)

inline operator fun <reified T> DataProvider<T>.getValue(
    thisObj: Any?,
    property: KProperty<*>
): T {
    return value
}

inline operator fun <reified T> MutableDataObserver<T>.setValue(
    thisObj: Any?,
    property: KProperty<*>,
    newValue: T
) {
    value = newValue
}

class StaticDataObserver<T>(override val value: T) : BasicDataProvider<T>()

class MutableDataObserver<T>(v: T) : BasicDataProvider<T>() {

    var modeCount = 0
        private set

    override var value: T = v
        set(value) {
            modeCount++
            field = value
            notifyUpdate()
        }

}