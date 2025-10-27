package com.arrive.terminal.core.ui.base

import LiveEvent
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.ViewModel
import androidx.navigation.NavOptions
import com.arrive.terminal.R
import com.arrive.terminal.core.data.network.AppException
import com.arrive.terminal.core.ui.livedata.SafeLiveEvent
import com.arrive.terminal.core.ui.model.StringValue
import com.arrive.terminal.core.ui.model.StringValue.Companion.asStringValue
import com.arrive.terminal.core.ui.navigation.NavigationItem
import com.arrive.terminal.core.ui.utils.STUB

abstract class BaseViewModel : ViewModel() {

    /* Events */
    val onShowToast = SafeLiveEvent<StringValue>()
    val onNavigate = SafeLiveEvent<NavigationItem>()
    val onClearFocusHideKeyboard = LiveEvent()
    val onRestartApplication = LiveEvent()

    open fun onViewLoaded() = STUB

    protected fun showNotImplementedYet(message: String? = null) {
        val toastMessage = buildString {
            message?.let { append("$it | ") }
            append("Not implemented yet")
        }
        onShowToast.value = toastMessage.asStringValue
    }

    protected fun mapToInfoMessage(exception: Throwable): StringValue {
        return when (exception) {
            is AppException.NetworkException -> R.string.error_network_message.asStringValue
            is AppException -> exception.message?.asStringValue ?: R.string.error_something_bad_happen.asStringValue
            else -> R.string.error_something_bad_happen.asStringValue
        }
    }

    protected fun navigate(
        @IdRes navigationResId: Int,
        args: Bundle? = null,
        navOptions: NavOptions? = null
    ) {
        onNavigate.postValue(
            NavigationItem.NavigateByResource(navigationResId, args, navOptions)
        )
    }

    protected fun navigate(
        @IdRes navigationResId: Int,
        args: Bundle? = null,
        useCrossFade: Boolean = false,
        asStartDestination: Boolean = false,
        requestKey: String? = null,
        resultListener: FragmentResultListener? = null
    ) {
        onNavigate.postValue(
            NavigationItem.NavigateByResource(
                id = navigationResId,
                args = args,
                asStartDestination = asStartDestination,
                useCrossFade = useCrossFade,
                requestKey = requestKey,
                resultListener = resultListener
            )
        )
    }

    fun navigateBack() {
        onNavigate.postValue(NavigationItem.NavigateBack())
    }

    fun navigateBack(@IdRes popupTo: Int, inclusive: Boolean) {
        onNavigate.postValue(NavigationItem.NavigateBack(popupTo, inclusive))
    }
}