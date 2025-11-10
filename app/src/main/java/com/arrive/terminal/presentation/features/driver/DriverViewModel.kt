package com.arrive.terminal.presentation.features.driver;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.arrive.terminal.R
import com.arrive.terminal.core.ui.base.BaseViewModel
import com.arrive.terminal.core.ui.model.StringValue
import com.arrive.terminal.core.ui.utils.getDateTimeFormatted
import com.arrive.terminal.domain.manager.DriverManager
import com.arrive.terminal.domain.model.WeatherModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DriverViewModel @Inject constructor(
    private val driverManager: DriverManager
) : BaseViewModel() {

    val driverId = MutableLiveData(StringValue.StringResource(
        resId = R.string.rides_driver_id,
        args = listOf(driverManager.getAuthorizedDriverId().orEmpty())
    ))

    val weather = MutableLiveData<WeatherModel?>()

    override fun onViewLoaded() {
        if (driverManager.getAuthorizedDriverId().isNullOrBlank()) {
            onRestartApplication.fire()
        } else {
            loadWeather()
        }
    }

    private fun loadWeather() {
        viewModelScope.launch {
            weather.value = driverManager.getLastWeather()
        }
    }

    fun onMyAccountClick() {
        navigate(navigationResId = R.id.accountLoginFragment)
    }
}