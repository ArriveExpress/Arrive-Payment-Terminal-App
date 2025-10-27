package com.arrive.terminal.core.ui.extensions

import android.view.View

fun View.onClickSafe(onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener { onSafeClick(it) }
    setOnClickListener(safeClickListener)
}

class SafeClickListener(
    private var defaultInterval: Int = 1000,
    private val onSafeCLick: (View) -> Unit,
) : View.OnClickListener {
    
    private var lastTimeClicked: Long = 0

    override fun onClick(v: View) {
        val diff = System.currentTimeMillis() - lastTimeClicked

        if (diff in 1 until defaultInterval) {
            return
        }

        lastTimeClicked = System.currentTimeMillis()
        onSafeCLick(v)
    }
}