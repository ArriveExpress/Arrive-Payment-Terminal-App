package com.arrive.terminal.presentation.adapter;

data class CreditCardItem(
    val id: String,
    val default: Boolean,
    val lastFour: String,
    val drawBottomDivider: Boolean,
    val drawSelector: Boolean,
) : RVItem