package com.arrive.terminal.core.ui.extensions

import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible

fun TextView.textOrGoneIfBlank(text: CharSequence?) {
    isVisible = text.isNullOrBlank().not()
    this.text = text ?: ""
}

fun TextView.textOrInvisibleIfBlank(text: CharSequence?) {
    visibility = if (text.isNullOrBlank()) View.INVISIBLE else View.VISIBLE
    this.text = text ?: ""
}

fun TextView.underline() {
    if (text.isBlank()) {
        return
    }

    text = SpannableString(text).apply {
        setSpan(UnderlineSpan(), 0, text.length, 0)
    }
}
