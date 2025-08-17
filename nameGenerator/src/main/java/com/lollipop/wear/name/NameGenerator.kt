package com.lollipop.wear.name

import android.content.Context
import com.lollipop.wear.generator.R
import com.lollipop.wear.name.generator.ChineseGenerator
import com.lollipop.wear.name.generator.EnglishGenerator

object NameGenerator {

    var currentGenerator: Generator = ChineseGenerator

    fun init(context: Context) {
        val key = context.getString(R.string.name_generator).lowercase()
        currentGenerator = when (key) {
            "en" -> EnglishGenerator
            "zh" -> ChineseGenerator
            else -> ChineseGenerator
        }
    }

    fun randomAdjective(): String {
        return currentGenerator.randomAdjective()
    }

    fun randomNoun(): String {
        return currentGenerator.randomNoun()
    }

    fun randomName(): String {
        return "${randomAdjective()}${randomNoun()}"
    }

    interface Generator {

        fun randomAdjective(): String
        fun randomNoun(): String

    }

}