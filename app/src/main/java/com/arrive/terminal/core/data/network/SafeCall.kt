package com.arrive.terminal.core.data.network

import com.google.gson.JsonSyntaxException
import retrofit2.HttpException
import com.arrive.terminal.core.ui.utils.safe
import com.arrive.terminal.core.data.network.AppException.HttpException.*
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import java.net.ConnectException
import java.net.UnknownHostException

suspend fun <T> safeResultCall(
    call: suspend () -> T
): Result<T> {
    return try {
        val callResult = call.invoke()
        Result.success(callResult)
    } catch (throwable: Throwable) {
        val appException = when (throwable) {
            is AppException -> throwable
            is ConnectException, is UnknownHostException -> AppException.NetworkException(cause = throwable)

            is HttpException -> {
                val errorBody = throwable.getErrorMessage()
                when {
                    throwable.code() in 400..499 -> ClientException(
                        message = errorBody?.message,
                        errorCode = throwable.code(),
                        cause = throwable
                    )

                    throwable.code() in 500..599 -> ServerException(
                        errorCode = throwable.code(),
                        cause = throwable
                    )

                    else -> AppException.HttpException(
                        statusCode = throwable.code(),
                        cause = throwable
                    )
                }
            }

            is JsonSyntaxException -> AppException.ParseJsonException(cause = throwable)
            else -> AppException.SomethingBadHappenedException(cause = throwable)
        }
        Result.failure(appException)
    }
}

private fun HttpException.getErrorMessage(): ErrorBody? {
    return safe {
        response()?.errorBody()?.string()?.let {
            GsonBuilder().create().fromJson(it, ErrorBody::class.java)
        }
    }
}

class ErrorBody(
    @SerializedName("message") val message: String? = null
)