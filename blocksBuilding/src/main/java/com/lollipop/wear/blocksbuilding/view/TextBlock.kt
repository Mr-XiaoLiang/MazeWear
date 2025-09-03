package com.lollipop.wear.blocksbuilding.view

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.annotation.StringRes
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

    fun text(@StringRes resId: Int)

    fun gravity(vararg gravity: ViewGravity)

    fun fontSize(size: MetricsValue)

    fun fontStyle(style: TextStyle)

    fun fontByAsset(assetName: String)

    fun fontByResource(@FontRes resId: Int)

    fun color(@ColorInt color: Int)

    fun colorRes(@ColorRes colorRes: Int)

    fun drawableStart(drawable: Drawable?)
    fun drawableEnd(drawable: Drawable?)
    fun drawableTop(drawable: Drawable?)
    fun drawableBottom(drawable: Drawable?)

    fun drawableStart(@DrawableRes resId: Int)
    fun drawableEnd(@DrawableRes resId: Int)
    fun drawableTop(@DrawableRes resId: Int)
    fun drawableBottom(@DrawableRes resId: Int)


    fun drawableTint(color: ColorStateList)
    fun drawableTint(@ColorInt color: Int)
    fun drawableTintRes(@ColorRes colorRes: Int)

}

class TextBlockScope(
    textView: AppCompatTextView, lifecycleOwner: LifecycleOwner
) : BasicItemViewScope<AppCompatTextView>(textView, lifecycleOwner), TextScope {

    private var drawableStart: Drawable? = null
    private var drawableEnd: Drawable? = null
    private var drawableTop: Drawable? = null
    private var drawableBottom: Drawable? = null

    override var text: CharSequence
        get() {
            return view.text
        }
        set(value) {
            view.text = value
        }

    override fun text(resId: Int) {
        text = context.getString(resId)
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

    private fun updateDrawable() {
        view.setCompoundDrawablesWithIntrinsicBounds(
            drawableStart, drawableTop, drawableEnd, drawableBottom
        )
    }

    override fun drawableStart(drawable: Drawable?) {
        drawableStart = drawable
        updateDrawable()
    }

    override fun drawableEnd(drawable: Drawable?) {
        drawableEnd = drawable
        updateDrawable()
    }

    override fun drawableTop(drawable: Drawable?) {
        drawableTop = drawable
        updateDrawable()
    }

    override fun drawableBottom(drawable: Drawable?) {
        drawableBottom = drawable
        updateDrawable()
    }

    override fun drawableStart(resId: Int) {
        drawableStart(ContextCompat.getDrawable(context, resId))
    }

    override fun drawableEnd(resId: Int) {
        drawableEnd(ContextCompat.getDrawable(context, resId))
    }

    override fun drawableTop(resId: Int) {
        drawableTop(ContextCompat.getDrawable(context, resId))
    }

    override fun drawableBottom(resId: Int) {
        drawableBottom(ContextCompat.getDrawable(context, resId))
    }

    @SuppressLint("UseCompatTextViewDrawableApis")
    override fun drawableTint(color: ColorStateList) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.compoundDrawableTintList = color
        }
    }

    override fun drawableTint(color: Int) {
        drawableTint(ColorStateList.valueOf(color))
    }

    override fun drawableTintRes(colorRes: Int) {
        drawableTint(
            ColorStateList.valueOf(
                ContextCompat.getColor(context, colorRes)
            )
        )
    }

}

