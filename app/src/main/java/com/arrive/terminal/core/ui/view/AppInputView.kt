package com.arrive.terminal.core.ui.view;

import android.content.Context
import android.util.AttributeSet
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import com.arrive.terminal.R
import com.arrive.terminal.databinding.ViewAppInputBinding

class AppInputView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = ViewAppInputBinding.inflate(LayoutInflater.from(context), this)

    private val focusChangeListeners = mutableListOf<OnFocusChangeListener>()

    var hasError = false
        set(value) {
            field = value
            setupInputState(hasError = value)
        }

    val editText get() = binding.input
    val text get() = binding.input.text?.toString().orEmpty()

    init {
        editText.isSaveEnabled = false
        initUI()
        setupInputState()
    }

    fun addOnFocusChangeListener(listener: OnFocusChangeListener) {
        focusChangeListeners.add(listener)
    }

    private fun initUI() = with(binding) {
        input.setOnFocusChangeListener { view, hasFocus ->
            setupInputState(hasFocus = hasFocus)
            focusChangeListeners.forEach { it.onFocusChange(view, hasFocus) }
        }
    }

    private fun setupInputState(
        hasFocus: Boolean = binding.input.hasFocus(),
        hasError: Boolean = false,
    ) {
        binding.background.background = when {
            hasFocus -> R.drawable.background_input_focused
            hasError -> R.drawable.background_input_error
            else -> R.drawable.background_input_enabled
        }.let { AppCompatResources.getDrawable(context, it) }

        binding.input.setTextColor(
            when {
                hasFocus -> R.color.neutral_900
                hasError -> R.color.error
                else -> R.color.neutral_900
            }.let { context.getColor(it) }
        )
    }
}