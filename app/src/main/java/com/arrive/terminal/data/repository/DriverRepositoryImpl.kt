package com.arrive.terminal.data.repository;

import com.arrive.terminal.core.data.network.safeResultCall
import com.arrive.terminal.data.network.api.DriverApiService
import com.arrive.terminal.data.network.mapper.MainScreenMapper
import com.arrive.terminal.data.network.request.AccountPaymentRequest
import com.arrive.terminal.data.network.request.CardPaymentRequest
import com.arrive.terminal.domain.model.AccountModel
import com.arrive.terminal.domain.model.CardModel
import com.arrive.terminal.domain.model.FlaggedTripModel
import com.arrive.terminal.domain.model.RideModel
import com.arrive.terminal.domain.repository.DriverRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DriverRepositoryImpl @Inject constructor(
    private val apiService: DriverApiService
) : DriverRepository {

    override suspend fun getMainScreen(driverId: String) = withContext(Dispatchers.IO) {
        safeResultCall {
            apiService.getMainScreen(driverId)
        }.mapCatching {
            MainScreenMapper(it).entity
        }
    }

    override suspend fun processCardPayment(
        driverId: String,
        ride: RideModel?,
        flaggedTrip: FlaggedTripModel?,
        card: CardModel
    ) = withContext(Dispatchers.IO) {
        safeResultCall {
            apiService.processCardPayment(
                request = CardPaymentRequest(
                    driverId = driverId,
                    rideId = ride?.id,
                    amount = ride?.price ?: flaggedTrip?.price ?: .0,
                    flaggedTripId = flaggedTrip?.flaggedTripId,
                    paymentMethod = CARD_PAYMENT_TYPE,
                    customerPhone = ride?.customerPhone,
                    isCardManualEntry = card.isManualEntry,
                    cardNumber = card.number,
                    cardExpireMonth = card.cardExpireMonth,
                    cardExpireYear = card.cardExpireYear,
                    cardCvc = card.cvc
                )
            ).customerId
        }
    }

    override suspend fun processAccountPayment(
        driverId: String,
        ride: RideModel?,
        flaggedTrip: FlaggedTripModel?,
        account: AccountModel
    ) = withContext(Dispatchers.IO) {
        safeResultCall {
            apiService.processAccountPayment(
                request = AccountPaymentRequest(
                    driverId = driverId,
                    rideId = ride?.id,
                    amount = ride?.price ?: flaggedTrip?.price ?: .0,
                    flaggedTripId = flaggedTrip?.flaggedTripId,
                    paymentMethod = ACCOUNT_PAYMENT_TYPE,
                    customerPhone = ride?.customerPhone,
                    accountNumber = account.number,
                    accountPin = account.pin,
                )
            ).customerId
        }
    }

    companion object {

        private const val CARD_PAYMENT_TYPE = "credit"
        private const val ACCOUNT_PAYMENT_TYPE = "account"
    }
}