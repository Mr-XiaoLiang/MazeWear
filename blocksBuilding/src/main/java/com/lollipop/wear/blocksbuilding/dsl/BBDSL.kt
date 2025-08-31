package com.lollipop.wear.blocksbuilding.dsl

import android.content.Context
import android.content.res.Resources

interface BBLog {

    operator fun invoke(message: String)

    operator fun invoke(message: String, throwable: Throwable)

}

private class BBLogWrapper(val logTag: String) : BBLog {
    override fun invoke(message: String) {
        BBDSL.logImpl?.invoke("${logTag}: $message")
    }

    override fun invoke(message: String, throwable: Throwable) {
        BBDSL.logImpl?.invoke("${logTag}: $message", throwable)
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


inline fun <reified T : Any> T.bbLog(): BBLog {
    return logWith("${T::class.simpleName}@${System.identityHashCode(this)}")
}

fun logWith(tag: String): BBLog {
    return BBLogWrapper(tag)
}


