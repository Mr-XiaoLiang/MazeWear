package com.lollipop.play.core.controller

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TimeDelegate(
    private val callback: (String) -> Unit
) {

    companion object {
        fun auto(owner: LifecycleOwner, callback: (String) -> Unit): TimeDelegate.Auto {
            val observer = Auto(TimeDelegate(callback))
            owner.lifecycle.addObserver(observer)
            return observer
        }
    }

    private var isActive = false

    private val timeHandler = Handler(Looper.getMainLooper())

    private val timeRunnable = Runnable { updateTime() }

    private val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    fun onResume() {
        isActive = true
        updateTime()
    }

    fun onPause() {
        isActive = false
        timeHandler.removeCallbacks(timeRunnable)
    }

    private fun updateTime() {
        if (!isActive) {
            return
        }
        val timeMillis = System.currentTimeMillis()
        val date = dateFormat.format(Date(timeMillis))
        callback.invoke(date)
        postNext()
    }

    private fun postNext() {
        timeHandler.removeCallbacks(timeRunnable)
        timeHandler.postDelayed(timeRunnable, 1000)
    }

    class Auto(
        val delegate: TimeDelegate
    ) : LifecycleEventObserver {
        override fun onStateChanged(
            source: LifecycleOwner,
            event: Lifecycle.Event
        ) {
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    delegate.onResume()
                }

                Lifecycle.Event.ON_PAUSE -> {
                    delegate.onPause()
                }

                else -> {}
            }
        }
    }

}