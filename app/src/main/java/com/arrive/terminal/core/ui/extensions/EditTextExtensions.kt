package com.arrive.terminal.core.ui.extensions

import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import java.util.Calendar

val EditText.textOrEmpty: String
    get() = text?.toString().orEmpty()

class ExpiryDateInputFilter : InputFilter {

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        // Return if deleting
        if (source.isNullOrEmpty()) {
            return null
        }

        val result = StringBuilder(dest.toString())
        result.replace(dstart, dend, source.subSequence(start, end).toString())
        val resultString = result.toString()

        // Only allow digits
        if (!resultString.replace(" ", "").replace("/", "").matches(Regex("^[0-9]*$"))) {
            return ""
        }

        // Format should be "MM / YY"
        val cleanResult = resultString.replace(" ", "").replace("/", "")

        return when {
            cleanResult.length > 4 -> "" // Ensure no more than 4 digits
            else -> null // Allow the input
        }
    }
}

// Extension function to apply all expiry date formatting
fun EditText.setupAsExpiryDateField() {
    // Add the input filter
    filters = arrayOf(ExpiryDateInputFilter())

    // Add text watcher for additional formatting
    addTextChangedListener(object : TextWatcher {
        private var isFormatting = false
        private val calendar = Calendar.getInstance()
        private val currentYear = calendar.get(Calendar.YEAR) % 100 // Get last two digits of year
        private val currentMonth = calendar.get(Calendar.MONTH) + 1 // Months are 0-based

        override fun afterTextChanged(s: Editable?) {
            if (isFormatting || s == null) return

            isFormatting = true

            // Remove any existing spacing and slashes
            val digits = s.toString().replace(" ", "").replace("/", "")

            // Format the string
            val formatted = when {
                digits.length > 2 -> {
                    // Format with slash: "MM / YY"
                    val month = digits.substring(0, 2)
                    val year = digits.substring(2, minOf(4, digits.length))
                    "$month / $year"
                }
                else -> digits
            }

            // Validate month (01-12)
            if (digits.length >= 2) {
                val month = digits.substring(0, 2).toIntOrNull() ?: 0
                if (month < 1 || month > 12) {
                    setText("0" + digits[0] + if (digits.length > 1) " / " + digits.substring(1) else "")
                    setSelection(length())
                    isFormatting = false
                    return
                }
            }

            // Validate card hasn't expired
            if (digits.length == 4) {
                val month = digits.substring(0, 2).toIntOrNull() ?: 0
                val year = digits.substring(2, 4).toIntOrNull() ?: 0

                // Check if card is expired
                if (year < currentYear || (year == currentYear && month < currentMonth)) {
                    // Do nothing here, but you could show an error message
                }
            }

            if (formatted != s.toString()) {
                setText(formatted)
                setSelection(length())
            }

            isFormatting = false
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}

fun EditText.disableSelectInsertText() {
    makeTextNotSelectable()
    makeTextNotAllowPaste()
}

fun EditText.makeTextNotSelectable() {
    customSelectionActionModeCallback = object : ActionMode.Callback {

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?) = false
        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?) = false
        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?) = false
        override fun onDestroyActionMode(mode: ActionMode?) {}
    }
}

fun EditText.makeTextNotAllowPaste() {
    customInsertionActionModeCallback = object : ActionMode.Callback {

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?) = false
        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?) = false
        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?) = false
        override fun onDestroyActionMode(mode: ActionMode?) {}
    }
}