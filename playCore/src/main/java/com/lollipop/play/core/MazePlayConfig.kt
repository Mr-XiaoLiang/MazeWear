package com.lollipop.play.core

import android.content.Context
import com.lollipop.maze.helper.doAsync
import com.lollipop.play.core.helper.registerLog
import org.json.JSONObject
import java.io.File
import kotlin.math.min
import kotlin.reflect.KProperty

object MazePlayConfig {

    private var moveAnimationDurationValue by LongValue(200L)
    private var moveJoystickDurationValue by LongValue(200L)

    fun setMoveAnimationDuration(duration: Long) {
        moveAnimationDurationValue = duration
    }

    fun setMoveJoystickDuration(duration: Long) {
        moveJoystickDurationValue = duration
    }

    val moveAnimationDuration: Long
        get() {
            return min(moveAnimationDurationValue, moveJoystickDurationValue)
        }

    val moveJoystickDuration: Long
        get() {
            return moveJoystickDurationValue
        }

    fun init(context: Context, async: Boolean) {
        ConfigJson.load(context, async)
    }

    private object ConfigJson {

        private const val KEY_CONFIG_FILE = "maze_play_config.json"

        private val log by lazy {
            registerLog()
        }

        private var jsonObject = JSONObject()

        private var configFile: File? = null

        var isAutoSave = true

        private fun createConfigFile(context: Context): File {
            val file = File(context.filesDir, KEY_CONFIG_FILE)
            configFile = file
            return file
        }

        fun load(context: Context, async: Boolean) {
            if (async) {
                doAsync {
                    loadConfig(context)
                }
            } else {
                loadConfig(context)
            }
        }

        private fun loadConfig(context: Context) {
            log.tryDo("load config") {
                val file = createConfigFile(context)
                if (file.exists()) {
                    val json = file.readText()
                    jsonObject = JSONObject(json)
                }
            }
        }

        fun save(context: Context) {
            log.tryDo("save config") {
                save(createConfigFile(context))
            }
        }

        private fun autoSave() {
            configFile?.let { save(it) }
        }

        private fun save(file: File) {
            doAsync {
                log.tryDo("save config") {
                    file.writeText(jsonObject.toString())
                }
            }
        }

        fun opt(key: String, def: String = ""): String {
            return jsonObject.optString(key, def)
        }

        fun opt(key: String, def: Int = 0): Int {
            return jsonObject.optInt(key, def)
        }

        fun opt(key: String, def: Long = 0L): Long {
            return jsonObject.optLong(key, def)
        }

        fun opt(key: String, def: Double = 0.0): Double {
            return jsonObject.optDouble(key, def)
        }

        fun opt(key: String, def: Boolean = false): Boolean {
            return jsonObject.optBoolean(key, def)
        }

        fun put(key: String, value: Any) {
            jsonObject.put(key, value)
            if (isAutoSave) {
                autoSave()
            }
        }

    }

    private class StringValue(val def: String) {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
            return ConfigJson.opt(property.name, def)
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
            ConfigJson.put(property.name, value)
        }
    }

    private class LongValue(val def: Long) {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Long {
            return ConfigJson.opt(property.name, def)
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Long) {
            ConfigJson.put(property.name, value)
        }
    }

    private class BooleanValue(val def: Boolean) {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
            return ConfigJson.opt(property.name, def)
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
            ConfigJson.put(property.name, value)
        }
    }

    private class DoubleValue(val def: Double) {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Double {
            return ConfigJson.opt(property.name, def)
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Double) {
            ConfigJson.put(property.name, value)
        }
    }

}