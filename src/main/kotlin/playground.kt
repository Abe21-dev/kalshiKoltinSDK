
import java.io.File
import java.security.KeyFactory
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.Signature
import java.security.interfaces.RSAPrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.delay


const val FILE_PATH = "src/keys/private_key.txt"
const val API_KEY = "7e456ed4-ef02-4f81-b0d0-df4094ebac1f"

fun loadPrivateKey() : RSAPrivateKey{
    // read the file as a string
    val keyString = File("sample.txt").readText()
    val privateKeyPEM = keyString
        .replace("-----BEGIN RSA PRIVATE KEY-----", "")
        .replace(System.lineSeparator(), "")
        .replace("-----END RSA PRIVATE KEY-----", "")


    val decoded = Base64.getDecoder().decode(privateKeyPEM)

    val keyFactory = KeyFactory.getInstance("RSA")
    val keySpec = PKCS8EncodedKeySpec(decoded)
    return keyFactory.generatePrivate(keySpec) as RSAPrivateKey
}

fun createSignature(privateKey: PrivateKey, timeStamp: Long, method: String, path: String) : String{
    val pathWithoutQuery = path.split("/")[0]
    val message = "$timeStamp$method$pathWithoutQuery"

    val digest = MessageDigest.getInstance("SHA-256")
    val hashedData = digest.digest(message.encodeToByteArray())

    val signature: Signature = Signature.getInstance("SHA256withRSA")
    signature.initSign(privateKey)
    signature.update(hashedData)
    val signedHash: ByteArray? = signature.sign()
    return signedHash?.decodeToString() ?: ""
}


suspend fun get(privateKey: PrivateKey, apkKey: String, path: String){
//    val timestamp = System.currentTimeMillis()
//    val signature = createSignature(privateKey, timestamp, "GET", path)
//    println(signature)
    val client = HttpClient(CIO)
    val response: HttpResponse = client.get("https://ktor.io/")
    println(response.status)
    client.close()
}


suspend fun main(){
    val privateKey = loadPrivateKey()
    get(privateKey, API_KEY, "/trade-api/v2/portfolio/balance")
//    delay(100L)
//    println("hey")
}