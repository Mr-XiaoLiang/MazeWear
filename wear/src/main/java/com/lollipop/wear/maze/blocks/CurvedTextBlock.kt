package com.lollipop.wear.maze.blocks

import android.graphics.Typeface
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.FontRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LifecycleOwner
import androidx.wear.widget.CurvedTextView
import com.lollipop.wear.blocksbuilding.BBDsl
import com.lollipop.wear.blocksbuilding.dsl.ViewLayoutParams
import com.lollipop.wear.blocksbuilding.item.MetricsValue
import com.lollipop.wear.blocksbuilding.view.BasicItemViewScope
import com.lollipop.wear.blocksbuilding.view.ItemGroupScope
import com.lollipop.wear.blocksbuilding.view.ItemViewScope
import com.lollipop.wear.blocksbuilding.view.TextStyle

fun ItemGroupScope<*>.CurvedText(
    layoutParams: ViewGroup.LayoutParams = ViewLayoutParams(),
    content: CurvedTextScope.() -> Unit
): CurvedTextScope {
    return CurvedTextBlockScope(
        add(
            CurvedTextView(
                this.content.context
            ),
            layoutParams
        ), lifecycleOwner
    ).apply(content)
}

@BBDsl
interface CurvedTextScope : ItemViewScope<CurvedTextView> {

    var text: String

    var isClockwise: Boolean

    var angle: Float

    fun fontStyle(style: TextStyle)

    fun fontSize(size: MetricsValue)

    fun fontByAsset(assetName: String)

    fun fontByResource(@FontRes resId: Int)

    fun color(color: Int)

    fun colorRes(@ColorRes colorRes: Int)

}

class CurvedTextBlockScope(
    textView: CurvedTextView, lifecycleOwner: LifecycleOwner
) : BasicItemViewScope<CurvedTextView>(textView, lifecycleOwner), CurvedTextScope {

    override var text: String
        get() {
            return view.text ?: ""
        }
        set(value) {
            view.text = value
        }
    override var isClockwise: Boolean
        get() {
            return view.isClockwise
        }
        set(value) {
            view.isClockwise = value
        }
    override var angle: Float
        get() {
            return view.anchorAngleDegrees
        }
        set(value) {
            view.anchorAngleDegrees = value
        }

    override fun fontStyle(style: TextStyle) {
        view.setTypeface(Typeface.defaultFromStyle(style.style))
    }

    override fun fontSize(size: MetricsValue) {
        view.setTextSize(size.px.toFloat())
    }

    override fun fontByAsset(assetName: String) {
        view.setTypeface(Typeface.createFromAsset(context.assets, assetName))
    }

    override fun fontByResource(resId: Int) {
        view.setTypeface(ResourcesCompat.getFont(context, resId))
    }

    override fun color(color: Int) {
        view.setTextColor(color)
    }

    override fun colorRes(colorRes: Int) {
        view.setTextColor(ContextCompat.getColor(context, colorRes))
    }

}

