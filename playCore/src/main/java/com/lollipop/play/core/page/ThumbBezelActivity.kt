package com.lollipop.play.core.page

import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.MotionEvent
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.lollipop.play.core.databinding.ActivityThumbBezelBinding
import com.lollipop.play.core.helper.registerLog
import com.lollipop.play.core.view.ThumbBezelView
import com.lollipop.play.core.view.ThumbBezelView.ThumbState


abstract class ThumbBezelActivity : AppCompatActivity() {

    protected val bezelBinding by lazy {
        ActivityThumbBezelBinding.inflate(layoutInflater)
    }

    protected abstract fun getContentView(): View

    protected abstract fun onThumbMove(state: ThumbState)

    private val log = registerLog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(bezelBinding.root)
        bezelBinding.contentGroup.addView(getContentView())
        bezelBinding.thumbBezelView.setThumbCallback(::onThumbMoveCallback)
    }

    override fun onGenericMotionEvent(event: MotionEvent?): Boolean {
        event ?: return super.onGenericMotionEvent(event)
        if (bezelBinding.thumbBezelView.onGenericMotionEvent(event)) {
            return true
        }
        return super.onGenericMotionEvent(event)
    }

    protected fun buildBezel(block: (ThumbBezelView) -> Unit) {
        block(bezelBinding.thumbBezelView)
    }

    private fun onThumbMoveCallback(state: ThumbState) {
        onThumbMove(state)
        vibrator()
    }

    private fun vibrator() {
        val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                getSystemService(VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as? Vibrator
        }
        vibrator ?: return
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