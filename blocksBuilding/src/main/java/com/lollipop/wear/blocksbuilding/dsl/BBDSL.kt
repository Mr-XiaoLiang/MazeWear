package com.lollipop.wear.blocksbuilding.dsl

import com.lollipop.wear.blocksbuilding.data.DataObserver
import com.lollipop.wear.blocksbuilding.data.DataProvider
import kotlin.reflect.KProperty

fun <T> blockStateOf(value: T): DataObserver<T> {
    return DataObserver(value)
}

inline operator fun <reified T> DataProvider<T>.getValue(
    thisObj: Any?,
    property: KProperty<*>
): T {
    return value
}

inline operator fun <reified T> DataObserver<T>.setValue(
    thisObj: Any?,
    property: KProperty<*>,
    newValue: T
) {
    value = newValue
}


