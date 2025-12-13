package com.kalshikotlinsdk.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.serialization.*

@OptIn(ExperimentalTime::class)
@Serializable
sealed class ExchangeResponse {

    @Serializable
    data class LastUserDataUpdate(
        @SerialName("as_of_time") val userDataTimestamp: Instant?
    ) : ExchangeResponse()


    @Serializable
    data class ExchangeStatus(
        @SerialName("exchange_active") val exchangeActive: Boolean,
        @SerialName("trading_active") val tradingActive: Boolean,
        @SerialName("exchange_estimated_resume_time") val exchangeEstimatedResumeTime: Instant?,
    ) : ExchangeResponse()

    @Serializable
    data class ExchangeAnnouncement(
        val type: String,
        val message: String,
        @SerialName("delivery_time") val deliveryTime: Instant?,
        val status: CurrentExchangeStatus
    )
    @Serializable
    data class Announcements(
        val announcements: List<ExchangeAnnouncement>
    ): ExchangeResponse()

    @Serializable
    data class SeriesFeeChanges(
        @SerialName("series_fee_change_arr")
        val seriesFeeChangeArr: List<seriesFeeChange>
    ) : ExchangeResponse()

    @Serializable
    data class seriesFeeChange(
        val id: String,
        @SerialName("series_ticker") val seriesTicker: String,
        @SerialName("fee_type") val feeType: FeeTypes,
        @SerialName("fee_multiplier") val feeMultiplier: Float,
        @SerialName("scheduled_ts") val scheduleTs: Instant?
    )

    @OptIn(ExperimentalTime::class)
    @Serializable
    data class ExchangeSchedule(
        val schedule: Schedule
    ) : ExchangeResponse()

    @OptIn(ExperimentalTime::class)
    @Serializable
    data class Schedule(
        @SerialName("standard_hours")
        val standardHours: List<StandardHours>,
        @SerialName("maintenance_windows")
        val maintenanceWindows: List<MaintenanceWindow>
    )

    @OptIn(ExperimentalTime::class)
    @Serializable
    data class StandardHours(
        @SerialName("start_time")
        val startTime: Instant,
        @SerialName("end_time")
        val endTime: Instant,
        val monday: List<TimeWindow>,
        val tuesday: List<TimeWindow>,
        val wednesday: List<TimeWindow>,
        val thursday: List<TimeWindow>,
        val friday: List<TimeWindow>,
        val saturday: List<TimeWindow>,
        val sunday: List<TimeWindow>
    )

    @Serializable
    data class TimeWindow(
        @SerialName("open_time")
        val openTime: String,
        @SerialName("close_time")
        val closeTime: String
    )

    @OptIn(ExperimentalTime::class)
    @Serializable
    data class MaintenanceWindow(
        @SerialName("start_datetime")
        val startDatetime: Instant,
        @SerialName("end_datetime")
        val endDatetime: Instant
    )


}

@Serializable
enum class CurrentExchangeStatus{
    @SerialName("active")
    ACTIVE,
    @SerialName("inactive")
    INACTIVE
}


@Serializable
enum class FeeTypes{
    @SerialName("quadratic")
    QUADRATIC,
    @SerialName("quadratic_with_maker_fees")
    QUADRATIC_MAKER_FEE,
    @SerialName("flat")
    FLAT
}
