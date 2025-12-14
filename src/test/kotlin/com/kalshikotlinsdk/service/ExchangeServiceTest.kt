package com.kalshikotlinsdk.service

import com.kalshikotlinsdk.KalshiClient
import com.kalshikotlinsdk.model.KalshiResult
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class ExchangeServiceTest {

    lateinit var underTest: ExchangeService
    val testPrivateKeyPath = "src/test/kotlin/com/kalshikotlinsdk/private_key_test.pem"
    val fakeApiId = "af9e5f23-e42d-4923-8bf0-4021b6329891"

    @BeforeEach
    fun setup() {
    }

    // ==================== exchangeStatus Tests ====================

    @Test
    fun `exchangeStatus with successful response returns Success`() {
        runBlocking {
            val mockEngine = MockEngine { request ->
                respond(
                    content = ByteReadChannel(
                        """
                        {
                            "exchange_active": true,
                            "trading_active": true,
                            "exchange_estimated_resume_time": null
                        }
                        """.trimIndent()
                    ),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
            val apiClient = KalshiClient(mockEngine, testPrivateKeyPath, fakeApiId)
            underTest = ExchangeService(apiClient)

            val result = underTest.exchangeStatus()

            assertTrue(result is KalshiResult.Success)
            assertEquals(true, result.data.exchangeActive)
            assertEquals(true, result.data.tradingActive)
            assertEquals(null, result.data.exchangeEstimatedResumeTime)
        }
    }

    @Test
    fun `exchangeStatus with HTTP error returns HttpError`() {
        runBlocking {
            val mockEngine = MockEngine { request ->
                respond(
                    content = ByteReadChannel("""{"error": "Not Found"}"""),
                    status = HttpStatusCode.NotFound,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
            val apiClient = KalshiClient(mockEngine, testPrivateKeyPath, fakeApiId)
            underTest = ExchangeService(apiClient)

            val result = underTest.exchangeStatus()

            assertTrue(result is KalshiResult.Failure.HttpError)
            assertEquals(404, result.code)
        }
    }

    @Test
    fun `exchangeStatus with malformed JSON returns SerializationError`() {
        runBlocking {
            val mockEngine = MockEngine { request ->
                respond(
                    content = ByteReadChannel("""{"invalid_field": "invalid"}"""),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
            val apiClient = KalshiClient(mockEngine, testPrivateKeyPath, fakeApiId)
            underTest = ExchangeService(apiClient)

            val result = underTest.exchangeStatus()
            println(result)
            assertTrue(result is KalshiResult.Failure.SerializationError)
        }
    }

    // ==================== exchangeAnnouncements Tests ====================

    @Test
    fun `exchangeAnnouncements with successful response returns Success`() {
        runBlocking {
            val mockEngine = MockEngine { request ->
                respond(
                    content = ByteReadChannel(
                        """
                        {
                            "announcements": [
                                {
                                    "type": "maintenance",
                                    "message": "Scheduled maintenance tonight",
                                    "delivery_time": null,
                                    "status": "active"
                                },
                                {
                                    "type": "update",
                                    "message": "New features released",
                                    "delivery_time": null,
                                    "status": "inactive"
                                }
                            ]
                        }
                        """.trimIndent()
                    ),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
            val apiClient = KalshiClient(mockEngine, testPrivateKeyPath, fakeApiId)
            underTest = ExchangeService(apiClient)

            val result = underTest.exchangeAnnouncements()

            assertTrue(result is KalshiResult.Success)
            assertEquals(2, result.data.announcements.size)
            assertEquals("maintenance", result.data.announcements[0].type)
            assertEquals("Scheduled maintenance tonight", result.data.announcements[0].message)
        }
    }

    @Test
    fun `exchangeAnnouncements with HTTP error returns HttpError`() {
        runBlocking {
            val mockEngine = MockEngine { request ->
                respond(
                    content = ByteReadChannel("""{"error": "Internal Server Error"}"""),
                    status = HttpStatusCode.InternalServerError,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
            val apiClient = KalshiClient(mockEngine, testPrivateKeyPath, fakeApiId)
            underTest = ExchangeService(apiClient)

            val result = underTest.exchangeAnnouncements()

            assertTrue(result is KalshiResult.Failure.HttpError)
            assertEquals(500, result.code)
        }
    }

    @Test
    fun `exchangeAnnouncements with malformed JSON returns SerializationError`() {
        runBlocking {
            val mockEngine = MockEngine { request ->
                respond(
                    content = ByteReadChannel("""{"announcements": "not_an_array"}"""),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
            val apiClient = KalshiClient(mockEngine, testPrivateKeyPath, fakeApiId)
            underTest = ExchangeService(apiClient)

            val result = underTest.exchangeAnnouncements()

            assertTrue(result is KalshiResult.Failure.SerializationError)
        }
    }

    // ==================== seriesFeeChange Tests ====================

    @Test
    fun `seriesFeeChange with successful response returns Success`() {
        runBlocking {
            val mockEngine = MockEngine { request ->
                respond(
                    content = ByteReadChannel(
                        """
                        {
                            "series_fee_change_arr": [
                                {
                                    "id": "fee-001",
                                    "series_ticker": "SERIES-001",
                                    "fee_type": "quadratic",
                                    "fee_multiplier": 1.5,
                                    "scheduled_ts": null
                                }
                            ]
                        }
                        """.trimIndent()
                    ),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
            val apiClient = KalshiClient(mockEngine, testPrivateKeyPath, fakeApiId)
            underTest = ExchangeService(apiClient)

            val result = underTest.seriesFeeChange("SERIES-001", false)

            assertTrue(result is KalshiResult.Success)
            assertEquals(1, result.data.seriesFeeChangeArr.size)
            assertEquals("fee-001", result.data.seriesFeeChangeArr[0].id)
            assertEquals("SERIES-001", result.data.seriesFeeChangeArr[0].seriesTicker)
            assertEquals(1.5f, result.data.seriesFeeChangeArr[0].feeMultiplier)
        }
    }

    @Test
    fun `seriesFeeChange with empty ticker and showHistorical true returns Success`() {
        runBlocking {
            val mockEngine = MockEngine { request ->
                assertTrue(request.url.parameters.contains("show_historical"))
                assertEquals("true", request.url.parameters["show_historical"])
                
                respond(
                    content = ByteReadChannel(
                        """
                        {
                            "series_fee_change_arr": []
                        }
                        """.trimIndent()
                    ),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
            val apiClient = KalshiClient(mockEngine, testPrivateKeyPath, fakeApiId)
            underTest = ExchangeService(apiClient)

            val result = underTest.seriesFeeChange("", true)

            assertTrue(result is KalshiResult.Success)
            assertEquals(0, result.data.seriesFeeChangeArr.size)
        }
    }

    @Test
    fun `seriesFeeChange with HTTP error returns HttpError`() {
        runBlocking {
            val mockEngine = MockEngine { request ->
                respond(
                    content = ByteReadChannel("""{"error": "Bad Request"}"""),
                    status = HttpStatusCode.BadRequest,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
            val apiClient = KalshiClient(mockEngine, testPrivateKeyPath, fakeApiId)
            underTest = ExchangeService(apiClient)

            val result = underTest.seriesFeeChange()

            assertTrue(result is KalshiResult.Failure.HttpError)
            assertEquals(400, result.code)
        }
    }

    @Test
    fun `seriesFeeChange with malformed JSON returns SerializationError`() {
        runBlocking {
            val mockEngine = MockEngine { request ->
                respond(
                    content = ByteReadChannel("""{"series_fee_change_arr": "invalid"}"""),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
            val apiClient = KalshiClient(mockEngine, testPrivateKeyPath, fakeApiId)
            underTest = ExchangeService(apiClient)

            val result = underTest.seriesFeeChange()

            assertTrue(result is KalshiResult.Failure.SerializationError)
        }
    }

    // ==================== exchangeSchedule Tests ====================

    @Test
    fun `exchangeSchedule with successful response returns Success`() {
        runBlocking {
            val mockEngine = MockEngine { request ->
                respond(
                    content = ByteReadChannel(
                        """
                        {
                            "schedule": {
                                "standard_hours": [
                                    {
                                        "start_time": "2024-01-01T00:00:00Z",
                                        "end_time": "2024-12-31T23:59:59Z",
                                        "monday": [
                                            {
                                                "open_time": "09:00",
                                                "close_time": "17:00"
                                            }
                                        ],
                                        "tuesday": [],
                                        "wednesday": [],
                                        "thursday": [],
                                        "friday": [],
                                        "saturday": [],
                                        "sunday": []
                                    }
                                ],
                                "maintenance_windows": [
                                    {
                                        "start_datetime": "2024-06-01T00:00:00Z",
                                        "end_datetime": "2024-06-01T04:00:00Z"
                                    }
                                ]
                            }
                        }
                        """.trimIndent()
                    ),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
            val apiClient = KalshiClient(mockEngine, testPrivateKeyPath, fakeApiId)
            underTest = ExchangeService(apiClient)

            val result = underTest.exchangeSchedule()

            assertTrue(result is KalshiResult.Success)
            assertEquals(1, result.data.schedule.standardHours.size)
            assertEquals(1, result.data.schedule.maintenanceWindows.size)
            assertEquals("09:00", result.data.schedule.standardHours[0].monday[0].openTime)
            assertEquals("17:00", result.data.schedule.standardHours[0].monday[0].closeTime)
        }
    }

    @Test
    fun `exchangeSchedule with HTTP error returns HttpError`() {
        runBlocking {
            val mockEngine = MockEngine { request ->
                respond(
                    content = ByteReadChannel("""{"error": "Service Unavailable"}"""),
                    status = HttpStatusCode.ServiceUnavailable,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
            val apiClient = KalshiClient(mockEngine, testPrivateKeyPath, fakeApiId)
            underTest = ExchangeService(apiClient)

            val result = underTest.exchangeSchedule()

            assertTrue(result is KalshiResult.Failure.HttpError)
            assertEquals(503, result.code)
        }
    }

    @Test
    fun `exchangeSchedule with malformed JSON returns SerializationError`() {
        runBlocking {
            val mockEngine = MockEngine { request ->
                respond(
                    content = ByteReadChannel("""{"schedule": "invalid"}"""),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
            val apiClient = KalshiClient(mockEngine, testPrivateKeyPath, fakeApiId)
            underTest = ExchangeService(apiClient)

            val result = underTest.exchangeSchedule()

            assertTrue(result is KalshiResult.Failure.SerializationError)
        }
    }

    // ==================== userDataTimeStamp Tests ====================

    @Test
    fun `userDataTimeStamp with successful response returns Success`() {
        runBlocking {
            val mockEngine = MockEngine { request ->
                respond(
                    content = ByteReadChannel(
                        """
                        {
                            "as_of_time": null
                        }
                        """.trimIndent()
                    ),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
            val apiClient = KalshiClient(mockEngine, testPrivateKeyPath, fakeApiId)
            underTest = ExchangeService(apiClient)

            val result = underTest.userDataTimeStamp()

            assertTrue(result is KalshiResult.Success)
            assertEquals(null, result.data.userDataTimestamp)
        }
    }

    @Test
    fun `userDataTimeStamp with HTTP error returns HttpError`() {
        runBlocking {
            val mockEngine = MockEngine { request ->
                respond(
                    content = ByteReadChannel("""{"error": "Unauthorized"}"""),
                    status = HttpStatusCode.Unauthorized,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
            val apiClient = KalshiClient(mockEngine, testPrivateKeyPath, fakeApiId)
            underTest = ExchangeService(apiClient)

            val result = underTest.userDataTimeStamp()

            assertTrue(result is KalshiResult.Failure.HttpError)
            assertEquals(401, result.code)
        }
    }

    @Test
    fun `userDataTimeStamp with malformed JSON returns SerializationError`() {
        runBlocking {
            val mockEngine = MockEngine { request ->
                respond(
                    content = ByteReadChannel("""{"as_of_time": "not_a_timestamp"}"""),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
            val apiClient = KalshiClient(mockEngine, testPrivateKeyPath, fakeApiId)
            underTest = ExchangeService(apiClient)

            val result = underTest.userDataTimeStamp()

            assertTrue(result is KalshiResult.Failure.SerializationError)
        }
    }
}
