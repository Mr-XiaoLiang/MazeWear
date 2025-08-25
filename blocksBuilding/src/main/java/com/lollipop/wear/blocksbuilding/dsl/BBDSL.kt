package com.lollipop.wear.blocksbuilding.dsl

import android.content.Context
import android.content.res.Resources
import com.lollipop.wear.blocksbuilding.data.DataObserver
import com.lollipop.wear.blocksbuilding.data.DataProvider
import kotlin.reflect.KProperty

object BBDSL {

    lateinit var resources: Resources
        private set

    fun init(context: Context) {
        BBDSL.resources = context.resources
    }
}

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


