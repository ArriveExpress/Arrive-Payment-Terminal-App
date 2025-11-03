package com.arrive.terminal.domain.manager;

import com.arrive.terminal.domain.model.CustomerAccountModel

interface CustomerManager {

    suspend fun fetchAuthorizedCustomer(): Result<CustomerAccountModel>

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

    suspend fun addBalance(
        customerId: String,
        amount: Double,
        cardId: String?,
        isCardManualEntry: Boolean = false,
        cardNumber: String,
        cardExpMonth: String,
        cardExpYear: String,
        cardCvc: String? = null,
    ): Result<Unit>

    suspend fun setReview(
        customerId: String?,
        userId: String,
        rate: Int
    ): Result<Unit>

    fun clear()
}