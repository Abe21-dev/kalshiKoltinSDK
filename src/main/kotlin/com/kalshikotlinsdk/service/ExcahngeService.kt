package com.kalshikotlinsdk.service

import com.kalshikotlinsdk.KalshiClient
import com.kalshikotlinsdk.auth.ApiRequestType
import com.kalshikotlinsdk.model.ExchangeResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.util.appendAll

class ExchangeService(val kClient: KalshiClient) {
  private val exchangeStatusPath = "/exchange/status"

  suspend fun getExchangeStatus(): ExchangeResponse.ExchangeStatus {
    val res: ExchangeResponse.ExchangeStatus =
        kClient.client
            .get(kClient.baseUrl + exchangeStatusPath) {
              headers.appendAll(kClient.getHeader(ApiRequestType.GET))
            }
            .body()
    return res
  }
}
