package com.kalshikotlinsdk.model

sealed class KalshiResult<out T> {
    data class Success<T>(val data: T) : KalshiResult<T>()

    sealed class Failure : KalshiResult<Nothing>() {
        data class HttpError(
            val code: Int,
            val msg: String,
            val e: Exception,
        ) : Failure()

        data class SerializationError(
            val e: Exception,
        ) : Failure()

        data class NetworkError(
            val e: Exception
        ) : Failure()
    }
}