package com.arrive.terminal.core.data.network

sealed class AppException(
    override val message : String?,
    cause: Throwable? = null
) : Exception(message, cause) {

    open class ResponseValidationException(
        message : String? = null,
        cause: Throwable? = null,
    ) : AppException(message, cause)

    class NetworkException(
        message: String? = null,
        cause: Throwable? = null,
    ) : AppException(message, cause)

    open class HttpException(
        message: String? = null,
        cause: Throwable? = null,
        val statusCode: Int
    ) : AppException(message, cause) {

        open class ClientException(
            message: String?,
            cause: Throwable? = null,
            errorCode: Int,
        ) : HttpException(message, cause, errorCode)

        class ServerException(
            message: String? = null,
            cause: Throwable? = null,
            errorCode: Int,
        ) : HttpException(message, cause, errorCode)
    }

    class ParseJsonException(
        message: String? = null,
        cause: Throwable? = null,
    ) : AppException(message, cause)

    class SomethingBadHappenedException(
        message: String? = null,
        cause: Throwable? = null,
    ) : AppException(message, cause)
}