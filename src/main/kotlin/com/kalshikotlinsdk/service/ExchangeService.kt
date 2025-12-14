package com.kalshikotlinsdk.service

import com.kalshikotlinsdk.KalshiClient
import com.kalshikotlinsdk.auth.ApiRequestType
import com.kalshikotlinsdk.model.ExchangeResponse
import com.kalshikotlinsdk.model.KalshiResult
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.util.appendAll

class ExchangeService(val kClient: KalshiClient) {
    companion object {
        const val EXCHANGE_STATUS_PATH = "/exchange/status"
        const val EXCHANGE_ANNOUNCEMENTS_PATH = "/exchange/announcements"
        const val SERIES_FEE_PATH = "/series/fee_changes"
        const val EXCHANGE_SCHEDULE_PATH = "/exchange/schedule"
        const val USER_DATA_TIMESTAMP_PATH = "/exchange/user_data_timestamp"
    }

    suspend fun exchangeStatus(): KalshiResult<ExchangeResponse.ExchangeStatus> {
        return try {
            val res: ExchangeResponse.ExchangeStatus =
                kClient.client
                    .get(kClient.baseUrl + EXCHANGE_STATUS_PATH) {
                        headers.appendAll(kClient.getHeader(ApiRequestType.GET))
                    }
                    .body()
            KalshiResult.Success(res)
        } catch (e: Exception) {
            kClient.handleExceptionType(e)
        }
    }

    suspend fun exchangeAnnouncements(): KalshiResult<ExchangeResponse.Announcements> {
        return try {
            val res: ExchangeResponse.Announcements =
                kClient.client
                    .get(kClient.baseUrl + EXCHANGE_ANNOUNCEMENTS_PATH) {
                        headers.appendAll(kClient.getHeader(ApiRequestType.GET))
                    }
                    .body()
            KalshiResult.Success(res)
        } catch (e: Exception) {
            kClient.handleExceptionType(e)
        }
    }

    suspend fun seriesFeeChange(
        seriesTicker: String = "",
        showHistorical: Boolean = false,
    ): KalshiResult<ExchangeResponse.SeriesFeeChanges> {
        return try {
            val res: ExchangeResponse.SeriesFeeChanges =
                kClient.client
                    .get(kClient.baseUrl + SERIES_FEE_PATH) {
                        headers.appendAll(kClient.getHeader(ApiRequestType.GET))
                        parameter("series_ticker", seriesTicker)
                        parameter("show_historical", showHistorical)
                    }
                    .body()
            KalshiResult.Success(res)
        } catch (e: Exception) {
            kClient.handleExceptionType(e)
        }
    }

    suspend fun exchangeSchedule(): KalshiResult<ExchangeResponse.ExchangeSchedule> {
        return try {
            val res: ExchangeResponse.ExchangeSchedule =
                kClient.client
                    .get(kClient.baseUrl +EXCHANGE_SCHEDULE_PATH) {
                        headers.appendAll(kClient.getHeader(ApiRequestType.GET))
                    }
                    .body()
            KalshiResult.Success(res)
        } catch (e: Exception) {
            kClient.handleExceptionType(e)
        }
    }

    suspend fun userDataTimeStamp(): KalshiResult<ExchangeResponse.LastUserDataUpdate> {
        return try {
            val res: ExchangeResponse.LastUserDataUpdate =
                kClient.client
                    .get(kClient.baseUrl + USER_DATA_TIMESTAMP_PATH) {
                        headers.appendAll(kClient.getHeader(ApiRequestType.GET))
                    }
                    .body()
            KalshiResult.Success(res)
        } catch (e: Exception) {
            kClient.handleExceptionType(e)
        }
    }
}