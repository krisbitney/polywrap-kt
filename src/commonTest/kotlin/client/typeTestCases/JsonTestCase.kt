package client.typeTestCases

import io.github.krisbitney.client.PolywrapClient
import io.github.krisbitney.configBuilder.ConfigBuilder
import io.github.krisbitney.core.resolution.Uri
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import pathToTestWrappers
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@OptIn(ExperimentalCoroutinesApi::class)
class JsonTestCase {

    private val uri = Uri("fs/$pathToTestWrappers/json-type/implementations/rs")
    private val config = ConfigBuilder().addDefaults().build()
    private val client = PolywrapClient(config)

    @Test
    fun testParse() = runTest {
        val value = buildJsonObject {
            put("foo", "bar")
            put("bar", "bar")
        }.toString()
        val result = client.invoke<String>(
            uri = uri,
            method = "parse",
            args = mapOf("value" to value)
        )
        if (result.isFailure) throw result.exceptionOrNull()!!
        assertEquals(value, result.getOrThrow())
    }

    @Test
    fun testStringify() = runTest {
        val values = arrayOf(
            buildJsonObject { put("bar", "foo") }.toString(),
            buildJsonObject { put("baz", "fuz") }.toString()
        )
        val result = client.invoke<String>(
            uri = uri,
            method = "stringify",
            args = mapOf("values" to values)
        )
        if (result.isFailure) throw result.exceptionOrNull()!!
        assertEquals(values.joinToString(""), result.getOrThrow())
    }

    @Test
    fun testStringifyObject() = runTest {
        val value = mapOf(
            "jsonA" to buildJsonObject { put("foo", "bar") }.toString(),
            "jsonB" to buildJsonObject { put("fuz", "baz") }.toString()
        )
        val result = client.invoke<String>(
            uri = uri,
            method = "stringifyObject",
            args = mapOf("object" to value)
        )
        if (result.isFailure) throw result.exceptionOrNull()!!
        assertEquals(
            value["jsonA"] + value["jsonB"],
            result.getOrThrow()
        )
    }

    @Test
    fun testMethodJSON() = runTest {
        val value = mapOf(
            "valueA" to 5,
            "valueB" to "foo",
            "valueC" to true,
        )
        val result = client.invoke<String>(
                uri = uri,
                method = "methodJSON",
                args = value
            )
        if (result.isFailure) throw result.exceptionOrNull()!!

        val expected = buildJsonObject {
            put("valueA", 5)
            put("valueB", "foo")
            put("valueC", true)
        }.toString()
        assertEquals(expected, result.getOrThrow())
    }
}
