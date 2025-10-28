package com.arrive.terminal.presentation.features.payment_success;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.arrive.terminal.R
import com.arrive.terminal.core.ui.base.BaseViewModel
import com.arrive.terminal.core.ui.utils.withProgress
import com.arrive.terminal.domain.manager.CustomerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentResultViewModel @Inject constructor(
    private val customerManager: CustomerManager
) : BaseViewModel() {

    val progress = MutableLiveData<Boolean>()

    fun backToDriverPage(rate: Int, driverId: String?, customerId: String?) {
        viewModelScope.launch {
            sendRating(rate, driverId, customerId)
            navigateBack(R.id.driverFragment, inclusive = false)
        }
    }

    private suspend fun sendRating(rate: Int, driverId: String?, customerId: String?) {
        driverId?.let {
            withProgress(progress) {
                customerManager.setReview(
                    customerId = customerId,
                    userId = driverId,
                    rate = rate
                )
            }
        }
    }
}