package com.lollipop.play.core.page

import android.os.Bundle
import android.view.View
import com.lollipop.play.core.databinding.ActivityAdjustNumberBinding
import com.lollipop.play.core.view.ThumbBezelView


abstract class AdjustNumberActivity : ThumbBezelActivity() {

    protected val binding by lazy {
        ActivityAdjustNumberBinding.inflate(layoutInflater)
    }

    protected var numberValue = 0

    protected open val maxNumber = 999
    protected open val minNumber = 0

    protected open val offsetStep = 1

    override fun getContentView(): View {
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateNumber(getDefaultNumber(), false)
        binding.hintView.text = hintText()
    }

    override fun onThumbMove(state: ThumbBezelView.ThumbState) {
        var from = numberValue
        when (state) {
            ThumbBezelView.ThumbState.NEXT -> {
                from += offsetStep
            }

            ThumbBezelView.ThumbState.PREVIOUS -> {
                from -= offsetStep
            }
        }
        updateNumber(from, true)
    }

    protected fun updateNumber(number: Int, isFromUser: Boolean) {
        numberValue = number
        val min = minNumber
        if (numberValue < min) {
            numberValue = min
        }
        val max = maxNumber
        if (numberValue > max) {
            numberValue = max
        }
        val builder = StringBuilder()
        val maxLength = numberLength(max)
        var numberLength = numberLength(numberValue)
        while (numberLength < maxLength) {
            builder.append("0")
            numberLength++
        }
        builder.append(numberValue)
        binding.numberView.text = builder.toString()
        onNumberChanged(number, isFromUser)
    }

    protected abstract fun hintText(): String

    protected abstract fun onNumberChanged(number: Int, isFromUser: Boolean)

    protected abstract fun getDefaultNumber(): Int

    private fun numberLength(target: Int): Int {
        var max = target
        var count = 0
        while (max > 0) {
            count++
            max /= 10
        }
        return count
    }

}