package com.arrive.terminal.core.ui.helper

import android.view.View
import android.view.Window
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

object KeyboardHelper {

    fun Window.hideKeyboard(view: View) {
        WindowCompat.getInsetsController(this, view).hide(WindowInsetsCompat.Type.ime())
    }

    fun Window.showKeyboard(editText: View) {
        editText.requestFocus()
        WindowCompat.getInsetsController(this, editText).show(WindowInsetsCompat.Type.ime())
    }

    fun Window.clearFocusHideKeyboard(root: View) {
        root.clearFocus()
        WindowCompat.getInsetsController(this, root).hide(WindowInsetsCompat.Type.ime())
    }
}