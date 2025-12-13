package com.kalshikotlinsdk

import com.kalshikotlinsdk.service.ExchangeService
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.request.invoke
import io.ktor.client.statement.*
import java.io.File
import java.nio.charset.StandardCharsets
import java.security.PrivateKey
import java.security.Signature
import java.security.spec.MGF1ParameterSpec
import java.security.spec.PSSParameterSpec
import java.time.format.DateTimeFormatter.*
import java.util.*
import org.bouncycastle.openssl.PEMKeyPair
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter

val FILE_PATH = "src/main/kotlin/keys/private_key.pem"
// FIX 1: Add the leading slash to match the Python path input
const val URL_PATH = "/trade-api/v2/portfolio/balance"
const val API_KEY = "8b52c74c-468c-440e-836c-96cb78ba2b91"
const val BASE_URL =
    "https://api.elections.kalshi.com" // Removed trailing slash here to avoid double slash later

fun loadPrivateKey(filePath: String): PrivateKey {
    val privateKeyStrign = File(filePath).readText()
    val pemParser = PEMParser(privateKeyStrign.reader())
    val converter = JcaPEMKeyConverter().setProvider("BC")
    val obj = pemParser.readObject()
    // Handle case where PEM might be encrypted or purely a key pair
    if (obj is PEMKeyPair) {
        return converter.getKeyPair(obj).private
    }
    throw IllegalArgumentException("Parsed object is not a PEMKeyPair: ${obj::class.java}")
}

fun createSignature(privateKey: PrivateKey, timeStamp: Long, method: String, path: String): String {
    val pathWithoutQuery = path.split("?")[0]
    val message = "$timeStamp$method$pathWithoutQuery"

    // FIX 2: Explicitly configure PSS Parameters to match Python
    val signer = Signature.getInstance("SHA256withRSA/PSS", "BC")

    // Python's padding.PSS.DIGEST_LENGTH means salt length = hash length (32 for SHA256)
    val params =
        PSSParameterSpec(
            "SHA-256",
            "MGF1",
            MGF1ParameterSpec.SHA256,
            32, // Salt length must be 32 bytes
            1, // Trailer field (usually 1)
        )

    signer.setParameter(params)
    signer.initSign(privateKey)
    signer.update(message.toByteArray(StandardCharsets.UTF_8))

    return Base64.getEncoder().encodeToString(signer.sign())
}

suspend fun get(privateKey: PrivateKey, apkKey: String, path: String) {
    val timestamp = System.currentTimeMillis()
    val signature = createSignature(privateKey, timestamp, "GET", path)

    println("Requesting: $BASE_URL$path")
    println("Signature payload: $timestamp" + "GET" + path)

    val client = HttpClient(CIO)
    try {
        val response: HttpResponse =
            client.get(BASE_URL + path) {
                headers {
                    append("KALSHI-ACCESS-KEY", API_KEY)
                    append("KALSHI-ACCESS-SIGNATURE", signature)
                    append("KALSHI-ACCESS-TIMESTAMP", timestamp.toString())
                }
            }
        println("Status: ${response.status}")
        println("Body: ${response.body<String>()}")
    } catch (e: Exception) {
        println("Error: ${e.message}")
    } finally {
        client.close()
    }
}

suspend fun main() {

    val client = KalshiClient(CIO.create(),"src/main/kotlin/keys/private_key.pem", API_KEY)
    val exchangeService = ExchangeService(client)
//    println(exchangeService.exchangeStatus())
//    println(exchangeService.exchangeAnnouncements())
//    println(exchangeService.seriesFeeChange(showHistorical = true))
//    print(exchangeService.exchangeSchedule())
    print(exchangeService.userDataTimeStamp())
}


