package com.lollipop.wear.blocksbuilding.item

import android.util.TypedValue
import com.lollipop.wear.blocksbuilding.dsl.BBDSL

val Int.DP: DP
    get() {
        return DP(this.toFloat())
    }

val Float.DP: DP
    get() {
        return DP(this)
    }

val Int.PX: PX
    get() {
        return PX(this)
    }

val Int.SP: SP
    get() {
        return SP(this.toFloat())
    }

val Float.SP: SP
    get() {
        return SP(this)
    }

interface MetricsValue {

    val px: Int

}

class DP(val value: Float) : MetricsValue {
    override val px: Int by lazy {
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value,
            BBDSL.resources.displayMetrics
        ).toInt()
    }
}

class PX(val value: Int) : MetricsValue {
    override val px: Int = value
}

class SP(val value: Float) : MetricsValue {
    override val px: Int by lazy {
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            value,
            BBDSL.resources.displayMetrics
        ).toInt()
    }
}
