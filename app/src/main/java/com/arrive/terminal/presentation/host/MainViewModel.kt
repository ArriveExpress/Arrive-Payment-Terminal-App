package com.arrive.terminal.presentation.host;

import LiveEvent
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import com.arrive.terminal.R
import com.arrive.terminal.core.data.network.retryWithBackoff
import com.arrive.terminal.core.ui.base.BaseViewModel
import com.arrive.terminal.core.ui.livedata.SafeLiveEvent
import com.arrive.terminal.data.network.response.AdSchedulesEventNT
import com.arrive.terminal.data.network.response.PayingTerminalEventNT
import com.arrive.terminal.data.network.response.WeatherEventNT
import com.arrive.terminal.domain.manager.AdSchedulesMapper
import com.arrive.terminal.domain.manager.CustomerManager
import com.arrive.terminal.domain.manager.DriverManager
import com.arrive.terminal.domain.manager.PayingTerminalEventMapper
import com.arrive.terminal.domain.manager.WeatherEventMapper
import com.arrive.terminal.domain.manager.StringsManager
import com.arrive.terminal.presentation.features.payment_method.PaymentMethodFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val driverManager: DriverManager,
    private val customerManager: CustomerManager,
    private val stringsManager: StringsManager
): BaseViewModel() {

    companion object{
        const val LOGOUT_DELAY = 5  * 60 * 1000L
    }

    val driverId: String
        get() = driverManager.getAuthorizedDriverId().orEmpty()

    val onSetupGraph = SafeLiveEvent<Int>()
    val onWeatherUpdated = LiveEvent()

    override fun onViewLoaded() {
        initStrings()
        viewModelScope.launch {
            val id = driverManager.getSavedDriverId()
            if (id == null) onSetupGraph.value = R.id.loginFragment
            else {
                driverManager.getMainScreen(id).onSuccess {
                    onSetupGraph.value = R.id.driverFragment
                }
                    .onFailure { onShowToast.value = mapToInfoMessage(it) }
            }
        }
    }

    private fun initStrings() {
        viewModelScope.launch { retryWithBackoff { stringsManager.loadRemoteStrings() } }
    }

    fun logout() {
        customerManager.clear()
        navigate(
            navigationResId = R.id.driverFragment,
            args = null,
            navOptions = NavOptions.Builder().apply {
                setPopUpTo(R.id.driverFragment, true)
            }.build()
        )
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

    fun updateWeather(eventData: WeatherEventNT) {
        val model = WeatherEventMapper(eventData).entity
        model?.let {
            viewModelScope.launch {
                driverManager.updateWeather(it)
                onWeatherUpdated.fire()
            }
        }
    }

    fun updateAdSchedules(eventData: AdSchedulesEventNT) {
        val models = AdSchedulesMapper(eventData).entity
        viewModelScope.launch {
            driverManager.updateAdSchedules(models)
        }
    }
}