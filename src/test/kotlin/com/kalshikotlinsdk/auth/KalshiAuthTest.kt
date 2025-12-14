package com.kalshikotlinsdk.auth

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import java.security.PrivateKey
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class KalshiAuthTest {

    lateinit var underTest: KalshiAuth
    val fakePrivateKey = File("src/test/kotlin/com/kalshikotlinsdk/private_key_test.pem").readText()
    val fakeApiKeyId = "fakeApiKeyId"
    val keyList = listOf("KALSHI-ACCESS-KEY", "KALSHI-ACCESS-SIGNATURE", "KALSHI-ACCESS-TIMESTAMP")

    @MockK
    lateinit var mockTimeProvider: TimeProvider
    @MockK
    lateinit var mockSignatureCreator: SignatureCreator
    @MockK
    lateinit var mockPrivateKeyLoader: PrivateKeyLoader
    @MockK
    lateinit var mockPrivateKey: PrivateKey


    @BeforeEach
    fun setup(){
        MockKAnnotations.init(this, relaxUnitFun = true)
        every { mockPrivateKeyLoader.loadPrivateKey(any<String>())} returns mockPrivateKey
        every { mockTimeProvider.currentTimeMillis() } returns 0L
        every { mockSignatureCreator.createSignature(any<PrivateKey>(), any<Long>(), any<String>(),any<String>()) } returns ""
        underTest = KalshiAuth("fakePrivateKey", fakeApiKeyId, mockPrivateKeyLoader, mockSignatureCreator, mockTimeProvider)
    }

    @Test
    fun `getApiHeader happy path to get header`() {
        val header = underTest.getApiHeader(ApiRequestType.GET, "somepath")
        verify { mockTimeProvider.currentTimeMillis() }
        verify { mockSignatureCreator.createSignature(any<PrivateKey>(), any<Long>(), any<String>(),any<String>()) }
        assertTrue { header.keys.containsAll(keyList) }
        assertEquals(3, header.size)
    }
}