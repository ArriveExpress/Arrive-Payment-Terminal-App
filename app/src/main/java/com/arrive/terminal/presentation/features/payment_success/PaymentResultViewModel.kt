package com.arrive.terminal.presentation.features.payment_success;

import com.arrive.terminal.R
import com.arrive.terminal.core.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PaymentResultViewModel @Inject constructor(): BaseViewModel() {

    fun onTryAgainClick() {
        navigateBack()
    }

    fun toDriverPage() {
        navigateBack(R.id.driverFragment, inclusive = false)
    }
}