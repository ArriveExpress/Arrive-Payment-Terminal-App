package com.arrive.terminal.presentation.features.rides;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import com.arrive.terminal.R
import com.arrive.terminal.core.model.Constants
import com.arrive.terminal.core.model.ResultState
import com.arrive.terminal.core.model.isLoadingState
import com.arrive.terminal.core.ui.base.BaseViewModel
import com.arrive.terminal.core.ui.model.StringValue.Companion.asStringValue
import com.arrive.terminal.core.ui.utils.formatPrice
import com.arrive.terminal.domain.manager.DriverManager
import com.arrive.terminal.domain.manager.StringsManager
import com.arrive.terminal.domain.model.MainScreenModel
import com.arrive.terminal.presentation.adapter.FlaggedTripItem
import com.arrive.terminal.presentation.adapter.RVItem
import com.arrive.terminal.presentation.adapter.RideItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RidesViewModel @Inject constructor(
    private val driverManager: DriverManager,
    private val stringsManager: StringsManager
) : BaseViewModel() {

    val ridesState = MutableLiveData<ResultState<List<RVItem>>>()

    private var model: MainScreenModel? = null

    override fun onViewLoaded() {
        loadRides(force = false)
    }

    fun loadRides(force: Boolean) {
        if (ridesState.value.isLoadingState) {
            onShowToast.value = R.string.error_please_wait.asStringValue
            return
        }

        viewModelScope.launch {
            ridesState.value = ResultState.Loading
            driverManager.getMainScreenAuthorized(force)
                .onSuccess { result ->
                    model = result
                    ridesState.value = ResultState.Success(mapItems(result))
                }
                .onFailure { ridesState.value = ResultState.Error(it, message = mapToInfoMessage(it)) }
        }
    }

    private fun mapItems(model: MainScreenModel): List<RVItem> {
        return buildList {
            model.rides.map { rideModel ->
                RideItem(
                    id = rideModel.id.orEmpty(),
                    name = rideModel.text.orEmpty().asStringValue,
                    price = rideModel.price?.let { formatPrice(it) },
                    icon = when {
                        rideModel.price != null -> null
                        else -> R.drawable.ic_error
                    },
                ).let(::add)
            }

            if (model.flaggedTrip != null) {
                FlaggedTripItem(
                    id = "flagged_trip_${model.flaggedTrip.flaggedTripId}",
                    name = stringsManager.getString(
                        Constants.RIDES_ITEM_DIFFERENT_RIDE,
                        R.string.rides_item_different_ride.asStringValue.toString()
                    ).asStringValue,
                ).let(::add)
            }
        }
    }

    fun onFlaggedTripClick(item: FlaggedTripItem) {
        val flaggedTrip = model?.flaggedTrip ?: return
        if (flaggedTrip.price == null) {
            navigate(
                navigationResId = R.id.noPriceFragment,
                navOptions = NavOptions.Builder().apply {
                    setPopUpTo(R.id.driverFragment, false)
                }.build()
            )
            return
        }

//        navigate(
//            navigationResId = R.id.paymentMethodFragment,
//            args = PaymentMethodFragment.getBundle(
//                selectedRide = null,
//                flaggedTrip = flaggedTrip
//            ),
//            navOptions = NavOptions.Builder().apply {
//                setPopUpTo(R.id.driverFragment, false)
//            }.build()
//        )
    }

    fun onRideClick(item: RideItem) {
        val targetRide = model?.rides?.firstOrNull { it.id == item.id } ?: return
        if (targetRide.price == null) {
            navigate(
                navigationResId = R.id.noPriceFragment,
                navOptions = NavOptions.Builder().apply {
                    setPopUpTo(R.id.driverFragment, false)
                }.build()
            )
            return
        }

//        navigate(
//            navigationResId = R.id.paymentMethodFragment,
//            args = PaymentMethodFragment.getBundle(
//                selectedRide = targetRide,
//                flaggedTrip = null
//            ),
//            navOptions = NavOptions.Builder().apply {
//                setPopUpTo(R.id.driverFragment, false)
//            }.build()
//        )
    }
}