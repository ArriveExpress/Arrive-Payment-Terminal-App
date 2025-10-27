package com.arrive.terminal.core.ui.extensions

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type.navigationBars
import androidx.core.view.WindowInsetsCompat.Type.systemBars
import androidx.core.view.updatePadding
import com.arrive.terminal.R

fun View.doOnApplyWindowInsets(block: (view: View, insets: WindowInsetsCompat, padding:Rect) -> WindowInsetsCompat) {
    val initialPadding = recordInitialPaddingForView(this)

    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        block(v, insets, initialPadding)
    }

    requestApplyInsetsWhenAttached()
}

fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        requestApplyInsets()
    } else {
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                v.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}

fun View.applyInsets() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val systemBars = insets.getInsets(systemBars())
        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
        insets
    }
}

private fun recordInitialPaddingForView(view: View) = Rect(
    view.paddingLeft, view.paddingTop, view.paddingRight, view.paddingBottom
)

val WindowInsetsCompat.statusBars: Insets
    get() = getInsets(systemBars())

val WindowInsetsCompat.navigationBars: Insets
    get() = getInsets(navigationBars())
