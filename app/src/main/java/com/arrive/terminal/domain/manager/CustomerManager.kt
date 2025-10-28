package com.arrive.terminal.domain.manager;

import com.arrive.terminal.domain.model.CustomerAccountModel

interface CustomerManager {

    suspend fun fetchAuthorizedCustomer(): Result<CustomerAccountModel>

    suspend fun getCustomerProfile(accountNumber: String, pin: String): Result<CustomerAccountModel>

    suspend fun setDefaultCard(customerId: String, cardId: String): Result<Unit>

    suspend fun saveCreditCard(
        customerId: String,
        cardNumber: String,
        expiryMonth: String,
        expiryYear: String
    ): Result<Unit>

    suspend fun addBalance(
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

    fun clear()
}