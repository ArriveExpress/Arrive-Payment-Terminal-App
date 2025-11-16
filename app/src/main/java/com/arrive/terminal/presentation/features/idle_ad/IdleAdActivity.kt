package com.arrive.terminal.presentation.features.idle_ad

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.arrive.terminal.BuildConfig
import com.arrive.terminal.databinding.ActivityIdleAdBinding
import com.arrive.terminal.domain.model.AdScheduleModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlin.random.Random

/**
 * Activity that displays fullscreen ads when the app has been idle
 */
class IdleAdActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIdleAdBinding
    private var adSchedules: List<AdScheduleModel> = emptyList()
    private var currentAdIndex = 0
    private val handler = Handler(Looper.getMainLooper())
    private var autoAdvanceRunnable: Runnable? = null

    companion object {
        private const val EXTRA_AD_SCHEDULES = "extra_ad_schedules"
        private const val AD_DISPLAY_DURATION = 10_000L // 10 seconds per ad
        
        fun start(context: Context, adSchedules: List<AdScheduleModel>) {
            val intent = Intent(context, IdleAdActivity::class.java).apply {
                putParcelableArrayListExtra(EXTRA_AD_SCHEDULES, ArrayList(adSchedules))
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIdleAdBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Keep screen on while displaying ads
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Get ad schedules from intent
        adSchedules = intent.getParcelableArrayListExtra<AdScheduleModel>(EXTRA_AD_SCHEDULES)
            ?.let { expandAdSchedules(it) }
            ?: emptyList()

        if (adSchedules.isEmpty()) {
            finish()
            return
        }

        setupUI()
        displayAd(0)
    }

    private fun setupUI() {
        // Close button
        binding.closeButton.setOnClickListener {
            finish()
        }
    }

    /**
     * Expand ad schedules based on their multiply value
     * For example, if multiply=3, the ad will appear 3 times in the rotation
     */
    private fun expandAdSchedules(schedules: List<AdScheduleModel>): List<AdScheduleModel> {
        return buildList {
            schedules.forEach { schedule ->
                val times = schedule.multiply ?: 1
                repeat(times) {
                    add(schedule)
                }
            }
        }.shuffled(Random(System.currentTimeMillis()))
    }

    private fun displayAd(index: Int) {
        if (index >= adSchedules.size) {
            currentAdIndex = 0
        } else {
            currentAdIndex = index
        }

        val adSchedule = adSchedules[currentAdIndex]
        val imageUrl = adSchedule.ad?.imageUrl

        if (imageUrl.isNullOrBlank()) {
            // Skip to next ad if current one has no image
            scheduleNextAd()
            return
        }

        // Construct full image URL
        val fullImageUrl = "${BuildConfig.BASE_API_URL}/storage/$imageUrl"

        // Load ad image using Glide
        Glide.with(this)
            .load(fullImageUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.adImage)
        
        scheduleNextAd()
    }

    private fun scheduleNextAd() {
        // Cancel any existing scheduled ad
        autoAdvanceRunnable?.let { handler.removeCallbacks(it) }
        
        // Schedule the next ad
        autoAdvanceRunnable = Runnable {
            displayAd(currentAdIndex + 1)
        }
        handler.postDelayed(autoAdvanceRunnable!!, AD_DISPLAY_DURATION)
    }

    override fun onDestroy() {
        super.onDestroy()
        autoAdvanceRunnable?.let { handler.removeCallbacks(it) }
    }
}
