package com.arrive.terminal.core.ui.extensions

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.PluralsRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.arrive.terminal.core.ui.model.ColorValue

fun Context.getQuantityString(@PluralsRes resId: Int, quantity: Int): String {
    return resources.getQuantityString(resId, quantity, quantity)
}

fun Context.getDrawableCompat(@DrawableRes res: Int, @ColorRes tintRes: Int? = null): Drawable? {
    val drawable = ContextCompat.getDrawable(this, res)
    return when (tintRes) {
        null -> drawable
        else -> drawable?.applyTint(getColor(tintRes))
    }
}

fun Context.getDrawableWithTint(@DrawableRes res: Int, @ColorInt color: Int): Drawable? {
    return getDrawableCompat(res)?.applyTint(color)
}

fun Drawable.applyTint(@ColorInt tintColor: Int): Drawable {
    val wrappedDrawable = DrawableCompat.wrap(this)
    DrawableCompat.setTint(wrappedDrawable, tintColor)
    return wrappedDrawable
}

fun Drawable.applyTint(context: Context, value: ColorValue): Drawable {
    val wrappedDrawable = DrawableCompat.wrap(this)
    when (value) {
        is ColorValue.DynamicColor,
        is ColorValue.ResourceColor -> DrawableCompat.setTint(wrappedDrawable, value.asIntColor(context))
        is ColorValue.ResourceStateColor -> {
            DrawableCompat.setTintList(wrappedDrawable, context.getColorStateList(value.colorId))
        }
    }
    return wrappedDrawable
}

fun TextView.applyTextColor(value: ColorValue) {
    when (value) {
        is ColorValue.DynamicColor -> setTextColor(value.color)
        is ColorValue.ResourceColor -> setTextColor(context.getColor(value.colorId))
        is ColorValue.ResourceStateColor -> setTextColor(context.getColorStateList(value.colorId))
    }
}