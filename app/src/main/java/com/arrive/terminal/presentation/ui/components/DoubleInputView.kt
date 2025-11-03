package com.arrive.terminal.presentation.ui.components

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatEditText
import androidx.constraintlayout.widget.ConstraintLayout
import com.arrive.terminal.R
import androidx.core.content.withStyledAttributes

class DoubleInputView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    val inputFirst: AppCompatEditText
    val inputSeconds: AppCompatEditText
    val inputThird: AppCompatEditText
    val continueAction: ImageView

    private var firstMaxLength: Int = 11

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_double_input, this, true)

        inputFirst = findViewById(R.id.inputFirst)
        inputSeconds = findViewById(R.id.inputSeconds)
        inputThird = findViewById(R.id.inputThird)
        continueAction = findViewById(R.id.continueAction)

        attrs?.let {
            context.withStyledAttributes(it, R.styleable.DoubleInputView, 0, 0) {
                firstMaxLength = getInt(R.styleable.DoubleInputView_firstInputMaxLength, firstMaxLength)
            }
        }

        inputFirst.inputType = InputType.TYPE_CLASS_NUMBER
        inputFirst.filters = arrayOf(InputFilter.LengthFilter(firstMaxLength))

        inputSeconds.inputType = InputType.TYPE_CLASS_NUMBER
        inputSeconds.filters = arrayOf(InputFilter.LengthFilter(4))

        inputThird?.inputType = InputType.TYPE_CLASS_NUMBER
        inputThird?.filters = arrayOf(InputFilter.LengthFilter(4))

        inputFirst.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == firstMaxLength) {
                    inputSeconds.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        
        inputSeconds.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Check if we have 4 clean digits for expiry date (MM/YY format)
                val cleanText = s.toString().replace(" ", "").replace("/", "")
                if (cleanText.length == 4 && inputThird.visibility == View.VISIBLE) {
                    inputThird.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}