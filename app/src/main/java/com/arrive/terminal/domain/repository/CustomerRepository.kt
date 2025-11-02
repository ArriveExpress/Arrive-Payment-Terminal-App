package com.arrive.terminal.domain.repository

import com.arrive.terminal.domain.model.CustomerAccountModel

interface CustomerRepository {

    suspend fun getCustomerProfile(accountNumber: String, pin: String): Result<CustomerAccountModel>

    suspend fun setDefaultCard(customerId: String, cardId: String): Result<Unit>

    suspend fun saveCreditCard(
        customerId: String,
        isManualEntry: Boolean,
        cardNumber: String,
        expiryMonth: String,
        expiryYear: String,
        cvc: String?
    ): Result<Unit>

    suspend fun addBalanceExisting(
        customerId: String,
        amount: Double,
        cardId: String?,
        cardNumber: String,
        cardExpMonth: String,
        cardExpYear: String,
    ): Result<Unit>

    suspend fun setReview(
        customerId: String?,
        userId: String,
        rate: Int
    ): Result<Unit>
}