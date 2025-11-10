package com.arrive.terminal.data.manager

import com.arrive.terminal.domain.manager.DriverManager
import com.arrive.terminal.domain.model.AccountModel
import com.arrive.terminal.domain.model.CardModel
import com.arrive.terminal.domain.model.FlaggedTripModel
import com.arrive.terminal.domain.model.MainScreenModel
import com.arrive.terminal.domain.model.RideModel
import com.arrive.terminal.domain.model.WeatherModel
import com.arrive.terminal.domain.repository.DriverIdRepository
import com.arrive.terminal.domain.repository.DriverRepository
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized
import javax.inject.Inject

@OptIn(InternalCoroutinesApi::class)
class DriverManagerImpl @Inject constructor(
    private val repository: DriverRepository,
    private val driverIdRepository: DriverIdRepository
) : DriverManager {

    @Volatile
    private var currentDriverId: String? = null

    @Volatile
    private var lastFeeFixed: Double? = null

    @Volatile
    private var lastFeePercent: Double? = null

    @Volatile
    private var lastIsRateEnabled: Boolean? = null

    @Volatile
    private var defaultRate: Int? = null

    @Volatile
    private var lastWeather: WeatherModel? = null

    private val lock = Any() // Lock for synchronization

    override suspend fun getMainScreenAuthorized(force: Boolean): Result<MainScreenModel> {
        return getMainScreen(getAuthorizedDriverId().orEmpty())
    }

    override suspend fun saveDriverId(driverId: String) {
        driverIdRepository.saveDriverId(driverId)
    }

    override suspend fun getSavedDriverId(): String? {
        return driverIdRepository.getDriverId()
    }

    override suspend fun getFeeFixed(driverId: String?): Double? {
        if (lastFeeFixed == null) {
            driverId?.let { getMainScreen(it) }
        }
        return lastFeeFixed
    }

    override suspend fun getFeePercent(driverId: String?): Double? {
        if (lastFeePercent == null) {
            driverId?.let { getMainScreen(it) }
        }
        return lastFeePercent
    }

    override suspend fun getIsRateEnabled(driverId: String?): Boolean? {
        if (lastIsRateEnabled == null) {
            driverId?.let { getMainScreen(it) }
        }
        return lastIsRateEnabled
    }

    override suspend fun getDefaultRate(driverId: String?): Int? {
        if (defaultRate == null) {
            driverId?.let { getMainScreen(it) }
        }
        return defaultRate
    }

    override suspend fun getLastWeather(driverId: String?): WeatherModel? {
        if (lastWeather == null) {
            driverId?.let { getMainScreen(it) }
        }
        return lastWeather
    }

    override suspend fun updateWeather(weather: WeatherModel) {
        synchronized(lock) {
            lastWeather = weather
        }
    }

    override suspend fun getMainScreen(driverId: String): Result<MainScreenModel> {
        return repository.getMainScreen(driverId)
            .onSuccess {
                synchronized(lock) {
                    currentDriverId = driverId
                }
                lastFeeFixed = it.fixed
                lastFeePercent = it.percent
                lastIsRateEnabled = it.isRateEnabled
                defaultRate = it.defaultRate
                lastWeather = it.weather
            }
            .map { model ->
                model.copy(rides = model.rides.takeLast(TAKE_LAST_COUNT))
            }
    }

    override suspend fun processCardPayment(
        ride: RideModel?,
        flaggedTrip: FlaggedTripModel?,
        card: CardModel
    ): Result<String?> {
        return repository.processCardPayment(
            driverId = getAuthorizedDriverId().orEmpty(),
            ride = ride,
            flaggedTrip = flaggedTrip,
            card = card
        )
    }

    override suspend fun processAccountPayment(
        ride: RideModel?,
        flaggedTrip: FlaggedTripModel?,
        account: AccountModel
    ): Result<String?> {
        return repository.processAccountPayment(
            driverId = getAuthorizedDriverId().orEmpty(),
            ride = ride,
            flaggedTrip = flaggedTrip,
            account = account
        )
    }

    override fun getAuthorizedDriverId(): String? {
        return currentDriverId
    }

    companion object {

        private const val TAKE_LAST_COUNT = 10
    }
}
