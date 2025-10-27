package com.arrive.terminal.core.ui.helper

import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.arrive.terminal.core.ui.utils.getDateTimeFormatted
import java.lang.ref.WeakReference
import java.util.Timer
import java.util.TimerTask

/**
 * A class that updates a TextView with the current time every minute
 * with proper lifecycle management
 */
class TimeUpdater(textView: TextView) : DefaultLifecycleObserver {

    private val textViewRef = WeakReference(textView)
    private val textView get() = textViewRef.get()

    private var timer: Timer? = null
    private val handler = Handler(Looper.getMainLooper())

    /**
     * Start updating the time when the lifecycle owner is started
     */
    override fun onStart(owner: LifecycleOwner) {
        startTimeUpdates()
    }

    /**
     * Stop updating the time when the lifecycle owner is stopped
     */
    override fun onStop(owner: LifecycleOwner) {
        stopTimeUpdates()
    }

    /**
     * Start the timer to update the time every minute
     */
    private fun startTimeUpdates() {
        // Update immediately
        updateTimeText()

        // Schedule updates every minute
        timer = Timer().apply {
            // Schedule the task to run at the start of each minute
            schedule(object : TimerTask() {
                override fun run() {
                    handler.post { updateTimeText() }
                }
            }, getMillisToNextMinute(), UPDATE_INTERVAL_MILLIS) // 60 seconds = 1 minute
        }
    }

    /**
     * Stop the timer
     */
    private fun stopTimeUpdates() {
        timer?.cancel()
        timer = null
    }

    /**
     * Update the TextView with the current time
     */
    private fun updateTimeText() {
        textView?.text = getDateTimeFormatted()
    }

    /**
     * Calculate milliseconds until the start of the next minute
     * to sync the timer with the clock
     */
    private fun getMillisToNextMinute(): Long {
        val now = System.currentTimeMillis()
        val nextMinute = (now / UPDATE_INTERVAL_MILLIS + 1) * UPDATE_INTERVAL_MILLIS
        return nextMinute - now
    }

    companion object {

        private const val UPDATE_INTERVAL_MILLIS = 60_000L
    }
}