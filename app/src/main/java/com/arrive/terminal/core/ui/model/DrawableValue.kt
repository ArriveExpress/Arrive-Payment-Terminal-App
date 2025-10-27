package com.arrive.terminal.core.ui.model;

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import com.arrive.terminal.core.ui.extensions.getDrawableCompat

sealed class DrawableValue : Parcelable {

    @Parcelize
    data class DynamicDrawable(
        val drawable: @RawValue Drawable
    ) : DrawableValue(), Parcelable

    @Parcelize
    data class DrawableResources(
        @DrawableRes val res: Int,
        @ColorRes val tintRes: Int? = null
    ) : DrawableValue(), Parcelable

    fun asDrawable(context: Context) = when (this) {
        is DynamicDrawable -> drawable
        is DrawableResources -> try {
            context.getDrawableCompat(res, tintRes)
        } catch (exception: Exception) {
            null
        }
    }

    companion object {

        val Int.asDrawableValue get() = DrawableResources(this)

        val Drawable.asDrawableValue get() = DynamicDrawable(this)
    }
}