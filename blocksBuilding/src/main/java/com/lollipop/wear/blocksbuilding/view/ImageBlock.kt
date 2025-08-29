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
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.lollipop.wear.blocksbuilding.BBDsl
import com.lollipop.wear.blocksbuilding.dsl.ViewLayoutParams


fun ItemGroupScope<*>.Image(
    layoutParams: ViewGroup.LayoutParams = ViewLayoutParams(),
    content: ImageScope.() -> Unit
): ImageScope {
    return ImageBlockScope(
        add(
            AppCompatImageView(
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

    var scaleType: ImageView.ScaleType

    fun backgroundTine(color: Int)

    fun backgroundTine(color: ColorStateList)

    fun backgroundTineRes(@ColorRes color: Int)

    fun backgroundTineStateRes(@ColorRes color: Int)

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
        tint(ColorStateList.valueOf(color))
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

    override var scaleType: ImageView.ScaleType
        get() {
            return content.scaleType
        }
        set(value) {
            content.scaleType = value
        }

    override fun backgroundTine(color: Int) {
        backgroundTine(ColorStateList.valueOf(color))
    }

    override fun backgroundTine(color: ColorStateList) {
        content.backgroundTintList = color
    }

    override fun backgroundTineRes(color: Int) {
        backgroundTine(ContextCompat.getColor(content.context, color))
    }

    override fun backgroundTineStateRes(color: Int) {
        backgroundTine(ContextCompat.getColorStateList(content.context, color)!!)
    }

}

