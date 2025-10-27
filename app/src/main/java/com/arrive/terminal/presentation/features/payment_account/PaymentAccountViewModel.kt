package com.arrive.terminal.presentation.features.payment_account;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import com.arrive.terminal.R
import com.arrive.terminal.core.ui.base.BaseViewModel
import com.arrive.terminal.core.ui.extensions.getOrNull
import com.arrive.terminal.core.ui.extensions.valueOrEmpty
import com.arrive.terminal.core.ui.model.StringValue.Companion.asStringValue
import com.arrive.terminal.core.ui.utils.withProgress
import com.arrive.terminal.domain.manager.DriverManager
import com.arrive.terminal.domain.model.AccountModel
import com.arrive.terminal.domain.model.FlaggedTripModel
import com.arrive.terminal.domain.model.RideModel
import com.arrive.terminal.presentation.features.payment_account.PaymentAccountFragment.Companion.KEY_FLAGGED_MODEL
import com.arrive.terminal.presentation.features.payment_account.PaymentAccountFragment.Companion.KEY_RIDE_MODEL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentAccountViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val driverManager: DriverManager
): BaseViewModel() {

    private val selectedRide = savedStateHandle.getOrNull<RideModel>(KEY_RIDE_MODEL)
    private val flaggedTrip = savedStateHandle.getOrNull<FlaggedTripModel>(KEY_FLAGGED_MODEL)

    val number = MutableLiveData<String>()
    val pin = MutableLiveData<String>()

    val hostProgress = MutableLiveData<Boolean>()

    fun onConfirmClick() {
        if (number.valueOrEmpty.isBlank() || pin.valueOrEmpty.isBlank()) {
            onShowToast.value = R.string.error_field_empty.asStringValue
            return
        }

        viewModelScope.launch {
            onClearFocusHideKeyboard.fire()
            withProgress(hostProgress) {
                driverManager.processAccountPayment(
                    ride = selectedRide,
                    flaggedTrip = flaggedTrip,
                    account = AccountModel(number.valueOrEmpty, pin.valueOrEmpty)
                ).onSuccess {
                    navigate(
                        navigationResId = R.id.paymentSuccessFragment,
                        navOptions = NavOptions.Builder().apply {
                            setPopUpTo(R.id.driverFragment, false)
                        }.build()
                    )
                }.onFailure {
                    onShowToast.value = mapToInfoMessage(it)
                }
            }
        }
    }
}