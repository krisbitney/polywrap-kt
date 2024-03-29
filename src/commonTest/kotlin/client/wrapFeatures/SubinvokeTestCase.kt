package client.wrapFeatures

import io.github.krisbitney.client.PolywrapClient
import io.github.krisbitney.configBuilder.ConfigBuilder
import io.github.krisbitney.core.resolution.Uri
import pathToTestWrappers
import kotlin.test.Test
import kotlin.test.assertEquals

class SubinvokeTestCase {
    @Test
    fun testSubinvoke() {
        val subinvokeUri = "fs/$pathToTestWrappers/subinvoke/00-subinvoke/implementations/rs"
        val wrapperUri = "fs/$pathToTestWrappers/subinvoke/01-invoke/implementations/rs"

        val config = ConfigBuilder()
            .addDefaults()
            .addRedirect("ens/imported-subinvoke.eth" to subinvokeUri)
            .build()
        val client = PolywrapClient(config)

        val result = client.invoke<Int>(
            uri = Uri(wrapperUri),
            method = "addAndIncrement",
            args = mapOf("a" to 1, "b" to 1)
        )
        if (result.isFailure) {
            throw result.exceptionOrNull()!!
        }
        assertEquals(3, result.getOrThrow())
    }
}