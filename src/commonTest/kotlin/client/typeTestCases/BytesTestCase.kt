package client.typeTestCases

import io.github.krisbitney.client.PolywrapClient
import io.github.krisbitney.configBuilder.ConfigBuilder
import io.github.krisbitney.core.resolution.Uri
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import pathToTestWrappers
import kotlin.test.Test
import kotlin.test.assertContentEquals

@OptIn(ExperimentalCoroutinesApi::class)
class BytesTestCase {

    private val uri = Uri("fs/$pathToTestWrappers/bytes-type/implementations/rs")
    private val config = ConfigBuilder().addDefaults().build()
    private val client = PolywrapClient(config)

    @Test
    fun testBytesType() = runTest {
        val result = client.invoke<ByteArray>(
            uri = uri,
            method = "bytesMethod",
            args = mapOf(
                "arg" to mapOf(
                    "prop" to "Argument Value".encodeToByteArray()
                )
            )
        )
        if (result.isFailure) throw result.exceptionOrNull()!!

        val expected = "Argument Value Sanity!".encodeToByteArray()
        assertContentEquals(expected, result.getOrThrow())
    }
}
