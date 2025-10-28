package com.arrive.terminal.core.ui.view;

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.content.withStyledAttributes
import androidx.core.view.isVisible
import com.arrive.terminal.R
import com.arrive.terminal.core.ui.extensions.dpToPx
import com.arrive.terminal.core.ui.extensions.getDrawableCompat
import com.arrive.terminal.core.ui.extensions.underline
import com.arrive.terminal.databinding.ViewAppButtonBinding

class AppButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : CardView(context, attrs, defStyleAttr) {

    private val binding = ViewAppButtonBinding.inflate(LayoutInflater.from(context), this)

    init {
        attrs?.let { findAttributes(attrs, defStyleAttr) }
        val outValue = TypedValue().apply {
            context.theme.resolveAttribute(android.R.attr.selectableItemBackground, this, true);
        }
        foreground = context.getDrawableCompat(outValue.resourceId)
    }

    fun setProgress(hasProgress: Boolean) = with(binding) {
        isClickable = hasProgress.not()
        isFocusable = hasProgress.not()
        textView.visibility = if (hasProgress.not()) View.VISIBLE else View.INVISIBLE
        progress.isVisible = hasProgress
    }

    fun setText(text: String) {
        binding.textView.text = text
    }

    private fun findAttributes(attrs: AttributeSet, defStyleAttr: Int) {
        context.withStyledAttributes(attrs, R.styleable.AppButtonView, defStyleAttr) {
            setCardBackgroundColor(getColor(R.styleable.AppButtonView_backgroundColor, context.getColor(R.color.primary)))
            cardElevation = getDimension(R.styleable.AppButtonView_elevation, 3f)
            radius = getDimension(R.styleable.AppButtonView_cornerRadius, context.dpToPx(8).toFloat())
            val contentPaddingHorizontal = getDimension(R.styleable.AppButtonView_paddingHorizontal, 0f).toInt()
            setContentPadding(contentPaddingHorizontal, 0, contentPaddingHorizontal, 0)
            binding.progress.indeterminateTintList = ColorStateList.valueOf(getColor(R.styleable.AppButtonView_progressColor, context.getColor(R.color.primary)))
            binding.textView.text = getString(R.styleable.AppButtonView_text)
            binding.textView.setTextColor(getColor(R.styleable.AppButtonView_textColor, context.getColor(R.color.neutral_0)))
            if (getBoolean(R.styleable.AppButtonView_underline, false)) {
                binding.textView.underline()
            }
        }
    }
}