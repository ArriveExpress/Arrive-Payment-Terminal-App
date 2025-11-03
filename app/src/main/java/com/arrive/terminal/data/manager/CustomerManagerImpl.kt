package com.arrive.terminal.data.manager;

import com.arrive.terminal.domain.manager.CustomerManager
import com.arrive.terminal.domain.model.CustomerAccountModel
import com.arrive.terminal.domain.repository.CustomerRepository
import javax.inject.Inject

class CustomerManagerImpl @Inject constructor(
    private val customerRepository: CustomerRepository
) : CustomerManager {

    @Volatile private var lastCustomerNumber: String? = null
    @Volatile private var lastCustomerPin: String? = null
    @Volatile private var lastCustomerId: String? = null

    private val lock = Any() // Lock for synchronization

    override suspend fun fetchAuthorizedCustomer(): Result<CustomerAccountModel> {
        return getCustomerProfile(lastCustomerNumber.orEmpty(), lastCustomerPin.orEmpty())
    }

    override suspend fun getCustomerProfile(
        accountNumber: String,
        pin: String
    ): Result<CustomerAccountModel> {
        return customerRepository.getCustomerProfile(accountNumber, pin)
            .onSuccess {
                synchronized(lock) {
                    lastCustomerNumber = accountNumber
                    lastCustomerPin = pin
                    lastCustomerId = it.id
                }
            }
    }

    override suspend fun setDefaultCard(customerId: String, cardId: String): Result<Unit> {
        return customerRepository.setDefaultCard(customerId, cardId)
    }

    override suspend fun saveCreditCard(
        customerId: String,
        isManualEntry: Boolean,
        cardNumber: String,
        expiryMonth: String,
        expiryYear: String,
        cvc: String?
    ): Result<Unit> {
        return customerRepository.saveCreditCard(customerId, isManualEntry, cardNumber, expiryMonth, expiryYear, cvc)
    }

    override suspend fun addBalance(
        customerId: String,
        amount: Double,
        cardId: String?,
        isCardManualEntry: Boolean,
        cardNumber: String,
        cardExpMonth: String,
        cardExpYear: String,
        cardCvc: String?,
    ): Result<Unit> {
        return customerRepository.addBalanceExisting(customerId, amount, cardId, isCardManualEntry, cardNumber, cardExpMonth, cardExpYear, cardCvc)
    }

    override suspend fun setReview(
        customerId: String?,
        userId: String,
        rate: Int
    ): Result<Unit> {
        return customerRepository.setReview(customerId, userId, rate)
    }

    override fun clear() {
        synchronized(lock) {
            lastCustomerNumber = null
            lastCustomerPin = null
            lastCustomerId = null
        }
    }
}