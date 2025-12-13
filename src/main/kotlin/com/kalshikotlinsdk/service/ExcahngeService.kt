package com.kalshikotlinsdk.service

import com.kalshikotlinsdk.KalshiClient
import com.kalshikotlinsdk.auth.ApiRequestType
import com.kalshikotlinsdk.model.ExchangeResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.util.appendAll

class ExchangeService(val kClient: KalshiClient) {
    private val exchangeStatusPath = "/exchange/status"
    private val exchangeAnnouncementsPath = "/exchange/announcements"
    private val seriesFeePath = "/series/fee_changes"
    private val exchangeSchedulePath = "/exchange/schedule"
    private val userDataTimestampPath = "/exchange/user_data_timestamp"

    suspend fun exchangeStatus(): ExchangeResponse.ExchangeStatus {
        val res: ExchangeResponse.ExchangeStatus =
            kClient.client
                .get(kClient.baseUrl + exchangeStatusPath) {
                    headers.appendAll(kClient.getHeader(ApiRequestType.GET))
                }
                .body()
        return res
    }

    suspend fun exchangeAnnouncements(): ExchangeResponse.Announcements{
        val res: ExchangeResponse.Announcements =
            kClient.client
                .get(kClient.baseUrl + exchangeAnnouncementsPath) {
                    headers.appendAll(kClient.getHeader(ApiRequestType.GET))
                }
                .body()
        return res
    }


    suspend fun seriesFeeChange(seriesTicker: String = "", showHistorical: Boolean = false): ExchangeResponse.SeriesFeeChanges{
        val res: ExchangeResponse.SeriesFeeChanges =
            kClient.client
                .get(kClient.baseUrl + seriesFeePath) {
                    headers.appendAll(kClient.getHeader(ApiRequestType.GET))
                    parameter("series_ticker", seriesTicker)
                    parameter("show_historical", showHistorical)
                }
                .body()
        return res
    }

    suspend fun exchangeSchedule(): ExchangeResponse.ExchangeSchedule{
        val res: ExchangeResponse.ExchangeSchedule =
            kClient.client
                .get(kClient.baseUrl + exchangeSchedulePath) {
                    headers.appendAll(kClient.getHeader(ApiRequestType.GET))
                }
                .body()
        return res
    }


    suspend fun userDataTimeStamp(): ExchangeResponse.LastUserDataUpdate{
        val res: ExchangeResponse.LastUserDataUpdate =
            kClient.client
                .get(kClient.baseUrl + userDataTimestampPath) {
                    headers.appendAll(kClient.getHeader(ApiRequestType.GET))
                }
                .body()
        return res
    }
}
