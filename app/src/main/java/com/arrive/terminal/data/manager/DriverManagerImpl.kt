package com.arrive.terminal.data.manager

import com.arrive.terminal.domain.manager.DriverManager
import com.arrive.terminal.domain.model.AccountModel
import com.arrive.terminal.domain.model.CardModel
import com.arrive.terminal.domain.model.FlaggedTripModel
import com.arrive.terminal.domain.model.MainScreenModel
import com.arrive.terminal.domain.model.RideModel
import com.arrive.terminal.domain.repository.DriverRepository
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized
import javax.inject.Inject

@OptIn(InternalCoroutinesApi::class)
class DriverManagerImpl @Inject constructor(
    private val repository: DriverRepository,
) : DriverManager {

    @Volatile
    private var currentDriverId: String? = null

    private val lock = Any() // Lock for synchronization

    override suspend fun getMainScreenAuthorized(force: Boolean): Result<MainScreenModel> {
        return getMainScreen(getAuthorizedDriverId().orEmpty())
    }

    override suspend fun getMainScreen(driverId: String): Result<MainScreenModel> {
        return repository.getMainScreen(driverId)
            .onSuccess {
                synchronized(lock) {
                    currentDriverId = driverId
                }
            }
            .map { model -> model.copy(rides = model.rides.takeLast(TAKE_LAST_COUNT)) }
    }

    override suspend fun processCardPayment(
        ride: RideModel?,
        flaggedTrip: FlaggedTripModel?,
        card: CardModel
    ): Result<Unit> {
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
    ): Result<Unit> {
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
