package com.arrive.terminal.domain.model;

data class CardModel(
    val isManualEntry: Boolean = false,
    val number: String,
    val cardExpireMonth: String,
    val cardExpireYear: String,
    val cvc: String? = null,
    val zipCode: String? = null,
)