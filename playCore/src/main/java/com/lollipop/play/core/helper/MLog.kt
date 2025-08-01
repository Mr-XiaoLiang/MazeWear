package com.lollipop.play.core.helper

import java.io.ByteArrayOutputStream
import java.io.PrintWriter

class MLog(
    val logTag: String,
    val defaultLevel: Level = Level.Debug
) {

    companion object {

        var printer: LogPrinter = AndroidLogPrinter()

        fun with(any: Any, defaultLevel: Level = Level.Debug): MLog {
            val className = any.javaClass.simpleName
            val hashCode = System.identityHashCode(any)
            return MLog(logTag = "${className}@${hashCode}", defaultLevel)
        }
    }

    operator fun invoke(message: String) {
        log(defaultLevel, message)
    }

    fun d(message: String) {
        log(Level.Debug, message)
    }

    fun i(message: String) {
        log(Level.Info, message)
    }

    fun w(message: String) {
        log(Level.Warn, message)
    }

    fun e(message: String) {
        log(Level.Error, message)
    }

    fun e(message: String, throwable: Throwable) {
        val output = ByteArrayOutputStream()
        val print = PrintWriter(output)
        print.print(logTag)
        print.print(": ")
        print.println(message)
        throwable.printStackTrace(print)
        print.flush()
        val error = output.toString()
        logPrint(Level.Error, error)
    }

    private fun log(level: Level, message: String) {
        logPrint(level, "${logTag}: $message")
    }

    private fun logPrint(level: Level, message: String) {
        printer.log(level, "LollipopMaze", message)
    }

    enum class Level {
        Debug,
        Info,
        Warn,
        Error,
    }

    interface LogPrinter {
        fun log(level: Level, tag: String, message: String)
    }

    class AndroidLogPrinter : LogPrinter {
        override fun log(level: Level, tag: String, message: String) {
            when (level) {
                Level.Debug -> android.util.Log.d(tag, message)
                Level.Info -> android.util.Log.i(tag, message)
                Level.Warn -> android.util.Log.w(tag, message)
                Level.Error -> android.util.Log.e(tag, message)
            }
        }
    }

    class SystemOutLogPrinter : LogPrinter {
        override fun log(level: Level, tag: String, message: String) {
            when (level) {
                Level.Debug -> println("$tag [DEBUG] $message")
                Level.Info -> println("$tag [INFO] $message")
                Level.Warn -> println("$tag [WARN] $message")
                Level.Error -> println("$tag [ERROR] $message")
            }
        }
    }

    class EmptyLogPrinter : LogPrinter {
        override fun log(level: Level, tag: String, message: String) {
        }
    }

}

inline fun <reified T : Any> T.registerLog(defaultLevel: MLog.Level = MLog.Level.Debug): MLog {
    return MLog.with(this, defaultLevel)
}
