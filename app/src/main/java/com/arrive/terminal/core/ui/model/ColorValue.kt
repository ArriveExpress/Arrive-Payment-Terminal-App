package com.arrive.terminal.core.ui.model;

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes

sealed interface ColorValue {

    data class DynamicColor(@ColorInt val color: Int) : ColorValue

    data class ResourceColor(@ColorRes val colorId: Int) : ColorValue

    data class ResourceStateColor(@ColorRes val colorId: Int) : ColorValue

    fun asIntColor(context: Context) = when (this) {
        is DynamicColor -> color
        is ResourceColor -> context.getColor(colorId)
        is ResourceStateColor -> context.getColor(colorId)
    }

    companion object {

        val Int.asIntColorValue get() = DynamicColor(this)

        val Int.asResourceColorValue get() = ResourceColor(this)

        val Int.asResourceStateColorValue get() = ResourceStateColor(this)
    }
}