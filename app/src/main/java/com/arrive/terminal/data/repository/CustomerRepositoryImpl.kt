package com.arrive.terminal.data.repository;

import com.arrive.terminal.core.data.network.safeResultCall
import com.arrive.terminal.data.network.api.CustomerApiService
import com.arrive.terminal.data.network.mapper.CustomerAccountMapper
import com.arrive.terminal.data.network.request.AddBalanceExistingCardRequest
import com.arrive.terminal.data.network.request.AddBalanceNewCardRequest
import com.arrive.terminal.data.network.request.GetAccountRequest
import com.arrive.terminal.data.network.request.ReviewRequest
import com.arrive.terminal.data.network.request.SaveCreditCardRequest
import com.arrive.terminal.data.network.request.SetDefaultCardRequest
import com.arrive.terminal.domain.repository.CustomerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CustomerRepositoryImpl @Inject constructor(
    private val apiService: CustomerApiService
) : CustomerRepository {

    override suspend fun getCustomerProfile(
        accountNumber: String,
        pin: String
    ) = withContext(Dispatchers.IO) {
        safeResultCall {
            apiService.getCustomerProfile(
                request = GetAccountRequest(accountNumber, pin)
            )
        }.mapCatching { result ->
            CustomerAccountMapper(result).entity
        }
    }

    override suspend fun setDefaultCard(customerId: String, cardId: String) = withContext(Dispatchers.IO) {
        safeResultCall {
            apiService.setDefaultCard(
                request = SetDefaultCardRequest(customerId, cardId)
            )
        }
    }

    override suspend fun saveCreditCard(
        customerId: String,
        cardNumber: String,
        expiryMonth: String,
        expiryYear: String
    )  = withContext(Dispatchers.IO) {
        safeResultCall {
            apiService.saveCreditCard(
                request = SaveCreditCardRequest(
                    customerId = customerId,
                    cardNumber = cardNumber,
                    expiryMonth = expiryMonth,
                    expiryYear = expiryYear
                )
            )
        }
    }

    override suspend fun addBalanceExisting(
        customerId: String,
        amount: Double,
        cardId: String?,
        cardNumber: String,
        cardExpMonth: String,
        cardExpYear: String,
    ) = withContext(Dispatchers.IO) {
        safeResultCall {
            if (cardId != null) {
                apiService.addBalanceExisting(
                    request = AddBalanceExistingCardRequest(
                    customerId = customerId,
                    cardId = cardId,
                    amount = amount,
                    newCard = false
                ))
            } else {
                apiService.addBalanceNew(
                    request = AddBalanceNewCardRequest(
                        customerId = customerId,
                        amount = amount,
                        newCard = true,
                        cardNumber = cardNumber,
                        expMonth = cardExpMonth,
                        expYear = cardExpYear
                    )
                )
            }
        }
    }

    override suspend fun setReview(customerId: String?, userId: String, rate: Int): Result<Unit> =
        withContext(Dispatchers.IO) {
            safeResultCall {
                apiService.setReview(
                    request = ReviewRequest(
                        customerId = customerId,
                        userId = userId,
                        rate = rate
                    )
                )
            }
        }
}