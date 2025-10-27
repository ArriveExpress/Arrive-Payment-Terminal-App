package com.arrive.terminal.core.model;

import com.arrive.terminal.core.ui.model.StringValue

sealed class ResultState<out R> {

    data class Success<out T>(val data: T) : ResultState<T>()

    data class Error(
        val error: Throwable,
        val throwable: Throwable? = null,
        val message: StringValue? = null
    ) : ResultState<Nothing>()

    data object Loading : ResultState<Nothing>()
}