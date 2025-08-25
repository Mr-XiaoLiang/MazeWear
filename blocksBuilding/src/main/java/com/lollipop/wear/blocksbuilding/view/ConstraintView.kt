package com.lollipop.wear.blocksbuilding.view

import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import com.lollipop.wear.blocksbuilding.BBDsl
import com.lollipop.wear.blocksbuilding.BlocksOwner
import com.lollipop.wear.blocksbuilding.dsl.ViewLayoutParams
import com.lollipop.wear.blocksbuilding.dsl.convert


fun ItemGroupScope<*>.constraint(
    layoutParams: ViewGroup.LayoutParams = ViewLayoutParams(),
    content: ConstraintScope.() -> Unit
): ConstraintScope {
    val view = add(ConstraintLayout(this.content.context), layoutParams)
    val scope = ConstraintViewScope(view, lifecycleOwner)
    content.invoke(scope)
    return scope
}

@BBDsl
interface ConstraintScope : ItemGroupScope<ConstraintLayout> {

    fun ItemViewScope<*>.control(build: ConstraintBuilder.() -> Unit) {
        val layout = content.layoutParams.convert { ConstraintLayout.LayoutParams(it) }
        build(ConstraintBuilder(viewId, layout))
        content.layoutParams = layout
    }

    fun ViewGroup.LayoutParams.width(percent: Float): ConstraintLayout.LayoutParams

    fun ViewGroup.LayoutParams.height(percent: Float): ConstraintLayout.LayoutParams

    fun ViewGroup.LayoutParams.ratio(width: Int, height: Int): ConstraintLayout.LayoutParams

}

class ConstraintViewScope(
    frameLayout: ConstraintLayout, lifecycleOwner: LifecycleOwner
) : BasicItemGroupScope<ConstraintLayout>(frameLayout, lifecycleOwner), ConstraintScope {

    private fun ViewGroup.LayoutParams.convertLayout(): ConstraintLayout.LayoutParams {
        return convert { ConstraintLayout.LayoutParams(it) }
    }

    override fun ViewGroup.LayoutParams.width(percent: Float): ConstraintLayout.LayoutParams {
        return convertLayout().also {
            it.matchConstraintPercentWidth = percent
        }
    }

    override fun ViewGroup.LayoutParams.height(percent: Float): ConstraintLayout.LayoutParams {
        return convertLayout().also {
            it.matchConstraintPercentHeight = percent
        }
    }

    override fun ViewGroup.LayoutParams.ratio(
        width: Int,
        height: Int
    ): ConstraintLayout.LayoutParams {
        return convertLayout().also {
            it.dimensionRatio = "${width}:${height}"
        }
    }

}

class ConstraintBuilder(
    private val selfId: Int,
    private val layout: ConstraintLayout.LayoutParams
) {
    private fun getId(scope: ItemViewScope<*>): Int {
        val targetId = scope.viewId
        if (targetId == selfId) {
            throw IllegalArgumentException("Cannot constraint self")
        }
        return targetId
    }

    private fun getLayout(block: ConstraintLayout.LayoutParams.() -> Unit) {
        block.invoke(layout)
    }

    fun leftToLeftOf(target: ItemViewScope<*>) {
        getLayout {
            leftToLeft = getId(target)
        }
    }

    fun leftToRightOf(target: ItemViewScope<*>) {
        getLayout {
            leftToRight = getId(target)
        }
    }

    fun rightToLeftOf(target: ItemViewScope<*>) {
        getLayout {
            rightToLeft = getId(target)
        }
    }

    fun rightToRightOf(target: ItemViewScope<*>) {
        getLayout {
            rightToRight = getId(target)
        }
    }

    fun topToTopOf(target: ItemViewScope<*>) {
        getLayout {
            topToTop = getId(target)
        }
    }

    fun topToBottomOf(target: ItemViewScope<*>) {
        getLayout {
            topToBottom = getId(target)
        }
    }

    fun bottomToTopOf(target: ItemViewScope<*>) {
        getLayout {
            bottomToTop = getId(target)
        }
    }

    fun bottomToBottomOf(target: ItemViewScope<*>) {
        getLayout {
            bottomToBottom = getId(target)
        }
    }

    fun centerHorizontallyOf(target: ItemViewScope<*>) {
        getLayout {
            val id = getId(target)
            startToStart = id
            endToEnd = id
        }
    }

    fun centerVerticallyOf(target: ItemViewScope<*>) {
        getLayout {
            val id = getId(target)
            topToTop = id
            bottomToBottom = id
        }
    }

    fun centerOf(target: ItemViewScope<*>) {
        getLayout {
            val id = getId(target)
            topToTop = id
            bottomToBottom = id
            startToStart = id
            endToEnd = id
        }
    }

    fun centerInParent() {
        getLayout {
            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        }
    }

    fun startToStartOf(target: ItemViewScope<*>) {
        getLayout {
            startToStart = getId(target)
        }
    }

    fun startToEndOf(target: ItemViewScope<*>) {
        getLayout {
            startToEnd = getId(target)
        }
    }

    fun endToStartOf(target: ItemViewScope<*>) {
        getLayout {
            endToStart = getId(target)
        }
    }

    fun endToEndOf(target: ItemViewScope<*>) {
        getLayout {
            endToEnd = getId(target)
        }
    }

    /**
     * @param target 圆心
     * @param radius 半径
     * @param anchor 角度
     */
    fun circle(target: ItemViewScope<*>, radius: Int, anchor: Float) {
        getLayout {
            circleConstraint = getId(target)
            circleRadius = radius
            circleAngle = anchor
        }
    }

}

fun BlocksOwner.test() {
    viewBlock {
        constraint {
            val boxScope = box {

            }
            val rowScope = row {

            }
            boxScope.control {
                leftToLeftOf(boxScope)
                rightToRightOf(boxScope)
                topToTopOf(boxScope)
            }
        }
    }
}
