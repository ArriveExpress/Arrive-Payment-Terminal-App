package com.arrive.terminal.core.ui.model;

import android.content.Context
import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

sealed class StringValue : Parcelable {

    @Parcelize
    data class DynamicString(
        val value: String
    ) : StringValue(), Parcelable

    @Parcelize
    data class StringResource(
        @StringRes val resId: Int,
        val args: @RawValue List<Any> = emptyList()
    ) : StringValue(), Parcelable

    @Parcelize
    data class SpannableString(
        val value: CharSequence,
    ) : StringValue(), Parcelable

    fun asString(context: Context) = when (this) {
        is DynamicString -> value
        is StringResource -> context.getString(resId, *args.toTypedArray())
        else -> ""
    }

    fun asCharSequence(context: Context) = (this as? SpannableString)?.value ?: asString(context)

    companion object {

        val Int.asStringValue get() = StringResource(this)

        val String.asStringValue get() = DynamicString(this)

        fun stringResource(@StringRes resId: Int, vararg args: Any) = StringResource(
            resId = resId,
            args = args.toList()
        )
    }
}