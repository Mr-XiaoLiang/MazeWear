package com.lollipop.wear.blocksbuilding.lifecycle

import android.content.Context
import androidx.startup.Initializer
import com.lollipop.wear.blocksbuilding.dsl.BBDSL

class BlocksInitializer : Initializer<BBDSL> {
    override fun create(context: Context): BBDSL {
        BBDSL.init(context)
        return BBDSL
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> {
        return emptyList()
    }

}