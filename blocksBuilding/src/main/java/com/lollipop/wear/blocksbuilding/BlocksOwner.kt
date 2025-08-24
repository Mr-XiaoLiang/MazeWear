package com.lollipop.wear.blocksbuilding

import android.content.Context
import androidx.lifecycle.LifecycleOwner

interface BlocksOwner {

    val context: Context

    val lifecycleOwner: LifecycleOwner

}