package com.lollipop.wear.blocksbuilding.view

import android.graphics.Typeface
import android.util.TypedValue
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.FontRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LifecycleOwner
import com.lollipop.wear.blocksbuilding.BBDsl
import com.lollipop.wear.blocksbuilding.dsl.ViewLayoutParams
import com.lollipop.wear.blocksbuilding.item.MetricsValue
import com.lollipop.wear.blocksbuilding.item.ViewGravity
import com.lollipop.wear.blocksbuilding.item.sum


fun ItemGroupScope<*>.Text(
    layoutParams: ViewGroup.LayoutParams = ViewLayoutParams(),
    content: TextScope.() -> Unit
): TextScope {
    return TextBlockScope(
        add(
            AppCompatTextView(
                this.content.context
            ),
            layoutParams
        ), lifecycleOwner
    ).apply(content)
}

@BBDsl
interface TextScope : ItemViewScope<AppCompatTextView> {

    var text: CharSequence

    fun gravity(vararg gravity: ViewGravity)

    fun fontSize(size: MetricsValue)

    fun fontStyle(style: TextStyle)

    fun fontByAsset(assetName: String)

    fun fontByResource(@FontRes resId: Int)

    fun color(color: Int)

    fun colorRes(@ColorRes colorRes: Int)

}

class TextBlockScope(
    textView: AppCompatTextView, lifecycleOwner: LifecycleOwner
) : BasicItemViewScope<AppCompatTextView>(textView, lifecycleOwner), TextScope {

    override var text: CharSequence
        get() {
            return view.text
        }
        set(value) {
            view.text = value
        }

    override fun fontStyle(style: TextStyle) {
        view.setTypeface(Typeface.defaultFromStyle(style.style))
    }

    override fun gravity(vararg gravity: ViewGravity) {
        view.gravity = gravity.sum()
    }

    override fun fontSize(size: MetricsValue) {
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, size.px.toFloat())
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

