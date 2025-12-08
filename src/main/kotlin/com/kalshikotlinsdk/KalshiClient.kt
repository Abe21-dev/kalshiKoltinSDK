package com.kalshikotlinsdk

import com.kalshikotlinsdk.auth.ApiRequestType
import com.kalshikotlinsdk.auth.KalshiAuth
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json

class KalshiClient(val privateKeyPath: String, val apiKeyId: String) {

  val client by lazy {
    HttpClient(CIO) {
      install(ContentNegotiation) {
        json(
            Json {
              prettyPrint = true
              isLenient = true
            }
        )
      }
    }
  }

  val baseUrl = "https://api.elections.kalshi.com/trade-api/v2"
  private val kalshiAuth = KalshiAuth(File(privateKeyPath).readText(), apiKeyId)

  fun getHeader(requestType: ApiRequestType): Map<String, String> {
    return kalshiAuth.getApiHeader(requestType, "")
  }
}

