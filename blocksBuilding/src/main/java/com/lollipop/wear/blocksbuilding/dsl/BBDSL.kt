package com.lollipop.wear.blocksbuilding.dsl

import android.content.Context
import android.content.res.Resources
import com.lollipop.wear.blocksbuilding.data.DataObserver
import com.lollipop.wear.blocksbuilding.data.DataProvider
import kotlin.reflect.KProperty

interface BBLog {

    operator fun invoke(message: String)

    fun log(message: String, throwable: Throwable)

}

private class BBLogWrapper(val logTag: String) : BBLog {
    override fun invoke(message: String) {
        BBDSL.logImpl?.invoke("${logTag}: $message")
    }

    override fun log(message: String, throwable: Throwable) {
        BBDSL.logImpl?.log("${logTag}: $message", throwable)
    }
}

object BBDSL {

    lateinit var resources: Resources
        private set

    var logImpl: BBLog? = null

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

inline fun <reified T : Any> T.registerLog(): BBLog {
    return logWith("${T::class.simpleName}@${System.identityHashCode(this)}")
}

fun logWith(tag: String): BBLog {
    return BBLogWrapper(tag)
}


