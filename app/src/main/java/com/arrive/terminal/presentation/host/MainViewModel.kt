package com.arrive.terminal.presentation.host;

import androidx.navigation.NavOptions
import com.arrive.terminal.R
import com.arrive.terminal.core.ui.base.BaseViewModel
import com.arrive.terminal.core.ui.livedata.SafeLiveEvent
import com.arrive.terminal.data.network.response.PayingTerminalEventNT
import com.arrive.terminal.domain.manager.DriverManager
import com.arrive.terminal.domain.manager.PayingTerminalEventMapper
import com.arrive.terminal.presentation.features.payment_method.PaymentMethodFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val driverManager: DriverManager
): BaseViewModel() {

    val driverId: String
        get() = driverManager.getAuthorizedDriverId().orEmpty()

    val onSetupGraph = SafeLiveEvent<Int>()

    override fun onViewLoaded() {
//        onSetupGraph.value = R.id.accountLoginFragment
//        return

        onSetupGraph.value = R.id.loginFragment
    }

    fun navigateToPaymentMethod(eventData: PayingTerminalEventNT) {
        val model = PayingTerminalEventMapper(eventData).entity

        navigate(
            navigationResId = R.id.paymentMethodFragment,
            args = PaymentMethodFragment.getBundle(model),
            navOptions = NavOptions.Builder().apply {
                setPopUpTo(R.id.driverFragment, false)
            }.build()
        )
    }
}