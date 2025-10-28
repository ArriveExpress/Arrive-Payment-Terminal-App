package com.arrive.terminal.core.ui.utils

import android.app.AlertDialog
import android.content.Context
import android.os.SystemClock
import android.view.MotionEvent
import android.widget.EditText
import kotlin.system.exitProcess

/**
 * Simple secret quit mechanism - tap corners in sequence then enter password
 */
object SecretQuit {
    private const val PASSWORD = "ARRIVE2025"
    private var tapSequence = 0
    private var lastTapTime = 0L
    
    fun handleTouch(context: Context, event: MotionEvent, screenWidth: Int, screenHeight: Int) {
        if (event.action != MotionEvent.ACTION_UP) return
        
        val x = event.x
        val y = event.y
        val now = SystemClock.elapsedRealtime()
        
        // Reset if too much time passed
        if (now - lastTapTime > 3000) tapSequence = 0
        
        val corner = 100f
        val isCorrectCorner = when (tapSequence) {
            0 -> x < corner && y < corner // Top-left
            1 -> x > screenWidth - corner && y < corner // Top-right  
            2 -> x > screenWidth - corner && y > screenHeight - corner // Bottom-right
            3 -> x < corner && y > screenHeight - corner // Bottom-left
            else -> false
        }
        
        if (isCorrectCorner) {
            tapSequence++
            lastTapTime = now
            if (tapSequence >= 4) {
                tapSequence = 0
                showPasswordDialog(context)
            }
        } else {
            tapSequence = 0
        }
    }
    
    private fun showPasswordDialog(context: Context) {
        val input = EditText(context)
        input.inputType = 129 // Password type
        
        AlertDialog.Builder(context)
            .setTitle("Enter Password")
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                if (input.text.toString() == PASSWORD) {
                    exitProcess(0)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}