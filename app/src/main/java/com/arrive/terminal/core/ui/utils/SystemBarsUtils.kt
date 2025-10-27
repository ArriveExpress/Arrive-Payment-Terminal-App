package com.arrive.terminal.core.ui.utils

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat

@Suppress("DEPRECATION")
private const val FLAG_FULL_SCREEN = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

@Suppress("DEPRECATION")
fun Window.setFullScreen() {
    if (isAtLeastAndroidR()) {
        setDecorFitsSystemWindows(false)
    } else {
        decorView.systemUiVisibility = FLAG_FULL_SCREEN
    }
}

fun Window.setLightStatusAndNavigationBar() {
    val controller = WindowInsetsControllerCompat(this, decorView)
    controller.isAppearanceLightStatusBars = true
    controller.isAppearanceLightNavigationBars = true
}

fun Window.setNoLightStatusAndNavigationBar() {
    val controller = WindowInsetsControllerCompat(this, decorView)
    controller.isAppearanceLightStatusBars = false
    controller.isAppearanceLightNavigationBars = false
}

fun Window.changeStatusBarColor(color: Int) {
    clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    statusBarColor = color
}

fun Window.clearStatusBar() {
    clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    statusBarColor = Color.TRANSPARENT
}