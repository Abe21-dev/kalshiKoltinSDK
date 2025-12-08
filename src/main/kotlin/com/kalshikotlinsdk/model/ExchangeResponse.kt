package com.kalshikotlinsdk.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.serialization.*

@Serializable
sealed class ExchangeResponse {
  @OptIn(ExperimentalTime::class)
  @Serializable
  data class ExchangeStatus(
      @SerialName("exchange_active") val exchangeActive: Boolean,
      @SerialName("trading_active") val tradingActive: Boolean,
      @SerialName("exchange_estimated_resume_time") val exchangeEstimatedResumeTime: Instant?,
  ) : ExchangeResponse()
}
