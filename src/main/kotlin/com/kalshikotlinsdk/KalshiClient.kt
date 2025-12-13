package com.kalshikotlinsdk

import com.kalshikotlinsdk.auth.ApiRequestType
import com.kalshikotlinsdk.auth.KalshiAuth
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import java.io.File
import kotlinx.serialization.json.Json

class KalshiClient(engine: HttpClientEngine, val privateKeyPath: String = "", val apiKeyId: String = "") {

    val client by lazy {
        HttpClient(engine) {
            expectSuccess = true
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
