package com.arrive.terminal.core.ui.extensions

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.view.isVisible

fun ImageView.drawableOrGone(drawable: Drawable?) {
    isVisible = drawable != null
    setImageDrawable(drawable)
}