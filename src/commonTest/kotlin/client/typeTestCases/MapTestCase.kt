package client.typeTestCases

import io.github.krisbitney.client.PolywrapClient
import io.github.krisbitney.configBuilder.ConfigBuilder
import io.github.krisbitney.core.resolution.Uri
import io.github.krisbitney.core.msgpack.MsgPackMap
import io.github.krisbitney.core.msgpack.toMsgPackMap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import pathToTestWrappers

@OptIn(ExperimentalCoroutinesApi::class)
class MapTestCase {

    private val uri = Uri("fs/$pathToTestWrappers/map-type/implementations/rs")
    private val config = ConfigBuilder().addDefaults().build()
    private val client = PolywrapClient(config)

    @Serializable
    private data class CustomMap(
        val map: MsgPackMap<String, Int>,
        val nestedMap: MsgPackMap<String, MsgPackMap<String, Int>>
    )

    @Test
    fun testReturnMap() = runTest {
        @Serializable
        data class ArgsReturnMap(val map: MsgPackMap<String, Int>)

        val mapClass = mapOf("Hello" to 1, "Heyo" to 50).toMsgPackMap()
        val result = client.invoke<ArgsReturnMap, MsgPackMap<String, Int>>(
            uri = uri,
            method = "returnMap",
            args = ArgsReturnMap(mapClass)
        )
        if (result.isFailure) throw result.exceptionOrNull()!!
        assertEquals(mapClass, result.getOrThrow())
    }

    @Test
    fun testGetKey() = runTest {
        val mapClass = mapOf("Hello" to 1, "Heyo" to 50).toMsgPackMap()
        val nestedMapClass = mapOf("Nested" to mapClass).toMsgPackMap()
        val result = client.invoke<Int>(
            uri = uri,
            method = "getKey",
            args = mapOf(
                "foo" to CustomMap(mapClass, nestedMapClass),
                "key" to "Hello"
            )
        )
        if (result.isFailure) throw result.exceptionOrNull()!!
        assertEquals(mapClass.map["Hello"], result.getOrThrow())
    }

    @Test
    fun testReturnCustomMap() = runTest {
        val mapClass = mapOf("Hello" to 1, "Heyo" to 50).toMsgPackMap()
        val nestedMapClass = mapOf("Nested" to mapClass).toMsgPackMap()
        val result = client.invoke<CustomMap>(
            uri = uri,
            method = "returnCustomMap",
            args = mapOf("foo" to CustomMap(mapClass,  nestedMapClass))
        )
        if (result.isFailure) throw result.exceptionOrNull()!!
        assertEquals(CustomMap(mapClass, nestedMapClass), result.getOrThrow())
    }

    @Test
    fun testReturnNestedMap() = runTest {
        @Serializable
        data class ArgsReturnNestedMap(val foo: MsgPackMap<String, MsgPackMap<String, Int>>)

        val mapClass = mapOf("Hello" to 1, "Heyo" to 50).toMsgPackMap()
        val nestedMapClass = mapOf("Nested" to mapClass).toMsgPackMap()
        val result = client.invoke<ArgsReturnNestedMap, MsgPackMap<String, MsgPackMap<String, Int>>>(
            uri = uri,
            method = "returnNestedMap",
            args = ArgsReturnNestedMap(nestedMapClass)
        )
        if (result.isFailure) throw result.exceptionOrNull()!!
        assertEquals(nestedMapClass, result.getOrThrow())
    }
}
