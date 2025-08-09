package com.lollipop.play.core.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.reflect.KProperty

class PreferencesHelper(
    private val preferences: SharedPreferences
) {

    companion object {

        const val MAZE_WIDTH_MIN = 11
        const val MAZE_WIDTH_MAX = 999

        private const val KEY_PREFERENCES = "Maze"

        fun with(context: Context): Lazy<PreferencesHelper> = lazy {
            create(context)
        }

        fun create(context: Context): PreferencesHelper {
            return PreferencesHelper(
                context.applicationContext.getSharedPreferences(
                    KEY_PREFERENCES,
                    Context.MODE_PRIVATE
                )
            )
        }

    }

    var mazeWidth by intValue(def = MAZE_WIDTH_MIN, min = MAZE_WIDTH_MIN, max = MAZE_WIDTH_MAX)

    private fun stringValue(def: String = "") = StringDelegate(def)
    private fun intValue(
        def: Int = 0, min: Int? = null, max: Int? = null
    ) = IntDelegate(def, min, max)

    private fun booleanValue(def: Boolean = false) = BooleanDelegate(def)
    private fun floatValue(def: Float = 0F) = FloatDelegate(def)
    private fun longValue(def: Long = 0L) = LongDelegate(def)
    private fun doubleValue(def: Double = 0.0) = DoubleDelegate(def)

    private class IntDelegate(private val def: Int, private val min: Int?, private val max: Int?) {

        operator fun getValue(helper: PreferencesHelper, property: KProperty<*>): Int {
            val int = helper.preferences.getInt(property.name, def)
            if (min != null) {
                return if (int < min) {
                    min
                } else {
                    int
                }
            }
            if (max != null) {
                return if (int > max) {
                    max
                } else {
                    int
                }
            }
            return int
        }

        operator fun setValue(helper: PreferencesHelper, property: KProperty<*>, value: Int) {
            helper.preferences.edit { putInt(property.name, value) }
        }

    }

    private class BooleanDelegate(private val def: Boolean) {

        operator fun getValue(helper: PreferencesHelper, property: KProperty<*>): Boolean {
            return helper.preferences.getBoolean(property.name, def)
        }

        operator fun setValue(helper: PreferencesHelper, property: KProperty<*>, value: Boolean) {
            helper.preferences.edit { putBoolean(property.name, value) }
        }

    }

    private class FloatDelegate(private val def: Float) {

        operator fun getValue(helper: PreferencesHelper, property: KProperty<*>): Float {
            return helper.preferences.getFloat(property.name, def)
        }

        operator fun setValue(helper: PreferencesHelper, property: KProperty<*>, value: Float) {
            helper.preferences.edit { putFloat(property.name, value) }
        }

    }

    private class LongDelegate(private val def: Long) {

        operator fun getValue(helper: PreferencesHelper, property: KProperty<*>): Long {
            return helper.preferences.getLong(property.name, def)
        }

        operator fun setValue(helper: PreferencesHelper, property: KProperty<*>, value: Long) {
            helper.preferences.edit { putLong(property.name, value) }
        }

    }

    private class DoubleDelegate(private val def: Double) {

        operator fun getValue(helper: PreferencesHelper, property: KProperty<*>): Double {
            return helper.preferences.getFloat(property.name, def.toFloat()).toDouble()
        }

        operator fun setValue(helper: PreferencesHelper, property: KProperty<*>, value: Double) {
            helper.preferences.edit { putFloat(property.name, value.toFloat()) }
        }

    }

    private class StringDelegate(private val def: String) {

        operator fun getValue(helper: PreferencesHelper, property: KProperty<*>): String {
            return helper.preferences.getString(property.name, def) ?: def
        }

        operator fun setValue(helper: PreferencesHelper, property: KProperty<*>, value: String) {
            helper.preferences.edit { putString(property.name, value) }
        }

    }

}

fun Context.mazeSettings(): Lazy<PreferencesHelper> {
    return PreferencesHelper.with(this)
}
