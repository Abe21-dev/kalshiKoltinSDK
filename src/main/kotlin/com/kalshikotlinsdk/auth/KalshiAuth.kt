package com.kalshikotlinsdk.auth

import java.nio.charset.StandardCharsets
import java.security.PrivateKey
import java.security.Security
import java.security.Signature
import java.security.spec.MGF1ParameterSpec
import java.security.spec.PSSParameterSpec
import java.util.Base64
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMKeyPair
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter

enum class ApiRequestType {
    GET,
    POST,
}

interface PrivateKeyLoader {
    fun loadPrivateKey(privateApiKey: String): PrivateKey
}

interface TimeProvider {
    fun currentTimeMillis(): Long
}

interface SignatureCreator {
    fun createSignature(
        privateKey: PrivateKey,
        timeStamp: Long,
        method: String,
        path: String,
    ): String
}

class BouncyCastlePrivateKeyLoader : PrivateKeyLoader {
    override fun loadPrivateKey(privateApiKey: String): PrivateKey {
        val pemParser = PEMParser(privateApiKey.reader())
        val converter = JcaPEMKeyConverter().setProvider("BC")
        val obj = pemParser.readObject()
        if (obj is PEMKeyPair) {
            return converter.getKeyPair(obj).private
        }
        throw IllegalArgumentException(
            "Parsed object is not a PEMKeyPair it is: ${obj::class.java ?: null}"
        )
    }
}

class PSSSignatureCreator : SignatureCreator {
    override fun createSignature(
        privateKey: PrivateKey,
        timeStamp: Long,
        method: String,
        path: String,
    ): String {
        val pathWithoutQuery = path.split("?")[0]
        val message = "$timeStamp$method$pathWithoutQuery"

        // Explicitly configure PSS Parameters to match Python
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
}

class SystemTimeProvider : TimeProvider {
    override fun currentTimeMillis(): Long = System.currentTimeMillis()
}

class KalshiAuth(
    val privateApiKey: String, // Required first
    val apiKeyId: String, // Required second
    @Suppress("UNUSED_PARAMETER")
    private val keyLoader: PrivateKeyLoader = BouncyCastlePrivateKeyLoader(),
    private val signatureCreator: SignatureCreator = PSSSignatureCreator(),
    private val timeProvider: TimeProvider = SystemTimeProvider(),
) {
    init {
        Security.addProvider(BouncyCastleProvider())
    }

    internal val privateKey = keyLoader.loadPrivateKey(privateApiKey)

    internal fun createSignature(
        privateKey: PrivateKey,
        timeStamp: Long,
        method: String,
        path: String,
    ): String {
        val pathWithoutQuery = path.split("?")[0]
        val message = "$timeStamp$method$pathWithoutQuery"

        // Explicitly configure PSS Parameters to match Python
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

    fun getApiHeader(apiRequestType: ApiRequestType, path: String): Map<String, String> {
        val timestamp = timeProvider.currentTimeMillis()
        val signature =
            signatureCreator.createSignature(privateKey, timestamp, apiRequestType.toString(), path)
        val headers =
            mapOf(
                "KALSHI-ACCESS-KEY" to apiKeyId,
                "KALSHI-ACCESS-SIGNATURE" to signature,
                "KALSHI-ACCESS-TIMESTAMP" to timestamp.toString(),
            )
        return headers
    }
}
