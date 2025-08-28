package com.lollipop.wear.blocksbuilding.view

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.lollipop.wear.blocksbuilding.BBDsl
import com.lollipop.wear.blocksbuilding.dsl.ViewLayoutParams


fun ItemGroupScope<*>.Image(
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
interface ImageScope : ItemViewScope<AppCompatImageView> {

    fun src(@DrawableRes resId: Int)

    fun src(bitmap: Bitmap)

    fun src(bitmap: Bitmap, scaleType: ImageView.ScaleType)

    fun src(drawable: Drawable)

    fun src(uri: Uri)

    fun tint(color: Int)

    fun tintRes(@ColorRes colorRes: Int)

    fun tintStateRes(@ColorRes colorRes: Int)

    fun tint(color: ColorStateList)

    // TODO 还有缩放模式，背景tint，剪裁圆角的一些其他东西
}

class ImageBlockScope(
    imageView: AppCompatImageView, lifecycleOwner: LifecycleOwner
) : BasicItemViewScope<AppCompatImageView>(imageView, lifecycleOwner), ImageScope {
    override fun src(resId: Int) {
        content.setImageResource(resId)
    }

    override fun src(bitmap: Bitmap) {
        content.setImageBitmap(bitmap)
    }

    override fun src(
        bitmap: Bitmap,
        scaleType: ImageView.ScaleType
    ) {
        content.setImageBitmap(bitmap)
    }

    override fun src(drawable: Drawable) {
        content.setImageDrawable(drawable)
    }

    override fun src(uri: Uri) {
        content.setImageURI(uri)
    }

    override fun tint(color: Int) {
        content.imageTintList = ColorStateList.valueOf(color)
    }

    override fun tintRes(colorRes: Int) {
        tint(ContextCompat.getColor(content.context, colorRes))
    }

    override fun tintStateRes(colorRes: Int) {
        tint(ContextCompat.getColorStateList(content.context, colorRes)!!)
    }

    override fun tint(color: ColorStateList) {
        content.imageTintList = color
    }

}

