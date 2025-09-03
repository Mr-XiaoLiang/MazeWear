package com.lollipop.wear.blocksbuilding.view

import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import com.lollipop.wear.blocksbuilding.BBDsl
import com.lollipop.wear.blocksbuilding.dsl.ViewLayoutParams
import com.lollipop.wear.blocksbuilding.dsl.convert


fun ItemGroupScope<*>.Constraint(
    layoutParams: ViewGroup.LayoutParams = ViewLayoutParams(),
    content: ConstraintScope.() -> Unit
): ConstraintScope {
    val view = add(ConstraintLayout(this.content.context), layoutParams)
    val scope = ConstraintBlockScope(view, lifecycleOwner)
    content.invoke(scope)
    return scope
}

@BBDsl
interface ConstraintScope : ItemGroupScope<ConstraintLayout>, MarginGroupScope {

    companion object {
        private fun control(
            childId: Int?,
            layoutParams: ViewGroup.LayoutParams,
        ): ConstraintBuilder {
            val layout = layoutParams.convert { ConstraintLayout.LayoutParams(it) }
            return ConstraintBuilder(selfId = childId, layout)
        }
    }

    fun ItemViewScope<*>.control(build: ConstraintBuilder.() -> Unit) {
        content.layoutParams = control(
            childId = viewId,
            layoutParams = content.layoutParams
        ).apply(build).complete()
    }

    fun ViewGroup.LayoutParams.width(percent: Float): ConstraintLayout.LayoutParams

    fun ViewGroup.LayoutParams.height(percent: Float): ConstraintLayout.LayoutParams

    fun ViewGroup.LayoutParams.ratio(width: Int, height: Int): ConstraintLayout.LayoutParams

    fun ViewGroup.LayoutParams.control(): ConstraintBuilder {
        return control(childId = null, layoutParams = this)
    }

}

class ConstraintBlockScope(
    frameLayout: ConstraintLayout, lifecycleOwner: LifecycleOwner
) : BasicItemGroupScope<ConstraintLayout>(frameLayout, lifecycleOwner), ConstraintScope {

    private fun ViewGroup.LayoutParams.convertLayout(): ConstraintLayout.LayoutParams {
        return convert {
            ConstraintLayout.LayoutParams(it)
        }
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
    private val selfId: Int?,
    private val layout: ConstraintLayout.LayoutParams
) {
    private fun getId(scope: ItemViewScope<*>): Int {
        val targetId = scope.viewId
        if (selfId != null && targetId == selfId) {
            throw IllegalArgumentException("Cannot constraint self")
        }
        return targetId
    }

    private fun getLayout(block: ConstraintLayout.LayoutParams.() -> Unit) {
        block.invoke(layout)
    }

    fun leftToLeftOf(target: ItemViewScope<*>): ConstraintBuilder {
        getLayout {
            leftToLeft = getId(target)
        }
        return this
    }

    fun leftToRightOf(target: ItemViewScope<*>): ConstraintBuilder {
        getLayout {
            leftToRight = getId(target)
        }
        return this
    }

    fun rightToLeftOf(target: ItemViewScope<*>): ConstraintBuilder {
        getLayout {
            rightToLeft = getId(target)
        }
        return this
    }

    fun rightToRightOf(target: ItemViewScope<*>): ConstraintBuilder {
        getLayout {
            rightToRight = getId(target)
        }
        return this
    }

    fun topToTopOf(target: ItemViewScope<*>): ConstraintBuilder {
        getLayout {
            topToTop = getId(target)
        }
        return this
    }

    fun topToBottomOf(target: ItemViewScope<*>): ConstraintBuilder {
        getLayout {
            topToBottom = getId(target)
        }
        return this
    }

    fun bottomToTopOf(target: ItemViewScope<*>): ConstraintBuilder {
        getLayout {
            bottomToTop = getId(target)
        }
        return this
    }

    fun bottomToBottomOf(target: ItemViewScope<*>): ConstraintBuilder {
        getLayout {
            bottomToBottom = getId(target)
        }
        return this
    }

    fun centerHorizontallyOf(target: ItemViewScope<*>): ConstraintBuilder {
        getLayout {
            val id = getId(target)
            startToStart = id
            endToEnd = id
        }
        return this
    }

    fun centerVerticallyOf(target: ItemViewScope<*>): ConstraintBuilder {
        getLayout {
            val id = getId(target)
            topToTop = id
            bottomToBottom = id
        }
        return this
    }

    fun centerOf(target: ItemViewScope<*>): ConstraintBuilder {
        getLayout {
            val id = getId(target)
            topToTop = id
            bottomToBottom = id
            startToStart = id
            endToEnd = id
        }
        return this
    }

    fun leftToParent(): ConstraintBuilder {
        getLayout {
            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        }
        return this
    }

    fun rightToParent(): ConstraintBuilder {
        getLayout {
            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        }
        return this
    }

    fun topToParent(): ConstraintBuilder {
        getLayout {
            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        }
        return this
    }

    fun bottomToParent(): ConstraintBuilder {
        getLayout {
            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        }
        return this
    }

    fun startToParent(): ConstraintBuilder {
        getLayout {
            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        }
        return this
    }

    fun endToParent(): ConstraintBuilder {
        getLayout {
            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        }
        return this
    }

    fun startToStartOf(target: ItemViewScope<*>): ConstraintBuilder {
        getLayout {
            startToStart = getId(target)
        }
        return this
    }

    fun startToEndOf(target: ItemViewScope<*>): ConstraintBuilder {
        getLayout {
            startToEnd = getId(target)
        }
        return this
    }

    fun endToStartOf(target: ItemViewScope<*>): ConstraintBuilder {
        getLayout {
            endToStart = getId(target)
        }
        return this
    }

    fun endToEndOf(target: ItemViewScope<*>): ConstraintBuilder {
        getLayout {
            endToEnd = getId(target)
        }
        return this
    }

    /**
     * @param target 圆心
     * @param radius 半径
     * @param anchor 角度
     */
    fun circle(target: ItemViewScope<*>, radius: Int, anchor: Float): ConstraintBuilder {
        getLayout {
            circleConstraint = getId(target)
            circleRadius = radius
            circleAngle = anchor
        }
        return this
    }

    fun complete(): ConstraintLayout.LayoutParams {
        return layout
    }

}
