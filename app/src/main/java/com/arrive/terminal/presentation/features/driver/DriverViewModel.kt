package com.arrive.terminal.presentation.features.driver;

import androidx.lifecycle.MutableLiveData
import com.arrive.terminal.R
import com.arrive.terminal.core.ui.base.BaseViewModel
import com.arrive.terminal.core.ui.model.StringValue
import com.arrive.terminal.core.ui.utils.getDateTimeFormatted
import com.arrive.terminal.domain.manager.DriverManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DriverViewModel @Inject constructor(
    private val driverManager: DriverManager
) : BaseViewModel() {

    val driverId = MutableLiveData(StringValue.StringResource(
        resId = R.string.rides_driver_id,
        args = listOf(driverManager.getAuthorizedDriverId().orEmpty())
    ))

    override fun onViewLoaded() {
        if (driverManager.getAuthorizedDriverId().isNullOrBlank()) {
            onRestartApplication.fire()
        }
    }

    fun onMyAccountClick() {
        navigate(navigationResId = R.id.accountLoginFragment)
    }
}