package com.lollipop.play.core.page

import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.lollipop.play.core.databinding.ActivityThumbBezelBinding
import com.lollipop.play.core.view.ThumbBezelView
import com.lollipop.play.core.view.ThumbBezelView.ThumbState


abstract class ThumbBezelActivity : AppCompatActivity() {

    private val bezelBinding by lazy {
        ActivityThumbBezelBinding.inflate(layoutInflater)
    }

    protected abstract fun getContentView(): View

    protected abstract fun onThumbMove(state: ThumbState)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(bezelBinding.root)
        bezelBinding.contentGroup.addView(getContentView())
        bezelBinding.thumbBezelView.setThumbCallback(::onThumbMoveCallback)
    }

    protected fun buildBezel(block: (ThumbBezelView) -> Unit) {
        block(bezelBinding.thumbBezelView)
    }

    private fun onThumbMoveCallback(state: ThumbState) {
        onThumbMove(state)
        vibrator()
    }

    private fun vibrator() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        val vibrate = 10L
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    vibrate,
                    100
                )
            )
        } else {
            // backward compatibility for Android API < 26
            // noinspection deprecation
            @Suppress("DEPRECATION")
            vibrator.vibrate(vibrate)
        }
    }

}