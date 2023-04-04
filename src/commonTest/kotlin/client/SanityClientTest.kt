package client

import io.polywrap.client.PolywrapClient
import io.polywrap.configBuilder.ClientConfigBuilder
import io.polywrap.core.resolution.Uri
import io.polywrap.core.types.InvokeOptions
import io.polywrap.msgpack.msgPackEncode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertNull

class SanityClientTest {

    private val sha3Uri = Uri("ipfs/QmThRxFfr7Hj9Mq6WmcGXjkRrgqMG3oD93SLX27tinQWy5")

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun tryResolveUri() = runTest {
        val config = ClientConfigBuilder().addDefaults().build()
        val client = PolywrapClient(config)
        val result = client.tryResolveUri(uri = sha3Uri).await()

        assertNull(result.exceptionOrNull())
        println(result.getOrThrow())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun invoke() = runTest {
        val config = ClientConfigBuilder().addDefaults().build()
        val client = PolywrapClient(config)
        val result = client.invoke(
            InvokeOptions(
                uri = sha3Uri,
                method = "keccak_256",
                args = msgPackEncode(mapOf("message" to "Hello World!"))
            )
        ).await()
        assertNull(result.exceptionOrNull())
        println(result.getOrThrow())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun invokeWithMapStringAnyArgs() = runTest {
        val config = ClientConfigBuilder().addDefaults().build()
        val client = PolywrapClient(config)
        val result = client.invoke<String>(
            uri = sha3Uri,
            method = "keccak_256",
            args = mapOf("message" to "Hello World!")
        ).await()
        assertNull(result.exceptionOrNull())
        println(result.getOrThrow())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun invokeWithReifiedTypes() = runTest {
        @Serializable
        data class MethodArgs(
            val firstKey: String,
            val secondKey: String
        )
        val config = ClientConfigBuilder().addDefaults().build()
        val client = PolywrapClient(config)
        val result = client.invoke<MethodArgs, String>(
            uri = sha3Uri,
            method = "keccak_256",
            args = MethodArgs("firstValue", "secondValue")
        ).await()
        assertNull(result.exceptionOrNull())
        println(result.getOrThrow())
    }
}
