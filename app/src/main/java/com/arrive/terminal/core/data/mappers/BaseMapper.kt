package com.arrive.terminal.core.data.mappers

import com.arrive.terminal.core.data.network.AppException
import com.arrive.terminal.core.ui.extensions.takeIfNotBlank
import java.math.BigDecimal
import kotlin.reflect.KProperty1

open class BaseMapper<T : Any>(protected var response: T) {

    protected fun validateAsBigDecimal(value: Double?): BigDecimal {
        return BigDecimal.valueOf(validateAnyNull(value))
    }

    protected fun validateNullOrEmpty(property: String?): String {
        return property ?: throw AppException.ResponseValidationException("null string value")
    }

    protected fun <R> validateAnyNull(any: R?): R {
        return any ?: throw AppException.ResponseValidationException("null value")
    }

    protected fun <R> validateNull(property: KProperty1<T, R?>): R {
        return property.get(response) ?: throw createException("${property.name} is null")
    }

    protected fun validateNullOrEmpty(property: KProperty1<T, String?>): String {
        return property.get(response)
            .takeIfNotBlank()
            ?: throw createException("${property.name} is null or empty")
    }

    private fun createException(message: String) = AppException.ResponseValidationException(
        message = "${response::class.simpleName}: $message"
    )
}